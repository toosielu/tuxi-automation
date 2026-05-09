package cn.tuxi.automation.service;

import cn.tuxi.automation.domain.GenerateRequest;
import cn.tuxi.automation.domain.GenerateResult;

public interface AiGenerateService {
    GenerateResult generate(GenerateRequest request);
}
