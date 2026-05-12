package cn.tuxi.automation.domain;

public record ImageGenerateRequest(
        String prompt,
        String niche,
        String imageType,
        String style
) {
}
