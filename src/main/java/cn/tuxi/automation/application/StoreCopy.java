package cn.tuxi.automation.application;

import java.util.List;

public record StoreCopy(
        String title,
        String subtitle,
        List<String> bullets,
        String delivery,
        String risk
) {
}
