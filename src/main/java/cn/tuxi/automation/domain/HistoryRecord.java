package cn.tuxi.automation.domain;

import java.time.Instant;
import java.util.List;

public record HistoryRecord(
        String id,
        String niche,
        String productName,
        String targetUser,
        List<CoverPlan> generatedCovers,
        List<PostImagePlan> generatedPostImages,
        List<XiaohongshuPost> generatedPosts,
        ProductCopy productCopy,
        List<DmScript> dmScripts,
        Instant createdAt
) {
}
