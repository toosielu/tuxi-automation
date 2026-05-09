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
        NicheVoice voice = resolveVoice(niche, audience);

        return List.of(
                new PostDraft(
                        "引流爆款",
                        "痛点反差",
                        voice.peopleLabel() + "别急着做" + niche + "，先看这 5 个信号",
                        voice.coverPrefix() + "先别急着发",
                        "封面主图用真实桌面/手机备忘录截图感，左侧放大标题，右侧放 5 条打勾清单，颜色克制，不要科技感大字报。",
                        "我见过不少" + audience + "一上来就想把内容做满，结果越写越像广告。其实先看 5 个信号就够了：评论里有没有具体问题、收藏是不是高过点赞、私信问的是不是同一类需求、对方愿不愿意留下场景、你手里有没有能当天交付的东西。缺两个以上，就先别急着卖。",
                        "想要我用这 5 个信号帮你拆一版，可以留言“拆”。"
                ),
                new PostDraft(
                        "引流爆款",
                        "结果路径",
                        "我会这样从 0 跑" + niche + "，不先做矩阵",
                        "先跑一条小闭环",
                        "流程图风格，4 个节点横向排列：选题、笔记、店铺、私域。每个节点下面放一句短备注，像手写复盘稿。",
                        "如果是我从 0 开始，会先用 7 天跑一个小闭环。前三天只测内容，不急着改简介；第 4 天把问得最多的问题整理成一个小资料；第 5 天上架低价版本；第 6 天改私信回复；第 7 天只看三件事：有没有收藏、有没有私信、有没有人点进店铺。",
                        "需要这张 7 天表，私信我“7天”。"
                ),
                new PostDraft(
                        "引流爆款",
                        "避坑反差",
                        "你以为是没流量，其实是这句话写错了",
                        "别把用户劝跑了",
                        "做成聊天截图式封面，上方一句错误表达，下方一句改法，中间用箭头连接。",
                        "很多笔记卡住，不是选题不行，是最后一句太硬。比如一上来就让人下单，用户会本能后退。可以换成更轻的动作：先问场景、先给清单、先让对方自查。尤其是" + niche + "，信任感比催促更重要。",
                        "我整理了 12 句低压 CTA，评论“话术”发你。"
                ),
                new PostDraft(
                        "引流爆款",
                        "真实场景",
                        voice.sceneTitle() + "，我建议先发这种笔记",
                        voice.coverPrefix() + "直接写场景",
                        "生活化场景图，手机、便签、电脑或桌面资料组合，画面像真实博主随手拍，少用夸张贴纸。",
                        voice.sceneBody() + "这类内容不需要装得很专业，越像真实经历越容易有人停下来。正文先讲一个具体场景，再给一个可照做的小动作，最后再把资料或模板自然带出来。",
                        "你把当前赛道发我，我给你改 3 个场景选题。"
                ),
                new PostDraft(
                        "干货信任",
                        "清单教程",
                        niche + "图文贴可以照这个结构写",
                        "一篇笔记 4 块内容",
                        "信息图封面，四宫格：场景、误区、做法、领取。每格只放 8 到 12 个字。",
                        "结构不用复杂。第一段写你看到的真实问题，第二段说很多人哪里做反了，第三段给一个能当天执行的小方法，最后一句给低压力入口。你可以把" + pain + "拆成 3 个小问题，每个小问题单独发一篇。",
                        "收藏这条，下次写不出来就按这个顺序填。"
                ),
                new PostDraft(
                        "干货信任",
                        "模板拆解",
                        "一条" + niche + "干货贴，标题和封面这样搭",
                        "标题别只写干货",
                        "左右对比图，左边写弱标题，右边写改后标题；底部放封面关键词：人群、场景、结果。",
                        "标题里至少要有一个具体对象，比如" + voice.peopleLabel() + "；封面里要有一个具体结果，比如少走弯路、直接照做、少踩坑。正文别堆概念，拿一个真实场景讲清楚就够了。",
                        "想要标题模板，评论“标题”。"
                ),
                new PostDraft(
                        "干货信任",
                        "产品承接",
                        product.name() + "不要这样卖，换成场景更自然",
                        "别只说资料很全",
                        "产品资料平铺图，旁边放 3 个使用场景标签，整体像交付清单，不像广告海报。",
                        "虚拟产品最怕写成库存清单。用户真正想知道的是：我什么时候用、能省什么事、拿到后第一步做什么。你可以把" + product.name() + "包装成一个小结果，而不是一堆文件。",
                        "要详情页结构的话，留言“详情页”。"
                ),
                new PostDraft(
                        "干货信任",
                        "复盘方法",
                        "发完" + niche + "笔记后，我只看这 3 个数",
                        "别只盯点赞",
                        "数据看板截图感，突出收藏率、私信率、店铺点击，配简单箭头和备注。",
                        "点赞好看，但不一定能出单。我更建议看收藏率、私信率、店铺点击。收藏高说明内容有用，私信高说明痛点打准，店铺点击高才说明承接顺。只要其中一个数异常，下一条笔记就知道往哪改。",
                        "你发一条数据给我，我帮你判断先改哪里。"
                )
        );
    }

    private NicheVoice resolveVoice(String niche, String audience) {
        String text = (blankToDefault(niche, "") + blankToDefault(audience, "")).toLowerCase();
        if (text.contains("母婴") || text.contains("育儿") || text.contains("妈妈") || text.contains("宝妈")) {
            return new NicheVoice(
                    "新手妈妈",
                    "妈妈们",
                    "一边带娃一边做内容",
                    "很多妈妈不是没内容可写，而是把日常问题写得太像科普。比如睡眠、辅食、启蒙、收纳，都可以从一个真实小崩溃写起。"
            );
        }
        if (text.contains("教辅") || text.contains("学习") || text.contains("家长") || text.contains("学生")) {
            return new NicheVoice(
                    "家长",
                    "家长先看",
                    "孩子学习卡住时",
                    "教辅内容别一上来讲方法论。先写一个家长熟悉的场景，比如作业拖拉、错题反复错、背了又忘，再给一个能今晚就试的小动作。"
            );
        }
        if (text.contains("ai") || text.contains("人工智能") || text.contains("提效")) {
            return new NicheVoice(
                    "普通人",
                    "AI 别乱学",
                    "刚开始学 AI 工具",
                    "AI 赛道最容易写得像工具说明书。更好的写法是先写一个工作里真实浪费时间的场景，再给一个提示词或流程。"
            );
        }
        if (text.contains("商业") || text.contains("副业") || text.contains("创业") || text.contains("赚钱")) {
            return new NicheVoice(
                    "想做副业的人",
                    "副业别上头",
                    "下班后做项目",
                    "商业类内容要少讲大词，多讲选择和取舍。比如预算不多、时间不多、没人带，这些场景比宏观趋势更容易让人停留。"
            );
        }
        return new NicheVoice(
                "新手",
                "新手先看",
                "第一次做这个方向",
                "这个赛道先别写得太满，拿一个具体场景切进去，让用户觉得你真的懂他的卡点。"
        );
    }

    private record NicheVoice(String peopleLabel, String coverPrefix, String sceneTitle, String sceneBody) {
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
