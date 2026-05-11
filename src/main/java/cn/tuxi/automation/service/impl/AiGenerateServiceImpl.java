package cn.tuxi.automation.service.impl;

import cn.tuxi.automation.domain.AiGeneratedContent;
import cn.tuxi.automation.domain.GenerateRequest;
import cn.tuxi.automation.domain.GenerateResult;
import cn.tuxi.automation.domain.HistoryRecord;
import cn.tuxi.automation.service.AiContentProvider;
import cn.tuxi.automation.service.AiGenerateService;
import cn.tuxi.automation.service.HistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AiGenerateServiceImpl implements AiGenerateService {
    private static final Logger log = LoggerFactory.getLogger(AiGenerateServiceImpl.class);

    private final HistoryService historyService;
    private final List<AiContentProvider> providers;

    public AiGenerateServiceImpl(HistoryService historyService, List<AiContentProvider> providers) {
        this.historyService = historyService;
        this.providers = providers;
    }

    @Override
    public GenerateResult generate(GenerateRequest request) {
        String id = UUID.randomUUID().toString();
        Instant createdAt = Instant.now();
        AiGeneratedContent content = generateContent(request);
        GenerateResult result = new GenerateResult(
                id,
                content.covers(),
                content.posts(),
                content.productCopy(),
                content.dmScripts(),
                createdAt
        );

        historyService.save(new HistoryRecord(
                id,
                text(request.niche(), "小红书虚拟资料"),
                text(request.productName(), "虚拟资料包"),
                text(request.targetUser(), "想做副业的新手"),
                content.covers(),
                content.posts(),
                content.productCopy(),
                content.dmScripts(),
                createdAt
        ));
        return result;
    }

    private AiGeneratedContent generateContent(GenerateRequest request) {
        RuntimeException lastError = null;
        for (AiContentProvider provider : providers) {
            if (!provider.supports()) {
                continue;
            }
            try {
                return provider.generate(request);
            } catch (RuntimeException error) {
                lastError = error;
                log.warn("AI content provider {} failed, trying next provider", provider.getClass().getSimpleName(), error);
            }
        }
        throw lastError == null ? new IllegalStateException("No AI content provider available") : lastError;
    }

    private String text(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
