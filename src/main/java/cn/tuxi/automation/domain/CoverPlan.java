package cn.tuxi.automation.domain;

public record CoverPlan(
        String coverTitle,
        String coverSubtitle,
        String visualStyle,
        String layoutSuggestion,
        String imagePrompt,
        String reason
) {
}
