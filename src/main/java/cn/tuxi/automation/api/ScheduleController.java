package cn.tuxi.automation.api;

import cn.tuxi.automation.application.ScheduleService;
import cn.tuxi.automation.application.ScheduleService.ScheduleRequest;
import cn.tuxi.automation.application.ScheduleService.ScheduleResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {
    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping
    public ScheduleResult save(@RequestBody ScheduleRequest request) {
        return scheduleService.save(request);
    }

    @GetMapping
    public ScheduleList list() {
        return new ScheduleList(scheduleService.list());
    }

    public record ScheduleList(List<?> schedules) {
    }
}
