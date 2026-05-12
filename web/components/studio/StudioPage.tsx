"use client";

import { FormEvent, useMemo, useState } from "react";
import { AnimatePresence, motion } from "framer-motion";
import { Copy, Download, ImageIcon, Loader2, RefreshCcw, Sparkles, Wand2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { generateContent, generateImage } from "@/lib/api";
import { downloadDataUrl, downloadSquareImage } from "@/lib/download-cover";
import type { CoverPlan, GeneratedImage, GenerateRequest, GenerateResult, PostImagePlan, XiaohongshuPost } from "@/lib/types";

const initialForm: GenerateRequest = {
  sourceType: "TEXT",
  sourceText: "想做一条小红书虚拟电商引流贴，用户是副业新手，最卡的是不知道怎么把产品卖点讲得像真人经验。",
  niche: "小红书虚拟电商",
  productName: "虚拟电商爆款资料包",
  targetUser: "想做副业的新手",
  sellingPoints: "低成本、可复制、适合新手、自动发货",
  goal: "引流涨粉",
  style: "避坑型",
  painPoint: "不知道发什么内容，也不知道怎么承接成交"
};

export function StudioPage() {
  const [form, setForm] = useState<GenerateRequest>(initialForm);
  const [result, setResult] = useState<GenerateResult | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [imageLoadingKey, setImageLoadingKey] = useState("");
  const [generatedImages, setGeneratedImages] = useState<Record<string, GeneratedImage>>({});
  const [toast, setToast] = useState("");

  const hasResult = Boolean(result);
  const pairedResults = useMemo(() => {
    if (!result) return [];
    return result.covers.map((cover, index) => ({
      cover,
      postImage: result.postImages[index],
      post: result.posts[index]
    }));
  }, [result]);

  async function onSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setIsLoading(true);
    try {
      const data = await generateContent(form);
      setResult(data);
      showToast("已生成 3 套小红书内容");
    } catch (error) {
      showToast(error instanceof Error ? error.message : "生成失败");
    } finally {
      setIsLoading(false);
    }
  }

  async function copyText(text: string) {
    await navigator.clipboard.writeText(text);
    showToast("已复制");
  }

  async function onGenerateImage(key: string, prompt: string, imageType: string) {
    setImageLoadingKey(key);
    try {
      const image = await generateImage({
        prompt,
        niche: form.niche,
        imageType,
        style: form.style
      });
      setGeneratedImages((current) => ({ ...current, [key]: image }));
      showToast("AI 图片已生成");
    } catch (error) {
      showToast(error instanceof Error ? error.message : "生图失败");
    } finally {
      setImageLoadingKey("");
    }
  }

  function showToast(message: string) {
    setToast(message);
    window.setTimeout(() => setToast(""), 1800);
  }

  return (
    <main className="relative min-h-screen overflow-hidden px-5 py-6 lg:px-8">
      <div className="studio-grid pointer-events-none absolute inset-x-0 top-0 -z-10 h-[560px]" />
      <header className="mx-auto flex max-w-7xl items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="grid h-11 w-11 place-items-center rounded-2xl bg-ink text-sm font-black text-white shadow-soft">图</div>
          <div>
            <div className="text-base font-black tracking-tight">图喜 AI 小红书内容生成工厂</div>
            <div className="text-xs font-bold text-muted">AI Xiaohongshu Content Factory</div>
          </div>
        </div>
        <Button variant="outline" size="sm">MVP Studio</Button>
      </header>

      <section className="mx-auto max-w-7xl pb-16 pt-14">
        <motion.div
          initial={{ opacity: 0, y: 18 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.45 }}
          className="mx-auto max-w-4xl text-center"
        >
          <div className="mb-4 inline-flex items-center gap-2 rounded-full border border-line bg-white/70 px-4 py-2 text-xs font-black uppercase tracking-wide text-muted shadow-sm backdrop-blur">
            <span className="h-2 w-2 rounded-full bg-xhs" />
            AI Studio / Content Workspace / Viral Assets
          </div>
          <h1 className="text-4xl font-black tracking-tight sm:text-5xl lg:text-7xl">
            一键生成能发布的小红书图文
          </h1>
          <p className="mx-auto mt-5 max-w-2xl text-base font-semibold leading-8 text-muted">
            输入赛道、产品、人群和卖点，自动生成封面图方案、帖子配图方案、爆款文案和 CTA 引导。
          </p>
        </motion.div>

        <div className="mt-10 grid gap-6 lg:grid-cols-[430px_minmax(0,1fr)]">
          <Card className="p-5 sm:p-6">
            <form onSubmit={onSubmit} className="space-y-4">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-xs font-black uppercase tracking-wide text-xhs">Input</p>
                  <h2 className="mt-1 text-2xl font-black">产品信息</h2>
                </div>
                <Sparkles className="h-5 w-5 text-xhs" />
              </div>

              <Field label="赛道">
                <Input value={form.niche} onChange={(event) => setForm({ ...form, niche: event.target.value })} />
              </Field>
              <Field label="产品名称">
                <Input value={form.productName} onChange={(event) => setForm({ ...form, productName: event.target.value })} />
              </Field>
              <Field label="目标人群">
                <Input value={form.targetUser} onChange={(event) => setForm({ ...form, targetUser: event.target.value })} />
              </Field>
              <Field label="产品卖点">
                <Textarea value={form.sellingPoints} onChange={(event) => setForm({ ...form, sellingPoints: event.target.value })} />
              </Field>

              <details className="rounded-2xl border border-line bg-black/[0.025] p-4">
                <summary className="cursor-pointer text-sm font-black text-muted">高级选项</summary>
                <div className="mt-4 grid gap-4">
                  <Field label="内容风格">
                    <select className="h-[52px] w-full rounded-2xl border border-line bg-panel/80 px-4 text-[15px] font-semibold outline-none" value={form.style} onChange={(event) => setForm({ ...form, style: event.target.value })}>
                      <option>避坑型</option>
                      <option>干货型</option>
                      <option>结果型</option>
                      <option>真实记录型</option>
                      <option>清单型</option>
                    </select>
                  </Field>
                  <Field label="内容目的">
                    <select className="h-[52px] w-full rounded-2xl border border-line bg-panel/80 px-4 text-[15px] font-semibold outline-none" value={form.goal} onChange={(event) => setForm({ ...form, goal: event.target.value })}>
                      <option>引流涨粉</option>
                      <option>私信咨询</option>
                      <option>产品成交</option>
                      <option>信任建立</option>
                    </select>
                  </Field>
                  <Field label="补充素材">
                    <Textarea value={form.sourceText} onChange={(event) => setForm({ ...form, sourceText: event.target.value })} />
                  </Field>
                </div>
              </details>

              <Button className="w-full" size="lg" variant="gradient" disabled={isLoading}>
                {isLoading ? <Loader2 className="h-5 w-5 animate-spin" /> : <Wand2 className="h-5 w-5" />}
                一键生成小红书帖子
              </Button>
            </form>
          </Card>

          <section>
            <div className="mb-4 flex items-end justify-between gap-4">
              <div>
                <p className="text-xs font-black uppercase tracking-wide text-muted">Output</p>
                <h2 className="mt-1 text-2xl font-black">生成结果</h2>
              </div>
              {hasResult ? (
                <Button variant="outline" size="sm" onClick={() => setResult(null)}>
                  <RefreshCcw className="h-4 w-4" />
                  清空
                </Button>
              ) : null}
            </div>

            <AnimatePresence mode="popLayout">
              {!hasResult && !isLoading ? <EmptyState key="empty" /> : null}
              {isLoading ? <LoadingCards key="loading" /> : null}
              {result ? (
                <motion.div
                  key="result"
                  initial={{ opacity: 0, y: 18 }}
                  animate={{ opacity: 1, y: 0 }}
                  className="grid gap-5 xl:grid-cols-3"
                >
                  {pairedResults.map((item, index) => (
                    <ResultCard
                      key={`${result.id}-${index}`}
                      index={index}
                      cover={item.cover}
                      postImage={item.postImage}
                      post={item.post}
                      onCopy={copyText}
                      onGenerateImage={onGenerateImage}
                      imageLoadingKey={imageLoadingKey}
                      generatedImages={generatedImages}
                    />
                  ))}
                </motion.div>
              ) : null}
            </AnimatePresence>
          </section>
        </div>
      </section>

      <AnimatePresence>
        {toast ? (
          <motion.div
            initial={{ opacity: 0, y: 16, x: "-50%" }}
            animate={{ opacity: 1, y: 0, x: "-50%" }}
            exit={{ opacity: 0, y: 16, x: "-50%" }}
            className="fixed bottom-6 left-1/2 z-50 rounded-full bg-ink px-4 py-3 text-sm font-black text-white shadow-soft"
          >
            {toast}
          </motion.div>
        ) : null}
      </AnimatePresence>
    </main>
  );
}

