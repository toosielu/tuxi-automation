const form = document.querySelector("#generateForm");
const demoButton = document.querySelector("#demoButton");
const emptyState = document.querySelector("#emptyState");
const summaryCards = document.querySelector("#summaryCards");
const resultSections = ["covers", "posts", "product", "dm"].map(id => document.querySelector(`#${id}`));

let currentResult = null;

demoButton.addEventListener("click", () => {
  const values = {
    niche: "小红书虚拟资料",
    productName: "小红书虚拟电商实操资料包",
    targetUser: "想做副业的新手",
    sellingPoints: "低成本、可复制、适合新手、自动发货",
    goal: "产品成交",
    style: "干货型",
    painPoint: "不知道发什么内容，也不知道怎么承接成交"
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
    const response = await fetch("/api/ai/generate", {
      method: "POST",
      headers: { "Content-Type": "application/json; charset=utf-8" },
      body: JSON.stringify(payload)
    });
    const data = await response.json();
    if (!response.ok) throw new Error(data.error || "生成失败");
    currentResult = data;
    renderResult(data);
    await loadHistory();
  } catch (error) {
    alert(error.message);
  } finally {
    setLoading(false);
  }
});

function setLoading(isLoading) {
  const button = form.querySelector(".primary-button");
  button.disabled = isLoading;
  button.textContent = isLoading ? "AI 生成中..." : "生成爆款内容";
}

function renderResult(result) {
  emptyState.classList.add("hidden");
  summaryCards.classList.remove("hidden");
  resultSections.forEach(section => section.classList.remove("hidden"));

  summaryCards.innerHTML = `
    <div class="summary-card"><strong>${result.covers.length}</strong><span>封面方案</span></div>
    <div class="summary-card"><strong>${result.posts.length}</strong><span>小红书文案</span></div>
    <div class="summary-card"><strong>${result.dmScripts.length}</strong><span>私信话术</span></div>
  `;

  renderCovers(result.covers);
  renderPosts(result.posts);
  renderProductCopy(result.productCopy);
  renderDmScripts(result.dmScripts);
}

function renderCovers(covers) {
  document.querySelector("#coverResult").innerHTML = covers.map((cover, index) => `
    <article class="content-card">
      <div class="tag-row">
        <span class="tag">封面 ${index + 1}</span>
      </div>
      <h3>${cover.coverTitle}</h3>
      <p><b>副标题：</b>${cover.coverSubtitle}</p>
      <p><b>视觉：</b>${cover.visualStyle}</p>
      <p><b>排版：</b>${cover.layoutSuggestion}</p>
      <p><b>生图提示词：</b>${cover.imagePrompt}</p>
      <p><b>点击逻辑：</b>${cover.reason}</p>
      <button class="small-button" type="button" onclick="copyText(${JSON.stringify(toCoverText(cover))})">复制封面方案</button>
    </article>
  `).join("");
}

function renderPosts(posts) {
  document.querySelector("#postResult").innerHTML = posts.map((post, index) => `
    <article class="content-card">
      <div class="tag-row">
        <span class="tag">文案 ${index + 1}</span>
        <span class="tag">${post.postType}</span>
      </div>
      <h3>${post.title}</h3>
      <p><b>开头：</b>${post.hook}</p>
      <p>${post.content}</p>
      <p><b>CTA：</b>${post.cta}</p>
      <div class="tag-row">${post.tags.map(tag => `<span class="tag">${tag}</span>`).join("")}</div>
      <p><b>爆款逻辑：</b>${post.reason}</p>
      <button class="small-button" type="button" onclick="copyText(${JSON.stringify(toPostText(post))})">复制文案</button>
    </article>
  `).join("");
}

function renderProductCopy(copy) {
  document.querySelector("#productCopyResult").innerHTML = `
    <article class="content-card">
      <h3>${copy.productTitle}</h3>
      <p>${copy.productSubtitle}</p>
      <div class="tag-row">${copy.sellingPoints.map(point => `<span class="tag">${point}</span>`).join("")}</div>
      <p><b>交付：</b>${copy.deliveryNote}</p>
      <p><b>风险：</b>${copy.riskNotice}</p>
      <button class="small-button" type="button" onclick="copyText(${JSON.stringify(toProductText(copy))})">复制商品文案</button>
    </article>
  `;
}

function renderDmScripts(scripts) {
  document.querySelector("#dmResult").innerHTML = scripts.map(script => `
    <article class="script-item">
      <h3>${script.scene}</h3>
      <p>${script.message}</p>
      <button class="small-button" type="button" onclick="copyText(${JSON.stringify(script.message)})">复制话术</button>
    </article>
  `).join("");
}

async function loadHistory() {
  const response = await fetch("/api/history/list");
  const data = await response.json();
  const records = data.records || [];
  document.querySelector("#historyResult").innerHTML = records.length ? records.map(record => `
    <article class="script-item">
      <h3>${record.productName}</h3>
      <p>${record.niche}｜${record.targetUser}</p>
      <p>${new Date(record.createdAt).toLocaleString()}</p>
    </article>
  `).join("") : `<div class="empty-state small">暂无历史记录</div>`;
}

async function copyText(text) {
  await navigator.clipboard.writeText(text);
}

function toCoverText(cover) {
  return `封面主标题：${cover.coverTitle}\n封面副标题：${cover.coverSubtitle}\n视觉风格：${cover.visualStyle}\n排版建议：${cover.layoutSuggestion}\n生图提示词：${cover.imagePrompt}\n点击逻辑：${cover.reason}`;
}

function toPostText(post) {
  return `${post.title}\n\n${post.hook}\n\n${post.content}\n\n${post.cta}\n\n${post.tags.join(" ")}`;
}

function toProductText(copy) {
  return `${copy.productTitle}\n${copy.productSubtitle}\n\n卖点：${copy.sellingPoints.join("、")}\n交付：${copy.deliveryNote}\n风险提醒：${copy.riskNotice}`;
}

window.copyText = copyText;
loadHistory();
