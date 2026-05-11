package cn.tuxi.automation.service;

import cn.tuxi.automation.domain.AiGeneratedContent;
import cn.tuxi.automation.domain.GenerateRequest;

public interface AiContentProvider {
    boolean supports();

    AiGeneratedContent generate(GenerateRequest request);
}
