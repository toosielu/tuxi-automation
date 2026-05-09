package cn.tuxi.automation.domain;

public record ProjectInput(
        String niche,
        String audience,
        String stage,
        int dailyMinutes,
        int budget,
        String goal,
        String pain
) {
}
