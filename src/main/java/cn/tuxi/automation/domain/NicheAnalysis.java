package cn.tuxi.automation.domain;

public record NicheAnalysis(
        int totalScore,
        int demandScore,
        int monetizationScore,
        int executionScore,
        int budgetFitScore,
        int riskScore,
        String level,
        String summary
) {
}
