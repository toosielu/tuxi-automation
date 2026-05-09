(function (root) {
  function normalizePlan(plan) {
    const safePlan = plan || {};
    return {
      ...safePlan,
      nicheScore: normalizeScore(safePlan.nicheScore),
      viralTopics: normalizeViralTopics(safePlan.viralTopics),
      products: normalizeProducts(safePlan.products),
      posts: Array.isArray(safePlan.posts) ? safePlan.posts : [],
      imagePrompts: Array.isArray(safePlan.imagePrompts) ? safePlan.imagePrompts : [],
      storeCopy: normalizeStoreCopy(safePlan.storeCopy),
      privateDomainSop: Array.isArray(safePlan.privateDomainSop) ? safePlan.privateDomainSop : [],
      nextActions: Array.isArray(safePlan.nextActions) ? safePlan.nextActions : []
    };
  }

  function normalizeViralTopics(topics) {
    if (!Array.isArray(topics)) return [];
    return topics.map(topic => ({
      type: topic.type || "选题",
      title: topic.title || "",
      coverText: topic.coverText || "",
      hook: topic.hook || "",
      trafficScore: value(topic.trafficScore, topic.score, 0),
      reason: topic.reason || "",
      monetizationPath: topic.monetizationPath || ""
    }));
  }

  function normalizeScore(score) {
    const source = score || {};
    return {
      ...source,
      total: value(source.total, source.totalScore),
      demand: value(source.demand, source.demandScore),
      monetization: value(source.monetization, source.monetizationScore),
      execution: value(source.execution, source.executionScore),
      budgetFit: value(source.budgetFit, source.budgetFitScore),
      risk: value(source.risk, source.riskScore),
      level: source.level || "待判断",
      summary: source.summary || ""
    };
  }

  function normalizeProducts(products) {
    if (!Array.isArray(products)) return [];
    return products.map(item => {
      const product = item && item.product ? item.product : item || {};
      return {
        ...product,
        matchScore: value(item && item.matchScore, product.matchScore, 0),
        reason: (item && item.reason) || product.reason || "",
        sellingPoints: Array.isArray(product.sellingPoints) ? product.sellingPoints : []
      };
    });
  }

  function normalizeStoreCopy(storeCopy) {
    const source = storeCopy || {};
    return {
      title: source.title || "",
      subtitle: source.subtitle || "",
      bullets: Array.isArray(source.bullets) ? source.bullets : [],
      delivery: source.delivery || "",
      risk: source.risk || ""
    };
  }

  function value(...values) {
    for (const item of values) {
      if (item !== undefined && item !== null) return item;
    }
    return 0;
  }

  root.normalizePlan = normalizePlan;
  if (typeof module !== "undefined" && module.exports) {
    module.exports = { normalizePlan };
  }
})(typeof window !== "undefined" ? window : globalThis);
