package cn.tuxi.automation.domain;

public record MatchedProduct(Product product, int matchScore, String reason) {
}
