package cn.tuxi.automation.mapper;

import cn.tuxi.automation.domain.HistoryRecord;

import java.util.List;
import java.util.Optional;

public interface HistoryMapper {
    void insert(HistoryRecord record);

    List<HistoryRecord> findAll();

    Optional<HistoryRecord> findById(String id);
}
