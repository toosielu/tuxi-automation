import * as React from "react";
import { Slot } from "@radix-ui/react-slot";
import { cva, type VariantProps } from "class-variance-authority";
import { cn } from "@/lib/utils";

const buttonVariants = cva(
  "inline-flex items-center justify-center gap-2 whitespace-nowrap rounded-2xl text-sm font-black transition-all disabled:pointer-events-none disabled:opacity-60 focus-visible:outline-none focus-visible:ring-4 focus-visible:ring-xhs/15",
  {
    variants: {
      variant: {
        default: "bg-ink text-white shadow-sm hover:-translate-y-0.5 hover:bg-black",
        ghost: "bg-transparent text-ink hover:bg-black/[0.05]",
        outline: "border border-line bg-white/70 text-ink shadow-sm hover:-translate-y-0.5 hover:bg-white",
        gradient: "bg-[linear-gradient(135deg,#171717,#3a211d_48%,#ff2442)] text-white shadow-lift hover:-translate-y-0.5 hover:shadow-[0_24px_70px_rgba(255,36,66,0.24)]"
      },
      size: {
        default: "h-11 px-5",
        sm: "h-9 px-3 text-xs",
        lg: "h-14 px-7 text-base"
      }
    },
    defaultVariants: {
      variant: "default",
      size: "default"
    }
  }
);

export interface ButtonProps
  extends React.ButtonHTMLAttributes<HTMLButtonElement>,
    VariantProps<typeof buttonVariants> {
  asChild?: boolean;
}

export const Button = React.forwardRef<HTMLButtonElement, ButtonProps>(
  ({ className, variant, size, asChild = false, ...props }, ref) => {
    const Comp = asChild ? Slot : "button";
    return <Comp className={cn(buttonVariants({ variant, size, className }))} ref={ref} {...props} />;
  }
);
Button.displayName = "Button";