function Field({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <label className="block">
      <span className="mb-2 block text-sm font-black text-muted">{label}</span>
      {children}
    </label>
  );
}

function EmptyState() {
  return (
    <Card className="grid min-h-[520px] place-items-center p-8 text-center">
      <div className="max-w-sm">
        <div className="mx-auto grid h-14 w-14 place-items-center rounded-2xl bg-ink text-white">
          <ImageIcon className="h-6 w-6" />
        </div>
        <h3 className="mt-5 text-2xl font-black">内容资产会在这里生成</h3>
        <p className="mt-3 text-sm font-semibold leading-7 text-muted">每次生成 3 套封面图、帖子配图和文案，适合直接复制发布。</p>
      </div>
    </Card>
  );
}

function LoadingCards() {
  return (
    <div className="grid gap-5 xl:grid-cols-3">
      {[0, 1, 2].map((item) => (
        <Card key={item} className="h-[620px] animate-pulse bg-white/60 p-4">
          <div className="aspect-square rounded-[24px] bg-black/[0.06]" />
          <div className="mt-5 h-5 rounded-full bg-black/[0.06]" />
          <div className="mt-3 h-24 rounded-2xl bg-black/[0.04]" />
        </Card>
      ))}
    </div>
  );
}

function ResultCard({
  index,
  cover,
  postImage,
  post,
  onCopy,
  onGenerateImage,
  imageLoadingKey,
  generatedImages
}: {
  index: number;
  cover: CoverPlan;
  postImage?: PostImagePlan;
  post?: XiaohongshuPost;
  onCopy: (text: string) => void;
  onGenerateImage: (key: string, prompt: string, imageType: string) => void;
  imageLoadingKey: string;
  generatedImages: Record<string, GeneratedImage>;
}) {
  const coverKey = `cover-${index}`;
  const postImageKey = `post-image-${index}`;
  const generatedCover = generatedImages[coverKey];
  const generatedPostImage = generatedImages[postImageKey];
  const copyPayload = [
    `标题：${post?.title ?? cover.coverTitle}`,
    "",
    post?.hook,
    "",
    post?.content,
    "",
    post?.cta,
    "",
    post?.tags?.join(" ")
  ].filter(Boolean).join("\n");

  return (
    <motion.article layout className="group rounded-studio border border-line bg-white/78 p-4 shadow-soft backdrop-blur-xl transition hover:-translate-y-1 hover:bg-white">
      <Preview title={cover.coverTitle} subtitle={cover.coverSubtitle} badge={`方案 ${index + 1}`} imageUrl={generatedCover?.dataUrl} />
      <div className="mt-4 space-y-3">
        <div className="flex items-start justify-between gap-3">
          <div>
            <p className="text-xs font-black uppercase tracking-wide text-xhs">Cover</p>
            <h3 className="mt-1 text-xl font-black leading-snug">{cover.coverTitle}</h3>
          </div>
        </div>
        <Info label="封面 Prompt" value={cover.imagePrompt} />
        {generatedPostImage ? (
          <img src={generatedPostImage.dataUrl} alt="AI 生成帖子配图" className="aspect-square w-full rounded-[24px] border border-line object-cover" />
        ) : null}
        {postImage ? (
          <Info label="帖子配图" value={`${postImage.imageTitle}｜${postImage.imagePrompt}`} />
        ) : null}
        {post ? (
          <div className="rounded-3xl bg-black/[0.035] p-4">
            <p className="text-xs font-black text-xhs">{post.postType}</p>
            <h4 className="mt-2 text-base font-black">{post.title}</h4>
            <p className="mt-3 text-sm font-semibold leading-7 text-muted">{post.content}</p>
            <p className="mt-3 text-sm font-black text-ink">{post.cta}</p>
          </div>
        ) : null}
        <div className="flex flex-wrap gap-2 pt-1">
          <Button size="sm" onClick={() => onCopy(copyPayload)}>
            <Copy className="h-4 w-4" />
            复制文案
          </Button>
          <Button size="sm" variant="outline" onClick={() => downloadSquareImage(cover, `tuxi-cover-${index + 1}.png`)}>
            <Download className="h-4 w-4" />
            模板封面
          </Button>
          <Button size="sm" variant="outline" disabled={imageLoadingKey === coverKey} onClick={() => onGenerateImage(coverKey, cover.imagePrompt, "小红书爆款封面")}>
            {imageLoadingKey === coverKey ? <Loader2 className="h-4 w-4 animate-spin" /> : <ImageIcon className="h-4 w-4" />}
            AI 生封面
          </Button>
          {generatedCover ? (
            <Button size="sm" variant="outline" onClick={() => downloadDataUrl(generatedCover.dataUrl, `tuxi-ai-cover-${index + 1}.png`)}>
              <Download className="h-4 w-4" />
              下载 AI 封面
            </Button>
          ) : null}
          {postImage ? (
            <Button size="sm" variant="outline" disabled={imageLoadingKey === postImageKey} onClick={() => onGenerateImage(postImageKey, postImage.imagePrompt, "小红书帖子配图")}>
              {imageLoadingKey === postImageKey ? <Loader2 className="h-4 w-4 animate-spin" /> : <ImageIcon className="h-4 w-4" />}
              AI 生配图
            </Button>
          ) : null}
          {postImage ? (
            <Button size="sm" variant="outline" onClick={() => downloadSquareImage(postImage, `tuxi-post-image-${index + 1}.png`)}>
              <Download className="h-4 w-4" />
              模板配图
            </Button>
          ) : null}
          {generatedPostImage ? (
            <Button size="sm" variant="outline" onClick={() => downloadDataUrl(generatedPostImage.dataUrl, `tuxi-ai-post-image-${index + 1}.png`)}>
              <Download className="h-4 w-4" />
              下载 AI 配图
            </Button>
          ) : null}
        </div>
      </div>
    </motion.article>
  );
}

