let motionAnimate = null;
let motionStagger = null;

try {
  const motion = await import("https://esm.sh/framer-motion/dom");
  motionAnimate = motion.animate;
  motionStagger = motion.stagger;
} catch {
  motionAnimate = null;
  motionStagger = null;
}

const form = document.querySelector("#generateForm");
const demoButton = document.querySelector("#demoButton");
const emptyState = document.querySelector("#emptyState");
const summaryCards = document.querySelector("#summaryCards");
const loadingState = document.querySelector("#loadingState");
const resultSections = ["covers", "posts", "product", "dm"].map(id => document.querySelector(`#${id}`));
const sourceType = document.querySelector("#sourceType");

document.querySelectorAll(".mode-tab").forEach(button => {
  button.addEventListener("click", () => switchMode(button.dataset.mode));
});

demoButton.addEventListener("click", () => {
  switchMode("TEXT");
  const values = {
    sourceText: "我想做一个小红书虚拟电商资料包，用户多是新手，最卡的是不知道发什么内容，也不知道怎么把流量接到商品页。",
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
  const payload = buildPayload();
  setLoading(true);
  try {
    const response = await fetch("/api/ai/generate", {
      method: "POST",
      headers: { "Content-Type": "application/json; charset=utf-8" },
      body: JSON.stringify(payload)
    });
    const data = await response.json();
    if (!response.ok) throw new Error(data.error || "生成失败");
    renderResult(data);
    await loadHistory();
  } catch (error) {
    alert(error.message);
  } finally {
    setLoading(false);
  }
});

function switchMode(mode) {
  sourceType.value = mode;
  document.querySelectorAll(".mode-tab").forEach(tab => {
    tab.classList.toggle("active", tab.dataset.mode === mode);
  });
  document.querySelectorAll(".mode-panel").forEach(panel => {
    panel.classList.toggle("hidden", panel.dataset.panel !== mode);
  });
  safeAnimate(".mode-panel:not(.hidden)", { opacity: [0, 1], y: [8, 0] }, { duration: 0.22 });
}

function buildPayload() {
  const data = Object.fromEntries(new FormData(form).entries());
  if (data.linkNotes && !data.sourceText) {
    data.sourceText = data.linkNotes;
  }
  delete data.linkNotes;
  return data;
}

function setLoading(isLoading) {
  const button = form.querySelector(".generate-button");
  button.disabled = isLoading;
  button.querySelector(".button-label").textContent = isLoading ? "正在生成资产..." : "生成爆款内容";
  button.querySelector(".button-loader").classList.toggle("hidden", !isLoading);
  loadingState.classList.toggle("hidden", !isLoading);
}

function renderResult(result) {
  emptyState.classList.add("hidden");
  summaryCards.classList.remove("hidden");
  summaryCards.classList.add("grid");
  resultSections.forEach(section => section.classList.remove("hidden"));

  summaryCards.innerHTML = `
    ${summaryCard(result.covers.length, "封面方案")}
    ${summaryCard(result.posts.length, "图文文案")}
    ${summaryCard(result.dmScripts.length, "私信话术")}
  `;

  renderCovers(result.covers);
  renderPosts(result.posts);
  renderProductCopy(result.productCopy);
  renderDmScripts(result.dmScripts);
  revealAssets();
}

function summaryCard(count, label) {
  return `
    <div class="rounded-2xl border border-white/10 bg-white/[0.07] p-4">
      <strong class="block text-3xl font-black">${count}</strong>
      <span class="text-xs font-black text-white/55">${label}</span>
    </div>
  `;
}

function renderCovers(covers) {
  document.querySelector("#coverResult").innerHTML = covers.map((cover, index) => `
    <article class="asset-card">
      <div class="xhs-cover">
        <div>
          <span class="inline-flex rounded-full bg-[#ff2442]/10 px-3 py-1 text-xs font-black text-[#ff2442]">${cover.coverSubtitle}</span>
        </div>
        <strong class="block text-3xl font-black leading-tight tracking-tight">${cover.coverTitle}</strong>
        <div class="text-xs font-black text-muted">AI Cover Plan ${index + 1}</div>
      </div>
      <div class="mt-4 space-y-3">
        <div class="flex items-center justify-between gap-3">
          <h3 class="text-lg font-black">${cover.coverTitle}</h3>
          <span class="rounded-full bg-black/[0.06] px-3 py-1 text-xs font-black text-muted">封面</span>
        </div>
        <p class="text-sm font-semibold leading-6 text-muted"><b class="text-ink">风格：</b>${cover.visualStyle}</p>
        <p class="text-sm font-semibold leading-6 text-muted"><b class="text-ink">排版：</b>${cover.layoutSuggestion}</p>
        <p class="text-sm font-semibold leading-6 text-muted"><b class="text-ink">AI Prompt：</b>${cover.imagePrompt}</p>
        <p class="text-sm font-semibold leading-6 text-muted"><b class="text-ink">点击逻辑：</b>${cover.reason}</p>
        <div class="flex gap-2 pt-1">
          <button class="copy-button" type="button" onclick="copyText(${JSON.stringify(toCoverText(cover))})">复制</button>
          <button class="regen-button" type="button" onclick="regenerate()">重新生成</button>
        </div>
      </div>
    </article>
  `).join("");
}

function renderPosts(posts) {
  document.querySelector("#postResult").innerHTML = posts.map((post, index) => `
    <article class="asset-card">
      <div class="mb-4 flex items-center justify-between gap-3">
        <span class="rounded-full bg-[#ff2442]/10 px-3 py-1 text-xs font-black text-[#ff2442]">${post.postType}</span>
        <span class="text-xs font-black text-muted">文案 ${index + 1}</span>
      </div>
      <h3 class="text-xl font-black leading-snug">${post.title}</h3>
      <p class="mt-4 text-sm font-semibold leading-7 text-muted"><b class="text-ink">开头：</b>${post.hook}</p>
      <p class="mt-3 text-sm font-semibold leading-7 text-muted">${post.content}</p>
      <p class="mt-3 text-sm font-semibold leading-7 text-muted"><b class="text-ink">CTA：</b>${post.cta}</p>
      <div class="mt-4 flex flex-wrap gap-2">${post.tags.map(tag => `<span class="rounded-full bg-black/[0.05] px-3 py-1 text-xs font-black text-muted">${tag}</span>`).join("")}</div>
      <p class="mt-4 text-sm font-semibold leading-7 text-muted"><b class="text-ink">爆款逻辑：</b>${post.reason}</p>
      <div class="mt-4 flex gap-2">
        <button class="copy-button" type="button" onclick="copyText(${JSON.stringify(toPostText(post))})">复制</button>
        <button class="regen-button" type="button" onclick="regenerate()">重新生成</button>
      </div>
    </article>
  `).join("");
}

function renderProductCopy(copy) {
  document.querySelector("#productCopyResult").innerHTML = `
    <article class="asset-card">
      <div class="grid gap-5 lg:grid-cols-[1fr_320px]">
        <div>
          <span class="rounded-full bg-black/[0.05] px-3 py-1 text-xs font-black text-muted">商品承接</span>
          <h3 class="mt-4 text-2xl font-black">${copy.productTitle}</h3>
          <p class="mt-3 text-sm font-semibold leading-7 text-muted">${copy.productSubtitle}</p>
          <div class="mt-4 flex flex-wrap gap-2">${copy.sellingPoints.map(point => `<span class="rounded-full bg-[#ff2442]/10 px-3 py-1 text-xs font-black text-[#ff2442]">${point}</span>`).join("")}</div>
        </div>
        <div class="rounded-3xl bg-black/[0.04] p-5 text-sm font-semibold leading-7 text-muted">
          <p><b class="text-ink">交付：</b>${copy.deliveryNote}</p>
          <p class="mt-3"><b class="text-ink">风险：</b>${copy.riskNotice}</p>
        </div>
      </div>
      <div class="mt-5 flex gap-2">
        <button class="copy-button" type="button" onclick="copyText(${JSON.stringify(toProductText(copy))})">复制</button>
        <button class="regen-button" type="button" onclick="regenerate()">重新生成</button>
      </div>
    </article>
  `;
}

function renderDmScripts(scripts) {
  document.querySelector("#dmResult").innerHTML = scripts.map(script => `
    <article class="asset-card">
      <span class="rounded-full bg-black/[0.05] px-3 py-1 text-xs font-black text-muted">${script.scene}</span>
      <p class="mt-4 text-sm font-semibold leading-7 text-muted">${script.message}</p>
      <div class="mt-4 flex gap-2">
        <button class="copy-button" type="button" onclick="copyText(${JSON.stringify(script.message)})">复制</button>
        <button class="regen-button" type="button" onclick="regenerate()">重新生成</button>
      </div>
    </article>
  `).join("");
}

async function loadHistory() {
  const response = await fetch("/api/history/list");
  const data = await response.json();
  const records = data.records || [];
  document.querySelector("#historyResult").innerHTML = records.length ? records.map(record => `
    <article class="asset-card">
      <h3 class="text-lg font-black">${record.productName}</h3>
      <p class="mt-2 text-sm font-semibold text-muted">${record.niche}｜${record.targetUser}</p>
      <p class="mt-3 text-xs font-black text-muted">${new Date(record.createdAt).toLocaleString()}</p>
    </article>
  `).join("") : `<div class="rounded-[28px] border border-black/5 bg-white/60 p-8 text-center text-sm font-bold text-muted">暂无历史记录</div>`;
}

async function copyText(text) {
  await navigator.clipboard.writeText(text);
}

function regenerate() {
  form.requestSubmit();
}

function revealAssets() {
  safeAnimate(".result-section:not(.hidden)", { opacity: [0, 1], y: [18, 0] }, { duration: 0.34, delay: motionStagger ? motionStagger(0.05) : 0 });
  safeAnimate(".asset-card", { opacity: [0, 1], y: [16, 0] }, { duration: 0.28, delay: motionStagger ? motionStagger(0.035) : 0 });
}

function safeAnimate(selector, keyframes, options) {
  if (!motionAnimate) return;
  try {
    motionAnimate(selector, keyframes, options);
  } catch {
    // Framer Motion is progressive enhancement for CDN/offline cases.
  }
}

function toCoverText(cover) {
  return `封面主标题：${cover.coverTitle}\n封面副标题：${cover.coverSubtitle}\n视觉风格：${cover.visualStyle}\n排版建议：${cover.layoutSuggestion}\nAI Prompt：${cover.imagePrompt}\n点击逻辑：${cover.reason}`;
}

function toPostText(post) {
  return `${post.title}\n\n${post.hook}\n\n${post.content}\n\n${post.cta}\n\n${post.tags.join(" ")}`;
}

function toProductText(copy) {
  return `${copy.productTitle}\n${copy.productSubtitle}\n\n卖点：${copy.sellingPoints.join("、")}\n交付：${copy.deliveryNote}\n风险提醒：${copy.riskNotice}`;
}

window.copyText = copyText;
window.regenerate = regenerate;
loadHistory();
