package cn.tuxi.automation.domain;

public record PostImagePlan(
        String imageTitle,
        String imageSubtitle,
        String visualStyle,
        String layoutSuggestion,
        String imagePrompt,
        String matchedPostType,
        String reason
) {
}
