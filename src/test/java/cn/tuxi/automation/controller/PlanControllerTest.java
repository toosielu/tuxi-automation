package cn.tuxi.automation.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createsAutomationPlanFromProjectInput() throws Exception {
        String body = """
                {
                  "projectName": "小红书副业资料号",
                  "niche": "小红书虚拟电商",
                  "audience": "想做副业的新手",
                  "stage": "新手起盘",
                  "dailyMinutes": 45,
                  "budget": 300,
                  "goal": "资料包成交",
                  "pain": "不知道选什么产品，帖子发了没人咨询"
                }
                """;

        mockMvc.perform(post("/api/plan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.project.name").value("小红书副业资料号"))
                .andExpect(jsonPath("$.nicheScore.totalScore", greaterThanOrEqualTo(80)))
                .andExpect(jsonPath("$.viralTopics", hasSize(6)))
                .andExpect(jsonPath("$.viralTopics[0].trafficScore", greaterThanOrEqualTo(80)))
                .andExpect(jsonPath("$.viralTopics[0].monetizationPath").exists())
                .andExpect(jsonPath("$.products", hasSize(3)))
                .andExpect(jsonPath("$.posts", hasSize(5)))
                .andExpect(jsonPath("$.storeCopy.title").exists())
                .andExpect(jsonPath("$.privateDomainSop", hasSize(5)));
    }
}
