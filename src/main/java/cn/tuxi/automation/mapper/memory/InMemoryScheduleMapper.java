package cn.tuxi.automation.mapper.memory;

import cn.tuxi.automation.domain.PublishSchedule;
import cn.tuxi.automation.mapper.ScheduleMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class InMemoryScheduleMapper implements ScheduleMapper {
    private final List<PublishSchedule> schedules = new ArrayList<>();

    @Override
    public synchronized void insert(PublishSchedule schedule) {
        schedules.add(0, schedule);
    }

    @Override
    public synchronized List<PublishSchedule> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(schedules));
    }
}
