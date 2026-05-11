import * as React from "react";
import { cn } from "@/lib/utils";

export const Input = React.forwardRef<HTMLInputElement, React.InputHTMLAttributes<HTMLInputElement>>(
  ({ className, ...props }, ref) => (
    <input
      ref={ref}
      className={cn(
        "h-[52px] w-full rounded-2xl border border-line bg-panel/80 px-4 text-[15px] font-semibold text-ink outline-none transition focus:border-xhs/50 focus:bg-white focus:ring-4 focus:ring-xhs/10",
        className
      )}
      {...props}
    />
  )
);
Input.displayName = "Input";
