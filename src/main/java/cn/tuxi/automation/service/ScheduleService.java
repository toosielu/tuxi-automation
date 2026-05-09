package cn.tuxi.automation.service;

import cn.tuxi.automation.domain.PublishSchedule;
import cn.tuxi.automation.domain.ScheduleRequest;
import cn.tuxi.automation.domain.ScheduleResult;

import java.util.List;

public interface ScheduleService {
    ScheduleResult save(ScheduleRequest request);

    List<PublishSchedule> list();
}
