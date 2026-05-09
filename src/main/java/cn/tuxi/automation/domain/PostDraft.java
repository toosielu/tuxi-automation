package cn.tuxi.automation.domain;

public record PostDraft(
        String contentType,
        String angle,
        String title,
        String coverText,
        String imageBrief,
        String body,
        String cta
) {
}
