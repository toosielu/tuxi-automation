package cn.tuxi.automation.domain;

import java.util.List;

public record AiGeneratedContent(
        List<CoverPlan> covers,
        List<PostImagePlan> postImages,
        List<XiaohongshuPost> posts,
        ProductCopy productCopy,
        List<DmScript> dmScripts
) {
}
