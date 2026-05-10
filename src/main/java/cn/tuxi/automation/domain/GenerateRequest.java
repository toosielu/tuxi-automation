package cn.tuxi.automation.domain;

public record GenerateRequest(
        String sourceType,
        String sourceText,
        String sourceLink,
        String imageNotes,
        String niche,
        String productName,
        String targetUser,
        String sellingPoints,
        String goal,
        String style,
        String painPoint
) {
}
