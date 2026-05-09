package cn.tuxi.automation.controller;

import cn.tuxi.automation.domain.AutomationPlan;
import cn.tuxi.automation.domain.PlanRequest;
import cn.tuxi.automation.service.AutomationPlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PlanController {
    private final AutomationPlanService automationPlanService;

    public PlanController(AutomationPlanService automationPlanService) {
        this.automationPlanService = automationPlanService;
    }

    @PostMapping("/plan")
    public ResponseEntity<AutomationPlan> createPlan(@RequestBody PlanRequest request) {
        return ResponseEntity.ok(automationPlanService.create(request));
    }
}
