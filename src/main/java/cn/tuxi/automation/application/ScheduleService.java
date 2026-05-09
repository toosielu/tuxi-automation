package cn.tuxi.automation.application;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ScheduleService {
    private final List<PublishSchedule> schedules = new ArrayList<>();

    public synchronized ScheduleResult save(ScheduleRequest request) {
        PublishSchedule schedule = new PublishSchedule(
                "s" + System.currentTimeMillis(),
                request.title(),
                request.coverText(),
                request.cta(),
                parseInstant(request.scheduledAt()),
                "待发布"
        );
        schedules.add(0, schedule);
        return new ScheduleResult(schedule, list());
    }

    public synchronized List<PublishSchedule> list() {
        return Collections.unmodifiableList(new ArrayList<>(schedules));
    }

    private Instant parseInstant(String value) {
        if (value == null || value.isBlank()) {
            return Instant.now();
        }
        return Instant.parse(value);
    }

    public record ScheduleRequest(String title, String coverText, String cta, String scheduledAt) {
    }

    public record ScheduleResult(PublishSchedule schedule, List<PublishSchedule> schedules) {
    }
}
