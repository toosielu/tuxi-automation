package cn.tuxi.automation.service;

import cn.tuxi.automation.domain.NicheAnalysis;
import cn.tuxi.automation.domain.ProjectInput;

public interface NicheAnalysisService {
    NicheAnalysis analyze(ProjectInput input);
}