function Preview({ title, subtitle, badge, imageUrl }: { title: string; subtitle: string; badge: string; imageUrl?: string }) {
  return (
    <div className="relative aspect-square overflow-hidden rounded-[26px] border border-line bg-[linear-gradient(135deg,#fffdf8,#fff3e2_62%,#ffe7ec)] p-5">
      {imageUrl ? (
        <img src={imageUrl} alt="AI 生成小红书封面" className="absolute inset-0 h-full w-full object-cover" />
      ) : null}
      <div className={imageUrl ? "absolute inset-0 bg-gradient-to-b from-black/35 via-transparent to-black/35" : ""} />
      <div className="relative flex items-center justify-between">
        <span className="rounded-full bg-xhs/10 px-3 py-1 text-xs font-black text-xhs">{subtitle}</span>
        <span className="text-xs font-black text-muted">{badge}</span>
      </div>
      {!imageUrl ? (
        <>
          <div className="relative flex h-[72%] items-center">
            <h3 className="text-4xl font-black leading-tight tracking-tight">{title}</h3>
          </div>
          <div className="relative text-xs font-black text-muted">图喜 AI 生成</div>
        </>
      ) : null}
    </div>
  );
}

function Info({ label, value }: { label: string; value: string }) {
  return (
    <p className="text-sm font-semibold leading-6 text-muted">
      <span className="font-black text-ink">{label}：</span>
      {value}
    </p>
  );
}
