package cn.tuxi.automation.domain;

import cn.tuxi.automation.mapper.ProductMapper;
import cn.tuxi.automation.service.impl.ProductMatchingServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProductMatchingServiceTest {

    @Test
    void matchesProductsByStageNicheAndGoal() {
        ProductMapper productMapper = () -> List.of(
                new Product("p1", "副业起盘资料包", "副业", List.of("新手"), "资料包", "19-99", "网盘自动交付", "低", "避免收益承诺", List.of("低成本起步")),
                new Product("p2", "小红书起号 SOP 模板", "小红书运营", List.of("新手", "已有账号"), "模板", "39-199", "文档交付", "中", "避免绝对化表达", List.of("账号定位")),
                new Product("p3", "AI 内容批量生产工作流", "AI 提效", List.of("团队"), "工作流", "199-980", "提示词交付", "高", "人工校对", List.of("批量选题"))
        );
        ProductMatchingServiceImpl service = new ProductMatchingServiceImpl(productMapper);
        ProjectInput input = new ProjectInput(
                "小红书运营",
                "想做副业的新手",
                "新手起盘",
                45,
                300,
                "资料包成交",
                "不会起号"
        );

        List<MatchedProduct> matched = service.match(input);

        assertThat(matched).hasSize(3);
        assertThat(matched.get(0).product().name()).isEqualTo("小红书起号 SOP 模板");
        assertThat(matched.get(0).matchScore()).isGreaterThan(matched.get(2).matchScore());
    }
}
