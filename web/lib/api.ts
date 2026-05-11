import type { GenerateRequest, GenerateResult } from "@/lib/types";

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
