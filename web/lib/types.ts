export type GenerateRequest = {
  sourceType: "TEXT";
  sourceText: string;
  niche: string;
  productName: string;
  targetUser: string;
  sellingPoints: string;
  goal: string;
  style: string;
  painPoint?: string;
};

export type CoverPlan = {
  coverTitle: string;
  coverSubtitle: string;
  visualStyle: string;
  layoutSuggestion: string;
  imagePrompt: string;
  reason: string;
};

export type PostImagePlan = {
  imageTitle: string;
  imageSubtitle: string;
  visualStyle: string;
  layoutSuggestion: string;
  imagePrompt: string;
  matchedPostType: string;
  reason: string;
};

export type XiaohongshuPost = {
  postType: string;
  title: string;
  hook: string;
  content: string;
  cta: string;
  tags: string[];
  reason: string;
};

export type ProductCopy = {
  productTitle: string;
  productSubtitle: string;
  sellingPoints: string[];
  deliveryNote: string;
  riskNotice: string;
};

export type DmScript = {
  scene: string;
  message: string;
};

export type GenerateResult = {
  id: string;
  covers: CoverPlan[];
  postImages: PostImagePlan[];
  posts: XiaohongshuPost[];
  productCopy: ProductCopy;
  dmScripts: DmScript[];
  createdAt: string;
};

export type ImageGenerateRequest = {
  prompt: string;
  niche: string;
  imageType: string;
  style: string;
};

export type GeneratedImage = {
  id: string;
  prompt: string;
  imageBase64: string;
  mimeType: string;
  dataUrl: string;
  provider: string;
  model: string;
};
