import type { CoverPlan, PostImagePlan } from "@/lib/types";

type DownloadAsset = CoverPlan | PostImagePlan;

export function downloadSquareImage(asset: DownloadAsset, filename: string) {
  const canvas = document.createElement("canvas");
  const size = 1200;
  canvas.width = size;
  canvas.height = size;
  const ctx = canvas.getContext("2d");
  if (!ctx) return;

  const title = "coverTitle" in asset ? asset.coverTitle : asset.imageTitle;
  const subtitle = "coverSubtitle" in asset ? asset.coverSubtitle : asset.imageSubtitle;

  const gradient = ctx.createLinearGradient(0, 0, size, size);
  gradient.addColorStop(0, "#fffdf8");
  gradient.addColorStop(0.64, "#fff4e6");
  gradient.addColorStop(1, "#ffe7ec");
  ctx.fillStyle = gradient;
  ctx.fillRect(0, 0, size, size);

  roundRect(ctx, 88, 90, 420, 74, 37, "rgba(255,36,66,0.12)");
  ctx.fillStyle = "#ff2442";
  ctx.font = "900 34px Microsoft YaHei, PingFang SC, Arial";
  ctx.fillText(subtitle || "小红书内容图", 120, 139);

  ctx.fillStyle = "#171717";
  ctx.font = "900 104px Microsoft YaHei, PingFang SC, Arial";
  wrapText(ctx, title || "小红书爆款内容", 92, 350, 1010, 128, 4);

  roundRect(ctx, 92, 840, 1016, 218, 44, "rgba(23,23,23,0.06)");
  ctx.fillStyle = "#171717";
  ctx.font = "900 34px Microsoft YaHei, PingFang SC, Arial";
  ctx.fillText("图喜 AI 内容生成工厂", 136, 908);
  ctx.fillStyle = "#5f5a52";
  ctx.font = "700 30px Microsoft YaHei, PingFang SC, Arial";
  wrapText(ctx, asset.visualStyle || asset.layoutSuggestion || "真实、干净、适合收藏", 136, 970, 930, 44, 2);

  const link = document.createElement("a");
  link.download = filename;
  link.href = canvas.toDataURL("image/png");
  link.click();
}

function roundRect(
  ctx: CanvasRenderingContext2D,
  x: number,
  y: number,
  width: number,
  height: number,
  radius: number,
  fillStyle: string
) {
  ctx.beginPath();
  ctx.moveTo(x + radius, y);
  ctx.lineTo(x + width - radius, y);
  ctx.quadraticCurveTo(x + width, y, x + width, y + radius);
  ctx.lineTo(x + width, y + height - radius);
  ctx.quadraticCurveTo(x + width, y + height, x + width - radius, y + height);
  ctx.lineTo(x + radius, y + height);
  ctx.quadraticCurveTo(x, y + height, x, y + height - radius);
  ctx.lineTo(x, y + radius);
  ctx.quadraticCurveTo(x, y, x + radius, y);
  ctx.closePath();
  ctx.fillStyle = fillStyle;
  ctx.fill();
}

function wrapText(
  ctx: CanvasRenderingContext2D,
  text: string,
  x: number,
  y: number,
  maxWidth: number,
  lineHeight: number,
  maxLines: number
) {
  const chars = [...text];
  let line = "";
  let lines = 0;
  for (const char of chars) {
    const next = line + char;
    if (ctx.measureText(next).width > maxWidth && line) {
      ctx.fillText(line, x, y);
      y += lineHeight;
      lines += 1;
      line = char;
      if (lines >= maxLines - 1) break;
    } else {
      line = next;
    }
  }
  if (line && lines < maxLines) ctx.fillText(line, x, y);
}
