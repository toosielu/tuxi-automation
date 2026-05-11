import * as React from "react";
import { cn } from "@/lib/utils";

export function Card({ className, ...props }: React.HTMLAttributes<HTMLDivElement>) {
  return (
    <div
      className={cn("rounded-studio border border-line bg-white/76 shadow-soft backdrop-blur-xl", className)}
      {...props}
    />
  );
}
