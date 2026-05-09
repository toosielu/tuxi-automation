package cn.tuxi.automation.service.impl;

import cn.tuxi.automation.domain.AutomationPlan;
import cn.tuxi.automation.domain.MatchedProduct;
import cn.tuxi.automation.domain.NicheAnalysis;
import cn.tuxi.automation.domain.PlanRequest;
import cn.tuxi.automation.domain.PostDraft;
import cn.tuxi.automation.domain.Product;
import cn.tuxi.automation.domain.ProjectInput;
import cn.tuxi.automation.domain.StoreCopy;
import cn.tuxi.automation.service.AutomationPlanService;
import cn.tuxi.automation.service.NicheAnalysisService;
import cn.tuxi.automation.service.ProductMatchingService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class AutomationPlanServiceImpl implements AutomationPlanService {
    private final NicheAnalysisService nicheAnalysisService;
    private final ProductMatchingService productMatchingService;

    public AutomationPlanServiceImpl(
            NicheAnalysisService nicheAnalysisService,
            ProductMatchingService productMatchingService
    ) {
        this.nicheAnalysisService = nicheAnalysisService;
        this.productMatchingService = productMatchingService;
    }

    @Override
    public AutomationPlan create(PlanRequest request) {
        ProjectInput input = new ProjectInput(
                request.niche(),
                request.audience(),
                request.stage(),
                request.dailyMinutes() == null ? 30 : request.dailyMinutes(),
                request.budget() == null ? 0 : request.budget(),
                request.goal(),
                request.pain()
        );
        NicheAnalysis analysis = nicheAnalysisService.analyze(input);
        List<MatchedProduct> products = productMatchingService.match(input);
        Product primaryProduct = products.getFirst().product();

        return new AutomationPlan(
                new AutomationPlan.ProjectSummary(
                        blankToDefault(request.projectName(), blankToDefault(input.niche(), "小红书虚拟电商") + "自动化项目"),
                        blankToDefault(input.stage(), "新手起盘"),
                        Instant.now()
                ),
                analysis,
                products,
                buildPosts(input, primaryProduct),
                buildImagePrompts(input, primaryProduct),
                new StoreCopy(
                        primaryProduct.name() + "｜新手可执行版",
                        "适合" + blankToDefault(input.audience(), "想做副业的新手") + "，按步骤完成从内容到成交的测试闭环。",
                        primaryProduct.sellingPoints(),
                        primaryProduct.delivery(),
                        primaryProduct.risk()
                ),
                List.of(
                        "成交后 24 小时内发送欢迎语和资料领取方式",
                        "第 1 天确认用户目标和当前卡点，打标签",
                        "第 3 天提醒完成第一个小任务，收集反馈",
                        "第 5 天推送案例复盘和进阶资料",
                        "第 7 天根据完成情况推荐进阶包或陪跑服务"
                ),
                List.of(
                        "先选择 1 个主推产品，不要同时测试太多方向",
                        "连续发布 5 条不同角度笔记，记录收藏和私信数据",
                        "把咨询问题回写到内容库，生成下一轮选题",
                        "有首单后再优化详情页和私域承接"
                )
        );
    }

    private List<PostDraft> buildPosts(ProjectInput input, Product product) {
        String audience = blankToDefault(input.audience(), "想做副业的新手");
        String niche = blankToDefault(input.niche(), "小红书虚拟电商");
        String pain = blankToDefault(input.pain(), "不知道卖什么、发什么、怎么出单");

        return List.of(
                new PostDraft(
                        "痛点清单",
                        "新手做" + niche + "，最容易卡住的 5 个地方",
                        "别再盲目发帖了",
                        audience + "常见的问题不是不努力，而是链路没搭好。先确认人群和痛点，再选一个可交付的" + product.name() + "，最后用固定内容模板持续测试咨询。",
                        "想要起盘清单，可以评论“路线”。"
                ),
                new PostDraft(
                        "结果路径",
                        "从 0 跑通" + niche + "，先别急着做矩阵",
                        "先跑通第一单",
                        "第一阶段只做三件事：筛一个低风险赛道，准备一个能自动交付的产品，连续发布 10 条围绕同一痛点的笔记。数据稳定后再放大。",
                        "需要选品表和发帖模板，可以私信“起盘”。"
                ),
                new PostDraft(
                        "避坑反差",
                        "为什么你发了很多小红书，还是没有人买？",
                        "不是流量少，是承接断了",
                        "如果内容只讲概念，没有引导到店铺或私域，流量很容易浪费。每条笔记都要明确：解决什么问题、适合谁、下一步怎么领或怎么买。",
                        "我整理了转化话术模板，想看可以留言。"
                ),
                new PostDraft(
                        "产品展示",
                        product.name() + "怎么包装，用户才愿意下单？",
                        "虚拟产品包装公式",
                        "包装虚拟产品时，不要只写“资料很全”。要写清楚使用场景、交付内容、节省时间、适合人群和售后方式。用户买的是更快解决" + pain + "。",
                        "想要详情页结构，评论“详情页”。"
                ),
                new PostDraft(
                        "行动任务",
                        "今天用 30 分钟，搭一个" + niche + "测试闭环",
                        "今日起盘任务",
                        "第 1 步选一个人群，第 2 步确定一个资料型产品，第 3 步写 3 个痛点标题，第 4 步设置店铺承接入口。先验证有人咨询，再优化产品。",
                        "收藏后照着做，卡住可以私信我。"
                )
        );
    }

    private List<String> buildImagePrompts(ProjectInput input, Product product) {
        String niche = blankToDefault(input.niche(), "小红书虚拟电商");
        return List.of(
                "小红书封面图，主题“" + niche + "起盘路线”，干净白底，红色重点标注，包含流程箭头和清单感，适合知识类账号",
                "小红书信息图，展示“赛道-选品-发帖-店铺-私域”五步成交链路，现代工作台风格，高级但清晰",
                "产品展示图，" + product.name() + "资料包界面 mockup，表格、文档、SOP 叠放，强调可执行和自动交付"
        );
    }

    private static String blankToDefault(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
