package cn.tuxi.automation.service;

import cn.tuxi.automation.domain.HistoryRecord;

import java.util.List;
import java.util.Optional;

public interface HistoryService {
    void save(HistoryRecord record);

    List<HistoryRecord> list();

    Optional<HistoryRecord> detail(String id);
}
