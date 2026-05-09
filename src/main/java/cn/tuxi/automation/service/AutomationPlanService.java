package cn.tuxi.automation.service;

import cn.tuxi.automation.domain.AutomationPlan;
import cn.tuxi.automation.domain.PlanRequest;

public interface AutomationPlanService {
    AutomationPlan create(PlanRequest request);
}
