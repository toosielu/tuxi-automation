package cn.tuxi.automation.service.impl;

import cn.tuxi.automation.domain.ViralContentTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ViralContentTemplateLibrary {
    private final List<ViralContentTemplate> templates = List.of(
            new ViralContentTemplate(
                    "痛点避坑型",
                    "目标人群 + 先别 + 常见错误动作",
                    "很多人不是不会做，而是一开始就把动作做重了",
                    "先指出错误做法，再给一个低成本替代动作，最后引导用户用小步骤测试",
                    "低压互动",
                    List.of("私信咨询", "店铺点击", "引流涨粉"),
                    "不夸大收益，不制造焦虑，不承诺具体结果"
            ),
            new ViralContentTemplate(
                    "清单干货型",
                    "可收藏清单 + 数字结果 + 适用场景",
                    "我会先把复杂动作拆成一张清单",
                    "按步骤列出用户今天能照做的动作，每一步都给判断标准",
                    "收藏互动",
                    List.of("引流涨粉", "信任建立"),
                    "避免空泛教程，必须给具体动作和检查点"
            ),
            new ViralContentTemplate(
                    "结果对比型",
                    "错误写法 vs 正确写法 + 结果变化",
                    "同一个产品，换个表达方式，用户反应会完全不一样",
                    "展示错误表达、改后表达和背后的用户心理差异",
                    "轻转化",
                    List.of("店铺点击", "私信咨询"),
                    "不使用绝对化对比，不暗示必然爆单"
            ),
            new ViralContentTemplate(
                    "真实记录型",
                    "真实过程 + 小结果 + 复盘结论",
                    "这不是一条标准教程，更像我自己复盘出来的步骤",
                    "用第一人称写执行过程、卡点、调整和下一步动作",
                    "信任互动",
                    List.of("信任建立", "引流涨粉"),
                    "保留真实限制，不包装成轻松成功故事"
            ),
            new ViralContentTemplate(
                    "成交承接型",
                    "产品适合谁 + 能解决什么 + 怎么用",
                    "别只看资料多不多，先看你拿到后能不能当天用起来",
                    "先讲适合人群，再讲使用步骤，最后给低压购买建议",
                    "成交引导",
                    List.of("产品成交", "店铺点击"),
                    "先交付价值再成交，不强推，不承诺收益"
            )
    );

    public ViralContentTemplate selectFor(String goal, String style) {
        String normalizedGoal = text(goal);
        String normalizedStyle = text(style);
        if (normalizedGoal.contains("产品成交")) {
            return findByName("成交承接型");
        }
        if (normalizedStyle.contains("清单") || normalizedGoal.contains("引流涨粉")) {
            return findByName("清单干货型");
        }
        if (normalizedStyle.contains("真实") || normalizedGoal.contains("信任")) {
            return findByName("真实记录型");
        }
        if (normalizedStyle.contains("结果") || normalizedStyle.contains("反差") || normalizedGoal.contains("店铺点击")) {
            return findByName("结果对比型");
        }
        return findByName("痛点避坑型");
    }

    public List<ViralContentTemplate> list() {
        return templates;
    }

    private ViralContentTemplate findByName(String name) {
        return templates.stream()
                .filter(template -> template.name().equals(name))
                .findFirst()
                .orElse(templates.get(0));
    }

    private String text(String value) {
        return value == null ? "" : value;
    }
}
