import type { GeneratedImage, GenerateRequest, GenerateResult, ImageGenerateRequest } from "@/lib/types";

export async function generateContent(payload: GenerateRequest): Promise<GenerateResult> {
  const response = await fetch("/api/ai/generate", {
    method: "POST",
    headers: { "Content-Type": "application/json; charset=utf-8" },
    body: JSON.stringify(payload)
  });

  if (!response.ok) {
    const message = await response.text();
    throw new Error(message || "生成失败，请稍后再试");
  }

  return response.json();
}

export async function generateImage(payload: ImageGenerateRequest): Promise<GeneratedImage> {
  const response = await fetch("/api/images/generate", {
    method: "POST",
    headers: { "Content-Type": "application/json; charset=utf-8" },
    body: JSON.stringify(payload)
  });

  if (!response.ok) {
    const message = await response.text();
    throw new Error(message || "生图失败，请检查图片模型 token 配置");
  }

  return response.json();
}
