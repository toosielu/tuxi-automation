package cn.tuxi.automation.service.impl;

import cn.tuxi.automation.domain.AutomationPlan;
import cn.tuxi.automation.domain.MatchedProduct;
import cn.tuxi.automation.domain.NicheAnalysis;
import cn.tuxi.automation.domain.PlanRequest;
import cn.tuxi.automation.domain.PostDraft;
import cn.tuxi.automation.domain.Product;
import cn.tuxi.automation.domain.ProjectInput;
import cn.tuxi.automation.domain.StoreCopy;
import cn.tuxi.automation.domain.ViralTopic;
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
                buildViralTopics(input, primaryProduct),
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

    private List<ViralTopic> buildViralTopics(ProjectInput input, Product product) {
        String audience = blankToDefault(input.audience(), "想做副业的新手");
        String niche = blankToDefault(input.niche(), "小红书虚拟电商");
        String pain = blankToDefault(input.pain(), "不知道卖什么、发什么、怎么出单");
        String productPath = "用「" + product.name() + "」承接到店铺，再用私域 7 天 SOP 做复购";

        return List.of(
                new ViralTopic(
                        "痛点清单",
                        audience + "做" + niche + "，最容易踩的 7 个坑",
                        "这 7 个坑，正在吃掉你的流量",
                        "很多人不是不努力，是一开始就把链路做反了。",
                        92,
                        "痛点具体、适合收藏，能把用户从泛兴趣筛到真实需求。",
                        productPath
                ),
                new ViralTopic(
                        "结果路径",
                        "从 0 做" + niche + "，先把这条成交链路跑通",
                        "帖子 -> 店铺 -> 出单 -> 私域",
                        "先别急着做矩阵，第一阶段只看一件事：有没有人愿意问、愿意买。",
                        90,
                        "路径型内容能降低新手焦虑，也方便自然引导到执行资料包。",
                        productPath
                ),
                new ViralTopic(
                        "反差拆解",
                        "为什么你的小红书有曝光，却没有人下单？",
                        "有流量但没成交，多半卡在这里",
                        "曝光不等于成交，如果帖子没有承接口，流量很快就浪费掉。",
                        88,
                        "反差标题点击率高，能把问题从流量转到转化链路。",
                        "引导用户领取店铺承接模板，再推荐" + product.name()
                ),
                new ViralTopic(
                        "选品判断",
                        "虚拟资料怎么选品？先看这 4 个信号",
                        "别凭感觉选品",
                        "能不能卖，不是看资料多不多，而是看用户愿不愿意为结果付费。",
                        86,
                        "选品是成交前置问题，适合带出产品库和诊断服务。",
                        "用产品库匹配低风险测试品，再进入店铺成交"
                ),
                new ViralTopic(
                        "行动任务",
                        "今天 30 分钟，搭一个" + niche + "最小成交闭环",
                        "今天只做这 4 步",
                        "如果你一直停在想项目，今天先完成一次最小测试。",
                        84,
                        "任务型内容执行感强，适合促成收藏、私信和打卡。",
                        "评论或私信领取执行清单，进入私域做 7 天跟进"
                ),
                new ViralTopic(
                        "避坑合规",
                        "做虚拟资料别乱上架，这些表达容易出问题",
                        "新手上架前先自查",
                        "虚拟产品能做，但标题、详情页和交付内容都要避开夸大承诺。",
                        82,
                        "合规话题信任感强，能提升用户对陪跑和模板的付费意愿。",
                        "承接到合规上架模板和售后 SOP"
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
