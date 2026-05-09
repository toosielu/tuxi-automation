package cn.tuxi.automation.controller;

import cn.tuxi.automation.domain.GenerateRequest;
import cn.tuxi.automation.domain.GenerateResult;
import cn.tuxi.automation.service.AiGenerateService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AiGenerateController {
    private final AiGenerateService aiGenerateService;

    public AiGenerateController(AiGenerateService aiGenerateService) {
        this.aiGenerateService = aiGenerateService;
    }

    @PostMapping("/generate")
    public GenerateResult generate(@RequestBody GenerateRequest request) {
        return aiGenerateService.generate(request);
    }
}
