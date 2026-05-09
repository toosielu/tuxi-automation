package cn.tuxi.automation.api;

import cn.tuxi.automation.application.AutomationPlan;
import cn.tuxi.automation.application.AutomationPlanService;
import cn.tuxi.automation.domain.ProjectInput;
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
        ProjectInput input = new ProjectInput(
                request.niche(),
                request.audience(),
                request.stage(),
                request.dailyMinutes() == null ? 30 : request.dailyMinutes(),
                request.budget() == null ? 0 : request.budget(),
                request.goal(),
                request.pain()
        );
        return ResponseEntity.ok(automationPlanService.create(request.projectName(), input));
    }

    public record PlanRequest(
            String projectName,
            String niche,
            String audience,
            String stage,
            Integer dailyMinutes,
            Integer budget,
            String goal,
            String pain
    ) {
    }
}
