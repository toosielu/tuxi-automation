package cn.tuxi.automation.service.impl;

import cn.tuxi.automation.domain.GeneratedImage;
import cn.tuxi.automation.domain.ImageGenerateRequest;
import cn.tuxi.automation.service.ImageGenerationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OpenAiImageGenerationService implements ImageGenerationService {
    private final ObjectMapper objectMapper;
    private final String provider;
    private final String apiKey;
    private final String baseUrl;
    private final String model;
    private final String size;
    private final String outputFormat;
    private final int timeoutSeconds;
    private final HttpClient httpClient;

    public OpenAiImageGenerationService(
            ObjectMapper objectMapper,
            @Value("${tuxi.image.provider:disabled}") String provider,
            @Value("${tuxi.image.api-key:}") String apiKey,
            @Value("${tuxi.image.base-url:https://api.openai.com/v1}") String baseUrl,
            @Value("${tuxi.image.model:gpt-image-1.5}") String model,
            @Value("${tuxi.image.size:1024x1024}") String size,
            @Value("${tuxi.image.output-format:png}") String outputFormat,
            @Value("${tuxi.image.timeout-seconds:120}") int timeoutSeconds
    ) {
        this.objectMapper = objectMapper;
        this.provider = provider;
        this.apiKey = apiKey;
        this.baseUrl = trimTrailingSlash(baseUrl);
        this.model = model;
        this.size = size;
        this.outputFormat = outputFormat;
        this.timeoutSeconds = timeoutSeconds;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(timeoutSeconds))
                .build();
    }

    @Override
    public GeneratedImage generate(ImageGenerateRequest request) {
        if (!supports()) {
            throw new IllegalStateException("Image generation is not configured. Set TUXI_IMAGE_PROVIDER=openai and TUXI_IMAGE_API_KEY.");
        }
        try {
            String finalPrompt = buildPrompt(request);
            String responseBody = callImageApi(finalPrompt);
            String imageBase64 = parseImageBase64(responseBody);
            String mimeType = "image/" + outputFormat;
            return new GeneratedImage(
                    UUID.randomUUID().toString(),
                    finalPrompt,
                    imageBase64,
                    mimeType,
                    "data:" + mimeType + ";base64," + imageBase64,
                    provider,
                    model
            );
        } catch (IOException error) {
            throw new IllegalStateException("Image generation response could not be parsed", error);
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Image generation request was interrupted", error);
        }
    }

    private boolean supports() {
        return "openai".equalsIgnoreCase(provider)
                && apiKey != null
                && !apiKey.isBlank();
    }

    private String callImageApi(String prompt) throws IOException, InterruptedException {
        String body = objectMapper.writeValueAsString(Map.of(
                "model", model,
                "prompt", prompt,
                "n", 1,
                "size", size,
                "output_format", outputFormat
        ));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/images/generations"))
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException("Image generation failed: " + response.statusCode() + " " + response.body());
        }
        return response.body();
    }

    private String parseImageBase64(String responseBody) throws IOException {
        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode image = root.path("data").path(0).path("b64_json");
        if (image.isMissingNode() || image.asText().isBlank()) {
            throw new IllegalStateException("Image generation response did not include b64_json");
        }
        return image.asText();
    }

    private String buildPrompt(ImageGenerateRequest request) {
        return String.join("\n", List.of(
                "生成一张 1:1 小红书爆款内容图片。",
                "赛道：" + text(request.niche(), "小红书虚拟电商"),
                "图片类型：" + text(request.imageType(), "小红书爆款封面"),
                "风格：" + text(request.style(), "高级、极简、真实、可收藏"),
                "核心画面要求：" + text(request.prompt(), ""),
                "画面必须适合中文小红书用户，真实、有停留感，不要像企业海报，不要夸大收益，不要出现二维码、联系方式、平台违规导流信息。",
                "构图建议：白底或浅米色背景，黑白灰为主，小红书红点缀，标题区域清晰，适合手机端浏览。"
        ));
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
