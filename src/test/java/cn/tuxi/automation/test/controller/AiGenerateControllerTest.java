package cn.tuxi.automation.test.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AiGenerateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void generatesViralContentPackageAndSavesHistory() throws Exception {
        String body = """
                {
                  "niche": "小红书虚拟资料",
                  "sourceType": "LINK",
                  "sourceLink": "https://example.com/sample-note",
                  "productName": "小红书虚拟电商实操资料包",
                  "targetUser": "想做副业的新手",
                  "sellingPoints": "低成本、可复制、适合新手、自动发货",
                  "goal": "产品成交",
                  "style": "干货型",
                  "painPoint": "不知道发什么内容，也不知道怎么承接成交"
                }
                """;

        mockMvc.perform(post("/api/ai/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.covers", hasSize(3)))
                .andExpect(jsonPath("$.covers[0].coverTitle").value(containsString("新手")))
                .andExpect(jsonPath("$.covers[0].imagePrompt").exists())
                .andExpect(jsonPath("$.posts", hasSize(3)))
                .andExpect(jsonPath("$.posts[*].postType", hasItem("流量爆款贴")))
                .andExpect(jsonPath("$.posts[*].postType", hasItem("信任成交贴")))
                .andExpect(jsonPath("$.posts[0].content").value(containsString("链接")))
                .andExpect(jsonPath("$.posts[0].content").value(not(containsString("首先"))))
                .andExpect(jsonPath("$.posts[0].content").value(not(containsString("综上所述"))))
                .andExpect(jsonPath("$.productCopy.productTitle").value(containsString("小红书虚拟电商实操资料包")))
                .andExpect(jsonPath("$.dmScripts", hasSize(5)));

        mockMvc.perform(get("/api/history/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.records", hasSize(1)))
                .andExpect(jsonPath("$.records[0].productName").value("小红书虚拟电商实操资料包"));
    }
}
