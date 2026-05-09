package cn.tuxi.automation.domain;

import java.time.Instant;
import java.util.List;

public record AutomationPlan(
        ProjectSummary project,
        NicheAnalysis nicheScore,
        List<MatchedProduct> products,
        List<PostDraft> posts,
        List<String> imagePrompts,
        StoreCopy storeCopy,
        List<String> privateDomainSop,
        List<String> nextActions
) {
    public record ProjectSummary(String name, String stage, Instant createdAt) {
    }
}
