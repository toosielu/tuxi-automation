package cn.tuxi.automation.domain;

import java.util.List;

public record Product(
        String id,
        String name,
        String category,
        List<String> audience,
        String type,
        String priceRange,
        String delivery,
        String difficulty,
        String risk,
        List<String> sellingPoints
) {
}
