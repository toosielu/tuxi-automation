import { NextRequest, NextResponse } from "next/server";

const springBootBaseUrl = process.env.SPRING_BOOT_API_BASE_URL ?? "http://127.0.0.1:8082";

export async function POST(request: NextRequest) {
  const body = await request.text();
  const response = await fetch(`${springBootBaseUrl}/api/images/generate`, {
    method: "POST",
    headers: { "Content-Type": "application/json; charset=utf-8" },
    body,
    cache: "no-store"
  });

  const text = await response.text();
  return new NextResponse(text, {
    status: response.status,
    headers: {
      "Content-Type": response.headers.get("Content-Type") ?? "application/json; charset=utf-8"
    }
  });
}
