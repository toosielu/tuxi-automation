package cn.tuxi.automation.test.domain;

import cn.tuxi.automation.domain.GeneratedImage;
import cn.tuxi.automation.domain.ImageGenerateRequest;
import cn.tuxi.automation.service.impl.OpenAiImageGenerationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class OpenAiImageGenerationServiceTest {
    private HttpServer server;

    @AfterEach
    void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void callsImagesGenerationEndpointWithTokenAndReturnsDataUrl() throws Exception {
        String imageBase64 = Base64.getEncoder().encodeToString("fake-png".getBytes(StandardCharsets.UTF_8));
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/images/generations", exchange -> {
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            assertThat(exchange.getRequestHeaders().getFirst("Authorization")).isEqualTo("Bearer image-key");
            assertThat(requestBody).contains("gpt-image-1.5");
            assertThat(requestBody).contains("小红书爆款封面");
            byte[] response = imageResponseJson(imageBase64).getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });
        server.start();

        OpenAiImageGenerationService service = new OpenAiImageGenerationService(
                new ObjectMapper(),
                "openai",
                "image-key",
                "http://127.0.0.1:" + server.getAddress().getPort(),
                "gpt-image-1.5",
                "1024x1024",
                "png",
                5
        );

        GeneratedImage image = service.generate(new ImageGenerateRequest(
                "一张小红书爆款封面，白底红字",
                "AI副业",
                "小红书爆款封面",
                "避坑型"
        ));

        assertThat(image.imageBase64()).isEqualTo(imageBase64);
        assertThat(image.dataUrl()).isEqualTo("data:image/png;base64," + imageBase64);
        assertThat(image.provider()).isEqualTo("openai");
        assertThat(image.model()).isEqualTo("gpt-image-1.5");
    }

    private String imageResponseJson(String imageBase64) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();
        root.putArray("data").addObject().put("b64_json", imageBase64);
        return mapper.writeValueAsString(root);
    }
}
