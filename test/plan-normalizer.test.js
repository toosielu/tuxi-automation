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
    posts: [],
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
  assert.equal(normalized.products[0].name, "副业起盘资料包");
  assert.equal(normalized.products[0].matchScore, 84);
  assert.deepEqual(normalized.products[0].sellingPoints, ["低成本起步"]);
  assert.deepEqual(normalized.storeCopy.bullets, ["低成本起步"]);
});
