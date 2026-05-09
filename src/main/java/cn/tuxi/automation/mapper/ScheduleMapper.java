package cn.tuxi.automation.mapper;

import cn.tuxi.automation.domain.PublishSchedule;

import java.util.List;

public interface ScheduleMapper {
    void insert(PublishSchedule schedule);

    List<PublishSchedule> findAll();
}
