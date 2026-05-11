package cn.tuxi.automation.test.domain;

import cn.tuxi.automation.domain.AiGeneratedContent;
import cn.tuxi.automation.domain.GenerateRequest;
import cn.tuxi.automation.service.impl.OpenAiCompatibleAiContentProvider;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class OpenAiCompatibleAiContentProviderTest {
    private HttpServer server;

    @AfterEach
    void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void callsOpenAiCompatibleChatEndpointAndParsesContentJson() throws Exception {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/chat/completions", exchange -> {
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            assertThat(exchange.getRequestHeaders().getFirst("Authorization")).isEqualTo("Bearer test-key");
            assertThat(requestBody).contains("小红书爆款内容生成专家");
            assertThat(requestBody).contains("AI提示词包");

            byte[] response = chatResponseJson().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });
        server.start();

        OpenAiCompatibleAiContentProvider provider = new OpenAiCompatibleAiContentProvider(
                new ObjectMapper(),
                "openai-compatible",
                "test-key",
                "http://127.0.0.1:" + server.getAddress().getPort(),
                "test-model",
                5
        );

        AiGeneratedContent content = provider.generate(new GenerateRequest(
                "TEXT",
                "想做一条去 AI 味的小红书引流贴",
                null,
                null,
                "AI",
                "AI提示词包",
                "副业新手",
                "低成本、可复制",
                "引流涨粉",
                "避坑型",
                "不知道怎么写得像真人"
        ));

        assertThat(content.covers()).hasSize(3);
        assertThat(content.posts()).hasSize(3);
        assertThat(content.posts().get(0).postType()).isEqualTo("流量爆款贴");
        assertThat(content.posts().get(0).title()).contains("先别");
        assertThat(content.productCopy().productTitle()).contains("AI提示词包");
        assertThat(content.dmScripts()).hasSize(5);
    }

    private String chatResponseJson() throws IOException {
        String contentJson = """
                {
                  "covers": [
                    {"coverTitle":"副业新手先别乱买提示词","coverSubtitle":"3个能当天用的场景","visualStyle":"白底红字+截图感","layoutSuggestion":"大标题+场景标签+资料截图","imagePrompt":"1:1小红书封面，白底，红色重点字，AI提示词包截图","reason":"先别句式有避坑感，容易吸引新手"},
                    {"coverTitle":"我把提示词拆成了这张表","coverSubtitle":"收藏后直接照着改","visualStyle":"表格截图感","layoutSuggestion":"左标题右表格","imagePrompt":"小红书干货封面，表格，便签，干净","reason":"清单感强，适合收藏"},
                    {"coverTitle":"AI味太重，通常错在这句","coverSubtitle":"改完更像真人","visualStyle":"对比批注感","layoutSuggestion":"错误写法vs改后写法","imagePrompt":"左右对比封面，手写批注，少量红色","reason":"对比制造点击理由"}
                  ],
                  "posts": [
                    {"postType":"流量爆款贴","title":"副业新手做AI提示词，先别急着卖课","hook":"我发现很多人不是不会用AI，是一上来就把内容写得太像说明书。","content":"先把一个真实场景讲清楚：你是谁、卡在哪、这条提示词能帮你省哪一步。这样写出来更像经验，不像广告。","cta":"想要这张提示词拆解表，可以留言测试。","tags":["#AI提示词","#小红书运营","#副业"],"reason":"痛点明确，CTA轻，适合引流"},
                    {"postType":"流量爆款贴","title":"AI文案没人看，不一定是标题问题","hook":"有些内容看着完整，但用户就是划走。","content":"问题通常是没有具体场景。把泛泛的功能词换成一个当天能用的小动作，收藏率会更稳。","cta":"需要改写模板可以留言话术。","tags":["#AI文案","#内容变现"],"reason":"反差切入，适合评论"},
                    {"postType":"信任成交贴","title":"AI提示词包适合谁？我会这样讲","hook":"别只看提示词数量，先看拿到后能不能直接用。","content":"这份更适合想低成本测试内容的新手，先拿3条内容验证反馈，再决定要不要放大。","cta":"不确定适不适合，可以把赛道发我。","tags":["#AI工具","#资料包"],"reason":"先筛选人群，成交更自然"}
                  ],
                  "productCopy": {"productTitle":"AI提示词包｜新手引流版","productSubtitle":"适合副业新手快速生成小红书内容","sellingPoints":["低成本","可复制"],"deliveryNote":"交付提示词表、改写模板和封面标题库","riskNotice":"不承诺收益，需要结合账号调整"},
                  "dmScripts": [
                    {"scene":"初次咨询","message":"你现在是刚开始做，还是已经发过内容了？"},
                    {"scene":"价格咨询","message":"先看你要基础模板还是需要我帮你改一版。"},
                    {"scene":"犹豫观望","message":"可以先把你的赛道发我，我帮你判断方向。"},
                    {"scene":"想要资料","message":"我先发你基础清单，你看完再决定。"},
                    {"scene":"成交引导","message":"今天想开始的话，建议先用基础版跑3条。"}
                  ]
                }
                """;
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();
        root.putArray("choices")
                .addObject()
                .putObject("message")
                .put("content", contentJson);
        return mapper.writeValueAsString(root);
    }
}
