package cn.tuxi.automation.domain;

public record ViralTopic(
        String type,
        String title,
        String coverText,
        String hook,
        int trafficScore,
        String reason,
        String monetizationPath
) {
}
