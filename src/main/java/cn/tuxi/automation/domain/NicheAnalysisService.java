package cn.tuxi.automation.domain;

public class NicheAnalysisService {

    public NicheAnalysis analyze(ProjectInput input) {
        String text = normalize(input.niche()) + " " + normalize(input.audience()) + " " + normalize(input.goal()) + " " + normalize(input.pain());
        boolean hasUrgentDemand = containsAny(text, "副业", "赚钱", "变现", "考证", "育儿", "效率", "简历", "小红书", "资料", "模板", "AI", "求职");
        boolean hasVirtualFit = containsAny(text, "资料", "模板", "SOP", "AI", "教程", "表格", "清单", "话术", "运营", "副业", "虚拟");
        boolean hasHighRisk = containsAny(text, "医疗", "法律", "投资", "彩票", "减肥", "金融", "玄学", "侵权", "搬运", "理财");

        int demand = hasUrgentDemand ? 84 : 68;
        int monetization = hasVirtualFit ? 88 : 64;
        int execution = input.dailyMinutes() >= 45 ? 82 : 70;
        int budgetFit = input.budget() >= 300 ? 78 : 68;
        int risk = hasHighRisk ? 42 : 78;
        int total = Math.round((demand * 0.28f) + (monetization * 0.28f) + (execution * 0.18f) + (budgetFit * 0.10f) + (risk * 0.16f));

        String level = total >= 82 ? "优先测试" : total >= 70 ? "可以小规模验证" : "暂缓，先换细分切口";
        String summary = hasHighRisk
                ? "赛道存在较高合规风险，建议先换成资料、模板、效率工具等低风险交付。"
                : "赛道具备虚拟产品承接空间，适合先用内容测试咨询，再用低价产品跑首单。";

        return new NicheAnalysis(total, demand, monetization, execution, budgetFit, risk, level, summary);
    }

    private static boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private static String normalize(String value) {
        return value == null ? "" : value.toLowerCase();
    }
}
