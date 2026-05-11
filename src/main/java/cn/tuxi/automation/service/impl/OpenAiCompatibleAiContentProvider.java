package cn.tuxi.automation.service.impl;

import cn.tuxi.automation.domain.AiGeneratedContent;
import cn.tuxi.automation.domain.GenerateRequest;
import cn.tuxi.automation.service.AiContentProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import static java.util.Map.entry;

@Component
@Order(0)
public class OpenAiCompatibleAiContentProvider implements AiContentProvider {
    private final ObjectMapper objectMapper;
    private final String provider;
    private final String apiKey;
    private final String baseUrl;
    private final String model;
    private final int timeoutSeconds;
    private final HttpClient httpClient;

    public OpenAiCompatibleAiContentProvider(
            ObjectMapper objectMapper,
            @Value("${tuxi.ai.provider:local}") String provider,
            @Value("${tuxi.ai.api-key:}") String apiKey,
            @Value("${tuxi.ai.base-url:https://api.openai.com/v1}") String baseUrl,
            @Value("${tuxi.ai.model:gpt-4o-mini}") String model,
            @Value("${tuxi.ai.timeout-seconds:60}") int timeoutSeconds
    ) {
        this.objectMapper = objectMapper;
        this.provider = provider;
        this.apiKey = apiKey;
        this.baseUrl = trimTrailingSlash(baseUrl);
        this.model = model;
        this.timeoutSeconds = timeoutSeconds;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(timeoutSeconds))
                .build();
    }

    @Override
    public boolean supports() {
        return "openai-compatible".equalsIgnoreCase(provider)
                && apiKey != null
                && !apiKey.isBlank();
    }

    @Override
    public AiGeneratedContent generate(GenerateRequest request) {
        try {
            String response = callModel(request);
            String content = objectMapper.readTree(response)
                    .path("choices")
                    .path(0)
                    .path("message")
                    .path("content")
                    .asText();
            return objectMapper.readValue(extractJson(content), AiGeneratedContent.class);
        } catch (IOException error) {
            throw new IllegalStateException("AI model response could not be parsed", error);
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("AI model request was interrupted", error);
        }
    }

    private String callModel(GenerateRequest request) throws IOException, InterruptedException {
        String body = objectMapper.writeValueAsString(Map.of(
                "model", model,
                "temperature", 0.86,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt()),
                        Map.of("role", "user", "content", userPrompt(request))
                )
        ));
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/chat/completions"))
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException("AI model request failed: " + response.statusCode() + " " + response.body());
        }
        return response.body();
    }

    private String systemPrompt() {
        return """
                你是小红书爆款内容生成专家，专门服务虚拟电商、资料包、知识工具、陪跑营项目。
                你的任务是生成可直接复制使用的小红书引流图文资产。

                必须遵守：
                1. 只输出 JSON，不要解释，不要 Markdown。
                2. 文案像真人经验分享，不要出现“首先、其次、综上、作为AI”等 AI 腔。
                3. 不夸大收益，不承诺暴富，不诱导违规导流。
                4. 每次输出 3 个封面方案、3 条帖子、1 组商品承接、5 条私信话术。
                5. 3 条帖子必须包含 2 条“流量爆款贴”和 1 条“信任成交贴”。
                6. 标题要短，开头要有场景感，正文要具体，CTA 要轻。

                JSON 结构必须完全符合：
                {
                  "covers": [
                    {
                      "coverTitle": "",
                      "coverSubtitle": "",
                      "visualStyle": "",
                      "layoutSuggestion": "",
                      "imagePrompt": "",
                      "reason": ""
                    }
                  ],
                  "posts": [
                    {
                      "postType": "流量爆款贴",
                      "title": "",
                      "hook": "",
                      "content": "",
                      "cta": "",
                      "tags": ["#标签"],
                      "reason": ""
                    }
                  ],
                  "productCopy": {
                    "productTitle": "",
                    "productSubtitle": "",
                    "sellingPoints": [],
                    "deliveryNote": "",
                    "riskNotice": ""
                  },
                  "dmScripts": [
                    {
                      "scene": "",
                      "message": ""
                    }
                  ]
                }
                """;
    }

    private String userPrompt(GenerateRequest request) throws JsonProcessingException {
        JsonNode input = objectMapper.valueToTree(Map.ofEntries(
                entry("sourceType", text(request.sourceType(), "TEXT")),
                entry("sourceText", text(request.sourceText(), "")),
                entry("sourceLink", text(request.sourceLink(), "")),
                entry("imageNotes", text(request.imageNotes(), "")),
                entry("niche", text(request.niche(), "小红书虚拟资料")),
                entry("productName", text(request.productName(), "虚拟资料包")),
                entry("targetUser", text(request.targetUser(), "想做副业的新手")),
                entry("sellingPoints", text(request.sellingPoints(), "低成本、可复制、适合新手、自动发货")),
                entry("goal", text(request.goal(), "引流涨粉")),
                entry("style", text(request.style(), "避坑型")),
                entry("painPoint", text(request.painPoint(), "不知道发什么内容，也不知道怎么承接成交"))
        ));
        return """
                请根据下面用户输入，生成小红书爆款引流内容。

                重点：
                - 优先生成能带来点击、收藏、评论、私信的引流贴。
                - 不是写课程介绍，不是企业宣传，不要像广告。
                - 结合赛道和产品，给出具体可发的封面标题、正文和私信承接。
                - 如果用户给了素材，提炼素材里的具体场景，不要照搬。

                用户输入：
                """ + objectMapper.writeValueAsString(input);
    }

    private String extractJson(String content) {
        String text = content == null ? "" : content.trim();
        if (text.startsWith("```")) {
            text = text.replaceFirst("^```(?:json)?", "").replaceFirst("```$", "").trim();
        }
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start < 0 || end <= start) {
            throw new IllegalStateException("AI model did not return JSON content");
        }
        return text.substring(start, end + 1);
    }

    private String text(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private String trimTrailingSlash(String value) {
        String text = text(value, "https://api.openai.com/v1");
        while (text.endsWith("/")) {
            text = text.substring(0, text.length() - 1);
        }
        return text;
    }
}
