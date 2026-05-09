package cn.tuxi.automation.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NicheAnalysisServiceTest {

    @Test
    void scoresVirtualCommerceNicheAsPriorityWhenDemandAndDeliveryFit() {
        NicheAnalysisService service = new NicheAnalysisService();
        ProjectInput input = new ProjectInput(
                "小红书虚拟电商",
                "想做副业的新手",
                "新手起盘",
                45,
                300,
                "资料包成交",
                "不知道选什么产品，帖子发了没人咨询"
        );

        NicheAnalysis analysis = service.analyze(input);

        assertThat(analysis.totalScore()).isGreaterThanOrEqualTo(80);
        assertThat(analysis.level()).isEqualTo("优先测试");
        assertThat(analysis.summary()).contains("虚拟产品");
    }

    @Test
    void lowersRiskScoreForSensitiveNiche() {
        NicheAnalysisService service = new NicheAnalysisService();
        ProjectInput input = new ProjectInput(
                "投资理财资料",
                "想赚钱的新手",
                "新手起盘",
                30,
                100,
                "资料包成交",
                "想快速变现"
        );

        NicheAnalysis analysis = service.analyze(input);

        assertThat(analysis.riskScore()).isLessThan(60);
        assertThat(analysis.summary()).contains("合规风险");
    }
}
