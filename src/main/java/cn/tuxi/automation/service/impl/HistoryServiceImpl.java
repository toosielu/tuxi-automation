package cn.tuxi.automation.service.impl;

import cn.tuxi.automation.domain.HistoryRecord;
import cn.tuxi.automation.mapper.HistoryMapper;
import cn.tuxi.automation.service.HistoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HistoryServiceImpl implements HistoryService {
    private final HistoryMapper historyMapper;

    public HistoryServiceImpl(HistoryMapper historyMapper) {
        this.historyMapper = historyMapper;
    }

    @Override
    public void save(HistoryRecord record) {
        historyMapper.insert(record);
    }

    @Override
    public List<HistoryRecord> list() {
        return historyMapper.findAll();
    }

    @Override
    public Optional<HistoryRecord> detail(String id) {
        return historyMapper.findById(id);
    }
}
