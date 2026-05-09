const http = require("http");
const fs = require("fs");
const path = require("path");

const PORT = process.env.PORT || 3000;
const ROOT = __dirname;
const PUBLIC_DIR = path.join(ROOT, "public");
const PRODUCTS = JSON.parse(fs.readFileSync(path.join(ROOT, "data", "products.json"), "utf8"));
const schedules = [];

const mimeTypes = {
  ".html": "text/html; charset=utf-8",
  ".css": "text/css; charset=utf-8",
  ".js": "application/javascript; charset=utf-8",
  ".json": "application/json; charset=utf-8",
  ".png": "image/png",
  ".jpg": "image/jpeg",
  ".jpeg": "image/jpeg",
  ".svg": "image/svg+xml; charset=utf-8"
};

function readBody(req) {
  return new Promise((resolve, reject) => {
    let body = "";
    req.on("data", chunk => {
      body += chunk;
      if (body.length > 1_000_000) {
        req.destroy();
        reject(new Error("请求体过大"));
      }
    });
    req.on("end", () => {
      if (!body) {
        resolve({});
        return;
      }
      try {
        resolve(JSON.parse(body));
      } catch (error) {
        reject(new Error("JSON 格式不正确"));
      }
    });
  });
}

function sendJson(res, data, status = 200) {
  res.writeHead(status, { "Content-Type": "application/json; charset=utf-8" });
  res.end(JSON.stringify(data, null, 2));
}

function sendError(res, message, status = 400) {
  sendJson(res, { error: message }, status);
}

function scoreNiche(input) {
  const text = `${input.niche || ""} ${input.audience || ""} ${input.goal || ""}`;
  const hasUrgentDemand = /(副业|赚钱|变现|考证|育儿|效率|简历|小红书|资料|模板|AI|求职)/i.test(text);
  const hasVirtualFit = /(资料|模板|SOP|AI|教程|表格|清单|话术|运营|副业|虚拟)/i.test(text);
  const hasHighRisk = /(医疗|法律|投资|彩票|减肥|金融|玄学|侵权|搬运)/i.test(text);
  const time = Number(input.dailyMinutes || 30);
  const budget = Number(input.budget || 300);

  const demand = hasUrgentDemand ? 84 : 68;
  const monetization = hasVirtualFit ? 88 : 64;
  const execution = time >= 45 ? 82 : 70;
  const budgetFit = budget >= 300 ? 78 : 68;
  const risk = hasHighRisk ? 42 : 78;
  const total = Math.round((demand * 0.28) + (monetization * 0.28) + (execution * 0.18) + (budgetFit * 0.1) + (risk * 0.16));

  return {
    total,
    demand,
    monetization,
    execution,
    budgetFit,
    risk,
    level: total >= 82 ? "优先测试" : total >= 70 ? "可以小规模验证" : "暂缓，先换细分切口",
    summary: hasHighRisk
      ? "赛道存在较高合规风险，建议先换成资料、模板、效率工具等低风险交付。"
      : "赛道具备虚拟产品承接空间，适合先用内容测试咨询，再用低价产品跑首单。"
  };
}

function matchProducts(input) {
  const stage = input.stage || "新手";
  const niche = input.niche || "";
  const scored = PRODUCTS.map(product => {
    let score = 50;
    if (product.audience.some(item => stage.includes(item) || item.includes(stage))) score += 18;
    if (niche && product.category.includes(niche)) score += 16;
    if (/小红书|运营|起号/.test(niche + product.category)) score += 10;
    if (/AI|批量|自动/.test(input.goal || "") && product.category.includes("AI")) score += 12;
    if (stage.includes("已有") && product.category.includes("私域")) score += 10;
    return { ...product, matchScore: Math.min(score, 96) };
  });

  return scored.sort((a, b) => b.matchScore - a.matchScore).slice(0, 3);
}

function buildPostAngles(input, product) {
  const audience = input.audience || "想做副业的新手";
  const niche = input.niche || "小红书虚拟电商";
  const productName = product?.name || "虚拟资料包";
  const pain = input.pain || "不知道卖什么、发什么、怎么出单";
  return [
    {
      angle: "痛点清单",
      title: `新手做${niche}，最容易卡住的 5 个地方`,
      coverText: "别再盲目发帖了",
      body: `${audience}常见的问题不是不努力，而是链路没搭好。先确认人群和痛点，再选一个可交付的${productName}，最后用固定内容模板持续测试咨询。`,
      cta: "想要起盘清单，可以评论“路线”。"
    },
    {
      angle: "结果路径",
      title: `从 0 跑通${niche}，先别急着做矩阵`,
      coverText: "先跑通第一单",
      body: `第一阶段只做三件事：筛一个低风险赛道，准备一个能自动交付的产品，连续发布 10 条围绕同一痛点的笔记。数据稳定后再放大。`,
      cta: "需要选品表和发帖模板，可以私信“起盘”。"
    },
    {
      angle: "避坑反差",
      title: `为什么你发了很多小红书，还是没有人买？`,
      coverText: "不是流量少，是承接断了",
      body: `如果内容只讲概念，没有引导到店铺或私域，流量很容易浪费。每条笔记都要明确：解决什么问题、适合谁、下一步怎么领或怎么买。`,
      cta: "我整理了转化话术模板，想看可以留言。"
    },
    {
      angle: "产品展示",
      title: `${productName}怎么包装，用户才愿意下单？`,
      coverText: "虚拟产品包装公式",
      body: `包装虚拟产品时，不要只写“资料很全”。要写清楚使用场景、交付内容、节省时间、适合人群和售后方式。用户买的是更快解决${pain}。`,
      cta: "想要详情页结构，评论“详情页”。"
    },
    {
      angle: "行动任务",
      title: `今天用 30 分钟，搭一个${niche}测试闭环`,
      coverText: "今日起盘任务",
      body: `第 1 步选一个人群，第 2 步确定一个资料型产品，第 3 步写 3 个痛点标题，第 4 步设置店铺承接入口。先验证有人咨询，再优化产品。`,
      cta: "收藏后照着做，卡住可以私信我。"
    }
  ];
}

