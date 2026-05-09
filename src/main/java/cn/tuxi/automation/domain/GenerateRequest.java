package cn.tuxi.automation.domain;

public record GenerateRequest(
        String niche,
        String productName,
        String targetUser,
        String sellingPoints,
        String goal,
        String style,
        String painPoint
) {
}
