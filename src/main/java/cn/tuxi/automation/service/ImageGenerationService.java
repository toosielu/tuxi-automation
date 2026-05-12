package cn.tuxi.automation.service;

import cn.tuxi.automation.domain.GeneratedImage;
import cn.tuxi.automation.domain.ImageGenerateRequest;

public interface ImageGenerationService {
    GeneratedImage generate(ImageGenerateRequest request);
}
