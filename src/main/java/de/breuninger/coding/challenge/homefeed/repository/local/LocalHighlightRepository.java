package de.breuninger.coding.challenge.homefeed.repository.local;

import de.breuninger.coding.challenge.homefeed.repository.HighlightRepository;
import de.breuninger.coding.challenge.homefeed.repository.entity.HighlightEntity;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class LocalHighlightRepository implements HighlightRepository {
    private final Map<Long, HighlightEntity> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public List<HighlightEntity> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public HighlightEntity save(HighlightEntity highlight) {
        if(highlight.getId() == null) {
            highlight.setId(idGenerator.getAndIncrement());
        }

        storage.put(highlight.getId(), highlight);
        return highlight;
    }

    public void clear() {
        storage.clear();
    }
}
