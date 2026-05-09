const form = document.querySelector("#projectForm");
const demoButton = document.querySelector("#demoButton");
const emptyState = document.querySelector("#emptyState");
const summaryCards = document.querySelector("#summaryCards");
const sections = ["niche", "products", "posts", "schedule"].map(id => document.querySelector(`#${id}`));

let currentPlan = null;

demoButton.addEventListener("click", () => {
  const values = {
    projectName: "小红书副业资料号",
    niche: "小红书虚拟电商",
    audience: "想做副业的新手",
    stage: "新手起盘",
    dailyMinutes: 45,
    budget: 300,
    goal: "资料包成交",
    pain: "不知道选什么产品，帖子发了没人咨询，也不知道怎么承接到店铺和私域"
  };
  Object.entries(values).forEach(([key, value]) => {
    const field = form.elements[key];
    if (field) field.value = value;
  });
});

form.addEventListener("submit", async event => {
  event.preventDefault();
  const payload = Object.fromEntries(new FormData(form).entries());
  setLoading(true);
  try {
    const response = await fetch("/api/plan", {
      method: "POST",
      headers: { "Content-Type": "application/json; charset=utf-8" },
      body: JSON.stringify(payload)
    });
    const data = await response.json();
    if (!response.ok) throw new Error(data.error || "生成失败");
    currentPlan = normalizePlan(data);
    renderPlan(currentPlan);
  } catch (error) {
    alert(error.message);
  } finally {
    setLoading(false);
  }
});

function setLoading(isLoading) {
  const button = form.querySelector(".primary-button");
  button.disabled = isLoading;
  button.textContent = isLoading ? "系统生成中..." : "生成成交作战方案";
}

function renderPlan(plan) {
  emptyState.classList.add("hidden");
  summaryCards.classList.remove("hidden");
  sections.forEach(section => section.classList.remove("hidden"));
  document.querySelectorAll(".pipeline-step").forEach(step => step.classList.add("done"));

  summaryCards.innerHTML = `
    <div class="summary-card"><strong>${plan.nicheScore.total}</strong><span>赛道综合分</span></div>
    <div class="summary-card"><strong>${plan.products.length}</strong><span>匹配产品</span></div>
    <div class="summary-card"><strong>${plan.posts.length}</strong><span>生成帖子</span></div>
  `;

  renderNiche(plan.nicheScore);
  renderProducts(plan.products);
  renderPosts(plan.posts, plan.imagePrompts);
  renderSchedule(plan);
}

function renderNiche(score) {
  const target = document.querySelector("#nicheResult");
  target.innerHTML = `
    <div class="score-ring" style="--score:${score.total}">
      <span>${score.total}</span>
    </div>
    <div>
      <h3>${score.level}</h3>
      <p>${score.summary}</p>
      <div class="metric-grid">
        ${metric("需求强度", score.demand)}
        ${metric("变现适配", score.monetization)}
        ${metric("执行难度", score.execution)}
        ${metric("预算适配", score.budgetFit)}
        ${metric("合规安全", score.risk)}
      </div>
    </div>
  `;
}

function metric(label, value) {
  return `<div class="metric-card"><b>${label}</b><span>${value}</span></div>`;
}

function renderProducts(products) {
  document.querySelector("#productResult").innerHTML = products.map(product => `
    <article class="product-card">
      <h3>${product.name}</h3>
      <div class="tag-row">
        <span class="tag">匹配 ${product.matchScore}</span>
        <span class="tag">${product.type}</span>
        <span class="tag">${product.priceRange}</span>
      </div>
      <p><b>交付：</b>${product.delivery}</p>
      <p><b>难度：</b>${product.difficulty}</p>
      <p><b>风险：</b>${product.risk}</p>
      <div class="tag-row">
        ${product.sellingPoints.map(point => `<span class="tag">${point}</span>`).join("")}
      </div>
    </article>
  `).join("");
}

function renderPosts(posts, prompts) {
  document.querySelector("#postResult").innerHTML = posts.map((post, index) => `
    <article class="post-card">
      <div class="tag-row">
        <span class="tag">笔记 ${index + 1}</span>
        <span class="tag">${post.angle}</span>
      </div>
      <h3>${post.title}</h3>
      <p><b>封面：</b>${post.coverText}</p>
      <p>${post.body}</p>
      <p><b>CTA：</b>${post.cta}</p>
      <button class="small-button" type="button" onclick="saveSchedule(${index})">加入发布排程</button>
    </article>
  `).join("");

  document.querySelector("#imagePrompts").innerHTML = prompts.map(prompt => `
    <div class="prompt-item">${prompt}</div>
  `).join("");
}

function renderSchedule(plan) {
  const store = document.querySelector("#storeCopy");
  store.innerHTML = `
    <div class="store-card">
      <h3>店铺上架文案</h3>
      <p><b>${plan.storeCopy.title}</b></p>
      <p>${plan.storeCopy.subtitle}</p>
      <div class="tag-row">${plan.storeCopy.bullets.map(item => `<span class="tag">${item}</span>`).join("")}</div>
      <p><b>交付：</b>${plan.storeCopy.delivery}</p>
      <p><b>风险：</b>${plan.storeCopy.risk}</p>
    </div>
    <div class="store-card">
      <h3>私域 7 天承接 SOP</h3>
      ${plan.privateDomainSop.map(item => `<p>${item}</p>`).join("")}
    </div>
  `;

  const list = document.querySelector("#scheduleResult");
  list.innerHTML = plan.nextActions.map((item, index) => `
    <div class="schedule-item">
      <h3>下一步 ${index + 1}</h3>
      <p>${item}</p>
    </div>
  `).join("");
}

async function saveSchedule(index) {
  if (!currentPlan) return;
  const post = currentPlan.posts[index];
  const response = await fetch("/api/schedule", {
    method: "POST",
    headers: { "Content-Type": "application/json; charset=utf-8" },
    body: JSON.stringify({
      title: post.title,
      coverText: post.coverText,
      cta: post.cta,
      scheduledAt: new Date(Date.now() + (index + 1) * 86400000).toISOString()
    })
  });
  const data = await response.json();
  if (!response.ok) {
    alert(data.error || "保存失败");
    return;
  }
  const list = document.querySelector("#scheduleResult");
  list.insertAdjacentHTML("afterbegin", `
    <div class="schedule-item">
      <h3>${data.schedule.status}｜${data.schedule.title}</h3>
      <p>计划发布时间：${new Date(data.schedule.scheduledAt).toLocaleString()}</p>
      <p>封面：${data.schedule.coverText}｜CTA：${data.schedule.cta}</p>
    </div>
  `);
}

window.saveSchedule = saveSchedule;
