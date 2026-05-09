package cn.tuxi.automation.mapper.memory;

import cn.tuxi.automation.domain.HistoryRecord;
import cn.tuxi.automation.mapper.HistoryMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class InMemoryHistoryMapper implements HistoryMapper {
    private final List<HistoryRecord> records = new ArrayList<>();

    @Override
    public synchronized void insert(HistoryRecord record) {
        records.add(0, record);
    }

    @Override
    public synchronized List<HistoryRecord> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(records));
    }

    @Override
    public synchronized Optional<HistoryRecord> findById(String id) {
        return records.stream().filter(record -> record.id().equals(id)).findFirst();
    }
}
