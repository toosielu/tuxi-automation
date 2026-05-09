package cn.tuxi.automation.domain;

import java.util.List;

public record XiaohongshuPost(
        String postType,
        String title,
        String hook,
        String content,
        String cta,
        List<String> tags,
        String reason
) {
}
