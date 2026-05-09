package cn.tuxi.automation.mapper.memory;

import cn.tuxi.automation.domain.Product;
import cn.tuxi.automation.mapper.ProductMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InMemoryProductMapper implements ProductMapper {
    private final List<Product> products = List.of(
            new Product("p001", "副业起盘资料包", "副业", List.of("新手", "上班族", "宝妈"), "资料包", "19-99", "网盘自动交付", "低", "避免收益承诺，强调方法和流程", List.of("低成本起步", "7 天任务表", "适合零经验")),
            new Product("p002", "小红书起号 SOP 模板", "小红书运营", List.of("新手", "已有账号"), "模板", "39-199", "文档 + 表格交付", "中", "避免平台规则绝对化表达", List.of("账号定位", "内容节奏", "复盘指标")),
            new Product("p003", "虚拟产品选品库", "虚拟电商", List.of("新手", "已有店铺"), "产品库", "49-299", "表格 + 更新包", "中", "标注版权和合规检查项", List.of("蓝海方向", "定价参考", "交付难度")),
            new Product("p004", "私域成交话术包", "私域转化", List.of("已有账号", "已有店铺"), "话术包", "99-499", "文档 + 案例库", "中", "避免夸大转化率", List.of("欢迎话术", "跟进节奏", "复购触发")),
            new Product("p005", "AI 内容批量生产工作流", "AI 提效", List.of("新手", "已有账号", "团队"), "工作流", "199-980", "提示词 + SOP + 表格", "高", "人工二次校对，降低同质化", List.of("批量选题", "标题模板", "内容复盘"))
    );

    @Override
    public List<Product> findAll() {
        return products;
    }
}
