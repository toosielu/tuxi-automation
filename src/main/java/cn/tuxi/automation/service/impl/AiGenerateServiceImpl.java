package cn.tuxi.automation.service.impl;

import cn.tuxi.automation.domain.CoverPlan;
import cn.tuxi.automation.domain.DmScript;
import cn.tuxi.automation.domain.GenerateRequest;
import cn.tuxi.automation.domain.GenerateResult;
import cn.tuxi.automation.domain.HistoryRecord;
import cn.tuxi.automation.domain.ProductCopy;
import cn.tuxi.automation.domain.XiaohongshuPost;
import cn.tuxi.automation.service.AiGenerateService;
import cn.tuxi.automation.service.HistoryService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class AiGenerateServiceImpl implements AiGenerateService {
    private final HistoryService historyService;

    public AiGenerateServiceImpl(HistoryService historyService) {
        this.historyService = historyService;
    }

    @Override
    public GenerateResult generate(GenerateRequest request) {
        String id = UUID.randomUUID().toString();
        Instant createdAt = Instant.now();
        String niche = text(request.niche(), "小红书虚拟资料");
        String productName = text(request.productName(), "虚拟资料包");
        String targetUser = text(request.targetUser(), "想做副业的新手");
        String sellingPoints = text(request.sellingPoints(), "低成本、可复制、适合新手、自动发货");
        String goal = text(request.goal(), "产品成交");
        String style = text(request.style(), "干货型");
        String painPoint = text(request.painPoint(), "不知道发什么内容，也不知道怎么承接成交");

        List<CoverPlan> covers = buildCovers(niche, productName, targetUser, style);
        List<XiaohongshuPost> posts = buildPosts(niche, productName, targetUser, goal, painPoint);
        ProductCopy productCopy = buildProductCopy(productName, targetUser, sellingPoints);
        List<DmScript> dmScripts = buildDmScripts(productName);
        GenerateResult result = new GenerateResult(id, covers, posts, productCopy, dmScripts, createdAt);

        historyService.save(new HistoryRecord(
                id,
                niche,
                productName,
                targetUser,
                covers,
                posts,
                productCopy,
                dmScripts,
                createdAt
        ));
        return result;
    }

    private List<CoverPlan> buildCovers(String niche, String productName, String targetUser, String style) {
        return List.of(
                new CoverPlan(
                        targetUserShort(targetUser) + "先别乱发",
                        "3 个低成本起号方向",
                        "干净白底 + 红色重点字 + 产品截图感",
                        "上方大标题，中间放资料包截图，下方放 3 个卖点标签",
                        "小红书 1:1 封面，白色背景，红色重点标题，展示" + productName + "、电脑界面、便签元素，干净高级，适合" + niche + "知识产品",
                        "标题直接击中新手痛点，信息少但明确，容易让正在找方向的人停下来。"
                ),
                new CoverPlan(
                        "这类资料更容易被收藏",
                        targetUserShort(targetUser) + "照着做",
                        "备忘录截图感 + 真实桌面 + 轻量标注",
                        "左侧放手机备忘录截图，右侧放产品交付清单，底部放领取提示",
                        "真实桌面俯拍，小红书封面，手机备忘录、资料包文件夹、浅色便签，标题突出收藏价值，1:1 构图",
                        "收藏型封面适合干货内容，能降低广告感，同时自然承接虚拟产品。"
                ),
                new CoverPlan(
                        style + "选题别这样写",
                        "换个说法更像真人",
                        "对比图 + 手写批注 + 少量高亮",
                        "左右对比错误写法和改后写法，中间用箭头连接",
                        "小红书知识类封面，左右对比排版，手写批注风格，少量红色高亮，展示文案修改前后对比",
                        "反差对比能快速制造点击理由，也符合用户想直接抄作业的心理。"
                )
        );
    }

    private List<XiaohongshuPost> buildPosts(String niche, String productName, String targetUser, String goal, String painPoint) {
        return List.of(
                new XiaohongshuPost(
                        "流量爆款贴",
                        targetUserShort(targetUser) + "做" + niche + "，先别急着上产品",
                        "我发现很多人不是不会做，是第一步就做重了。",
                        "如果你现在卡在“发什么、怎么卖”，先别急着把产品做得很大。先用一条笔记测试一个具体问题：用户会不会收藏、会不会问、会不会点进商品。只要这三个动作里有一个跑出来，再去补资料包和详情页，成功率会稳很多。",
                        "想要这套测试表，可以评论“测试”。",
                        List.of("#小红书运营", "#虚拟资料", "#副业起盘", "#内容变现"),
                        "用轻量测试切入，降低新手压力，适合带来评论和私信。"
                ),
                new XiaohongshuPost(
                        "流量爆款贴",
                        "你以为缺流量，其实是承接断了",
                        "有些笔记数据不差，但就是没人买，问题通常不在标题。",
                        "我会先看最后一句：是不是太像广告，用户一看就想退出。更自然的做法是先给一个小清单，让对方确认自己是不是这个问题，再引导看产品或私信。尤其是" + goal + "，动作越轻，信任越容易起来。",
                        "需要低压 CTA 模板，留言“话术”。",
                        List.of("#小红书文案", "#私域转化", "#虚拟电商", "#引流文案"),
                        "反差标题有点击欲，正文把流量问题转成成交承接问题。"
                ),
                new XiaohongshuPost(
                        "信任成交贴",
                        productName + "适合谁？我会这样讲清楚",
                        "别只写资料很全，用户真正想看的是拿到后能干什么。",
                        "这份内容更适合" + targetUser + "。如果你现在的问题是" + painPoint + "，建议先按“选题、封面、正文、承接”四步跑一遍。资料只是工具，重点是让你当天就能发出一条像真人写的内容，而不是继续停在收藏教程。",
                        "不确定适不适合你，可以把你的赛道发我，我帮你看一眼。",
                        List.of("#资料包", "#小红书变现", "#内容生成", "#副业工具"),
                        "信任贴不强推产品，先解释适合人群和使用场景，更适合成交前承接。"
                )
        );
    }

    private ProductCopy buildProductCopy(String productName, String targetUser, String sellingPoints) {
        return new ProductCopy(
                productName + "｜新手可执行版",
                "适合" + targetUser + "，按步骤生成可发布的小红书内容",
                Arrays.stream(sellingPoints.split("[、,，\\n]"))
                        .map(String::trim)
                        .filter(item -> !item.isBlank())
                        .limit(6)
                        .toList(),
                "下单后交付文档、封面方案、文案模板和私信话术，可按当天项目直接套用。",
                "不承诺具体收益，内容需结合账号实际情况二次调整，避免夸大宣传和违规导流。"
        );
    }

    private List<DmScript> buildDmScripts(String productName) {
        return List.of(
                new DmScript("初次咨询", "可以的，你现在是刚准备做，还是已经发过几篇了？我先按你的阶段给你一版更合适的方向。"),
                new DmScript("价格咨询", productName + "有基础版和进阶版，先看你是想要直接发内容，还是还需要商品承接话术。"),
                new DmScript("犹豫观望", "没关系，你可以先把现在最卡的一点发我，我帮你判断是不是适合用这套模板。"),
                new DmScript("想要资料", "有的，我先发你一份基础清单，你看完如果觉得方向对，再决定要不要拿完整版本。"),
                new DmScript("成交引导", "如果你今天就想开始，我建议先拿基础版跑 3 条内容，有反馈后再决定要不要升级。")
        );
    }

    private String targetUserShort(String targetUser) {
        if (targetUser.contains("新手")) {
            return "新手";
        }
        if (targetUser.length() > 8) {
            return targetUser.substring(0, 8);
        }
        return targetUser;
    }

    private String text(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
