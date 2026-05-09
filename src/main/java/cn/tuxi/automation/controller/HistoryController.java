package cn.tuxi.automation.controller;

import cn.tuxi.automation.domain.HistoryList;
import cn.tuxi.automation.domain.HistoryRecord;
import cn.tuxi.automation.service.HistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/history")
public class HistoryController {
    private final HistoryService historyService;

    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping("/list")
    public HistoryList list() {
        return new HistoryList(historyService.list());
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<HistoryRecord> detail(@PathVariable String id) {
        return historyService.detail(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
