package cn.tuxi.automation.domain;

import java.util.List;

public record ScheduleResult(PublishSchedule schedule, List<PublishSchedule> schedules) {
}