function imagePrompts(input, product) {
  const niche = input.niche || "小红书虚拟电商";
  const productName = product?.name || "虚拟资料包";
  return [
    `小红书封面图，主题“${niche}起盘路线”，干净白底，红色重点标注，包含流程箭头和清单感，适合知识类账号`,
    `小红书信息图，展示“赛道-选品-发帖-店铺-私域”五步成交链路，现代工作台风格，高级但清晰`,
    `产品展示图，${productName}资料包界面 mockup，表格、文档、SOP 叠放，强调可执行和自动交付`
  ];
}

function createPlan(input) {
  const score = scoreNiche(input);
  const products = matchProducts(input);
  const primaryProduct = products[0];
  const posts = buildPostAngles(input, primaryProduct);
  return {
    project: {
      name: input.projectName || `${input.niche || "小红书虚拟电商"}自动化项目`,
      stage: input.stage || "新手起盘",
      createdAt: new Date().toISOString()
    },
    nicheScore: score,
    products,
    posts,
    imagePrompts: imagePrompts(input, primaryProduct),
    storeCopy: {
      title: `${primaryProduct.name}｜新手可执行版`,
      subtitle: `适合${input.audience || "想做副业的新手"}，按步骤完成从内容到成交的测试闭环。`,
      bullets: primaryProduct.sellingPoints,
      delivery: primaryProduct.delivery,
      risk: primaryProduct.risk
    },
    privateDomainSop: [
      "成交后 24 小时内发送欢迎语和资料领取方式",
      "第 1 天确认用户目标和当前卡点，打标签",
      "第 3 天提醒完成第一个小任务，收集反馈",
      "第 5 天推送案例复盘和进阶资料",
      "第 7 天根据完成情况推荐进阶包或陪跑服务"
    ],
    nextActions: [
      "先选择 1 个主推产品，不要同时测试太多方向",
      "连续发布 5 条不同角度笔记，记录收藏和私信数据",
      "把咨询问题回写到内容库，生成下一轮选题",
      "有首单后再优化详情页和私域承接"
    ]
  };
}

function serveStatic(req, res) {
  const urlPath = decodeURIComponent(req.url.split("?")[0]);
  const safePath = path.normalize(urlPath === "/" ? "/index.html" : urlPath).replace(/^(\.\.[/\\])+/, "");
  const filePath = path.join(PUBLIC_DIR, safePath);

  if (!filePath.startsWith(PUBLIC_DIR)) {
    sendError(res, "禁止访问", 403);
    return;
  }

  fs.readFile(filePath, (error, content) => {
    if (error) {
      sendError(res, "页面不存在", 404);
      return;
    }
    const ext = path.extname(filePath).toLowerCase();
    res.writeHead(200, { "Content-Type": mimeTypes[ext] || "application/octet-stream" });
    res.end(content);
  });
}

async function handleApi(req, res) {
  try {
    if (req.method === "GET" && req.url === "/api/products") {
      sendJson(res, { products: PRODUCTS });
      return;
    }

    if (req.method === "POST" && req.url === "/api/plan") {
      const input = await readBody(req);
      if (!input.niche && !input.goal) {
        sendError(res, "请至少填写赛道或目标");
        return;
      }
      sendJson(res, createPlan(input));
      return;
    }

    if (req.method === "POST" && req.url === "/api/schedule") {
      const item = await readBody(req);
      const saved = { id: `s${Date.now()}`, status: "待发布", ...item };
      schedules.unshift(saved);
      sendJson(res, { schedule: saved, schedules });
      return;
    }

    if (req.method === "GET" && req.url === "/api/schedule") {
      sendJson(res, { schedules });
      return;
    }

    sendError(res, "API 不存在", 404);
  } catch (error) {
    sendError(res, error.message || "服务异常", 500);
  }
}

const server = http.createServer((req, res) => {
  if (req.url.startsWith("/api/")) {
    handleApi(req, res);
    return;
  }
  serveStatic(req, res);
});

server.listen(PORT, () => {
  console.log(`Tuxi automation MVP running at http://localhost:${PORT}`);
});
