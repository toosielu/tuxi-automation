package cn.tuxi.automation.domain;

public record GeneratedImage(
        String id,
        String prompt,
        String imageBase64,
        String mimeType,
        String dataUrl,
        String provider,
        String model
) {
}
