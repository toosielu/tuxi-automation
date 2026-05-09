package cn.tuxi.automation.service.impl;

import cn.tuxi.automation.domain.PublishSchedule;
import cn.tuxi.automation.domain.ScheduleRequest;
import cn.tuxi.automation.domain.ScheduleResult;
import cn.tuxi.automation.mapper.ScheduleMapper;
import cn.tuxi.automation.service.ScheduleService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleMapper scheduleMapper;

    public ScheduleServiceImpl(ScheduleMapper scheduleMapper) {
        this.scheduleMapper = scheduleMapper;
    }

    @Override
    public ScheduleResult save(ScheduleRequest request) {
        PublishSchedule schedule = new PublishSchedule(
                "s" + System.currentTimeMillis(),
                request.title(),
                request.coverText(),
                request.cta(),
                parseInstant(request.scheduledAt()),
                "待发布"
        );
        scheduleMapper.insert(schedule);
        return new ScheduleResult(schedule, list());
    }

    @Override
    public List<PublishSchedule> list() {
        return scheduleMapper.findAll();
    }

    private Instant parseInstant(String value) {
        if (value == null || value.isBlank()) {
            return Instant.now();
        }
        return Instant.parse(value);
    }
}
