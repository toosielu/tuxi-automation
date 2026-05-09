package cn.tuxi.automation.domain;

import java.util.List;

public record ProductCopy(
        String productTitle,
        String productSubtitle,
        List<String> sellingPoints,
        String deliveryNote,
        String riskNotice
) {
}
