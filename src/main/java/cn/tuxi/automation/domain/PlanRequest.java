package cn.tuxi.automation.domain;

public record PlanRequest(
        String projectName,
        String niche,
        String audience,
        String stage,
        Integer dailyMinutes,
        Integer budget,
        String goal,
        String pain
) {
}
