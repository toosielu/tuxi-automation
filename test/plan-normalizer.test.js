const test = require("node:test");
const assert = require("node:assert/strict");
const { normalizePlan } = require("../public/plan-normalizer.js");

test("normalizes Java plan response for the frontend renderer", () => {
  const normalized = normalizePlan({
    nicheScore: {
      totalScore: 83,
      demandScore: 84,
      monetizationScore: 88,
      executionScore: 82,
      budgetFitScore: 78,
      riskScore: 78,
      level: "优先测试",
      summary: "适合测试"
    },
    viralTopics: [
      {
        type: "痛点清单",
        title: "新手做小红书虚拟电商，最容易卡住的 7 个地方",
        coverText: "别再盲目发帖了",
        hook: "很多人不是不努力，是链路没搭好。",
        trafficScore: 92,
        reason: "痛点具体，适合收藏。",
        monetizationPath: "承接到店铺资料包"
      }
    ],
    products: [
      {
        product: {
          name: "副业起盘资料包",
          type: "资料包",
          priceRange: "19-99",
          delivery: "网盘自动交付",
          difficulty: "低",
          risk: "避免收益承诺",
          sellingPoints: ["低成本起步"]
        },
        matchScore: 84,
        reason: "适合当前目标人群"
      }
    ],
    posts: [
      {
        contentType: "引流爆款",
        angle: "痛点反差",
        title: "别急着发",
        coverText: "先看这里",
        imageBrief: "手机备忘录截图感",
        body: "像真人复盘",
        cta: "评论"
      }
    ],
    imagePrompts: [],
    storeCopy: {
      title: "副业起盘资料包",
      subtitle: "适合新手",
      bullets: ["低成本起步"],
      delivery: "网盘自动交付",
      risk: "避免收益承诺"
    },
    privateDomainSop: [],
    nextActions: []
  });

  assert.equal(normalized.nicheScore.total, 83);
  assert.equal(normalized.nicheScore.demand, 84);
  assert.equal(normalized.viralTopics[0].trafficScore, 92);
  assert.equal(normalized.viralTopics[0].type, "痛点清单");
  assert.equal(normalized.posts[0].contentType, "引流爆款");
  assert.equal(normalized.posts[0].imageBrief, "手机备忘录截图感");
  assert.equal(normalized.products[0].name, "副业起盘资料包");
  assert.equal(normalized.products[0].matchScore, 84);
  assert.deepEqual(normalized.products[0].sellingPoints, ["低成本起步"]);
  assert.deepEqual(normalized.storeCopy.bullets, ["低成本起步"]);
});
