import type { Metadata } from "next";
import type { ReactNode } from "react";
import "./globals.css";

export const metadata: Metadata = {
  title: "图喜 AI 小红书内容生成工厂",
  description: "输入产品信息，一键生成小红书封面图方案、帖子配图方案和爆款文案。"
};

export default function RootLayout({ children }: Readonly<{ children: ReactNode }>) {
  return (
    <html lang="zh-CN">
      <body>{children}</body>
    </html>
  );
}
