package cn.tuxi.automation.test.domain;

import cn.tuxi.automation.domain.ViralContentTemplate;
import cn.tuxi.automation.service.impl.ViralContentTemplateLibrary;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ViralContentTemplateLibraryTest {

    private final ViralContentTemplateLibrary library = new ViralContentTemplateLibrary();

    @Test
    void selectsPainAvoidanceTemplateForPrivateMessageGoal() {
        ViralContentTemplate template = library.selectFor("私信咨询", "避坑型");

        assertThat(template.name()).isEqualTo("痛点避坑型");
        assertThat(template.coverFormula()).contains("先别");
        assertThat(template.hookFormula()).contains("不是不会做");
        assertThat(template.bodyPattern()).contains("错误做法");
        assertThat(template.ctaLevel()).isEqualTo("低压互动");
        assertThat(template.riskNotice()).contains("不夸大收益");
    }

    @Test
    void selectsChecklistTemplateForTrafficGoal() {
        ViralContentTemplate template = library.selectFor("引流涨粉", "清单型");

        assertThat(template.name()).isEqualTo("清单干货型");
        assertThat(template.coverFormula()).contains("清单");
        assertThat(template.bodyPattern()).contains("步骤");
        assertThat(template.suitableGoals()).contains("引流涨粉");
    }

    @Test
    void selectsConversionTemplateForProductGoal() {
        ViralContentTemplate template = library.selectFor("产品成交", "干货型");

        assertThat(template.name()).isEqualTo("成交承接型");
        assertThat(template.coverFormula()).contains("适合谁");
        assertThat(template.ctaLevel()).isEqualTo("成交引导");
        assertThat(template.suitableGoals()).contains("产品成交");
    }
}
