package cn.tuxi.automation.controller;

import cn.tuxi.automation.domain.GeneratedImage;
import cn.tuxi.automation.domain.ImageGenerateRequest;
import cn.tuxi.automation.service.ImageGenerationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/images")
public class ImageGenerationController {
    private final ImageGenerationService imageGenerationService;

    public ImageGenerationController(ImageGenerationService imageGenerationService) {
        this.imageGenerationService = imageGenerationService;
    }

    @PostMapping("/generate")
    public GeneratedImage generate(@RequestBody ImageGenerateRequest request) {
        return imageGenerationService.generate(request);
    }
}
