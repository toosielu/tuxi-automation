package cn.tuxi.automation.service.impl;

import cn.tuxi.automation.domain.MatchedProduct;
import cn.tuxi.automation.domain.Product;
import cn.tuxi.automation.domain.ProjectInput;
import cn.tuxi.automation.mapper.ProductMapper;
import cn.tuxi.automation.service.ProductMatchingService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class ProductMatchingServiceImpl implements ProductMatchingService {
    private final ProductMapper productMapper;

    public ProductMatchingServiceImpl(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @Override
    public List<MatchedProduct> match(ProjectInput input) {
        return productMapper.findAll().stream()
                .map(product -> new MatchedProduct(product, score(product, input), reason(product, input)))
                .sorted(Comparator.comparingInt(MatchedProduct::matchScore).reversed())
                .limit(3)
                .toList();
    }

    private int score(Product product, ProjectInput input) {
        int score = 50;
        String stage = normalize(input.stage());
        String niche = normalize(input.niche());
        String goal = normalize(input.goal());
        String category = normalize(product.category());

        boolean audienceMatches = product.audience().stream()
                .map(ProductMatchingServiceImpl::normalize)
                .anyMatch(item -> stage.contains(item) || item.contains(stage) || normalize(input.audience()).contains(item));
        if (audienceMatches) {
            score += 18;
        }
        if (!niche.isBlank() && (category.contains(niche) || niche.contains(category))) {
            score += 16;
        }
        if ((niche + category).contains("小红书") || (niche + category).contains("运营")) {
            score += 10;
        }
        if ((goal.contains("ai") || goal.contains("批量") || goal.contains("自动")) && category.contains("ai")) {
            score += 12;
        }
        if ((stage.contains("已有") || normalize(input.audience()).contains("已有")) && category.contains("私域")) {
            score += 10;
        }
        return Math.min(score, 96);
    }

    private String reason(Product product, ProjectInput input) {
        if (normalize(product.category()).contains(normalize(input.niche()))) {
            return "产品分类与目标赛道直接匹配";
        }
        if (product.audience().stream().anyMatch(item -> normalize(input.audience()).contains(normalize(item)))) {
            return "适合当前目标人群";
        }
        return "可作为当前链路的测试产品";
    }

    private static String normalize(String value) {
        return value == null ? "" : value.toLowerCase();
    }
}
