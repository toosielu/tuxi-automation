package cn.tuxi.automation.domain;

import java.time.Instant;
import java.util.List;

public record GenerateResult(
        String id,
        List<CoverPlan> covers,
        List<PostImagePlan> postImages,
        List<XiaohongshuPost> posts,
        ProductCopy productCopy,
        List<DmScript> dmScripts,
        Instant createdAt
) {
}
