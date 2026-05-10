package cn.tuxi.automation.domain;

import java.util.List;

public record ViralContentTemplate(
        String name,
        String coverFormula,
        String hookFormula,
        String bodyPattern,
        String ctaLevel,
        List<String> suitableGoals,
        String riskNotice
) {
}
