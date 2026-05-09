package cn.tuxi.automation.controller;

import cn.tuxi.automation.domain.ScheduleList;
import cn.tuxi.automation.domain.ScheduleRequest;
import cn.tuxi.automation.domain.ScheduleResult;
import cn.tuxi.automation.service.ScheduleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
