import type { Config } from "tailwindcss";

const config: Config = {
  darkMode: ["class"],
  content: [
    "./app/**/*.{ts,tsx}",
    "./components/**/*.{ts,tsx}",
    "./lib/**/*.{ts,tsx}"
  ],
  theme: {
    extend: {
      colors: {
        ink: "#171717",
        paper: "#f7f3ec",
        panel: "#fffaf2",
        muted: "#7b746b",
        xhs: "#ff2442",
        line: "rgba(23, 23, 23, 0.08)"
      },
      boxShadow: {
        soft: "0 24px 80px rgba(23, 23, 23, 0.08)",
        lift: "0 18px 54px rgba(255, 36, 66, 0.18)"
      },
      borderRadius: {
        studio: "28px"
      }
    }
  },
  plugins: []
};

export default config;
