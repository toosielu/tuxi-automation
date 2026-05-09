package cn.tuxi.automation.application;

import java.time.Instant;

public record PublishSchedule(
        String id,
        String title,
        String coverText,
        String cta,
        Instant scheduledAt,
        String status
) {
}
