package de.breuninger.coding.challenge.homefeed.repository.local;

import de.breuninger.coding.challenge.homefeed.repository.BannerRepository;
import de.breuninger.coding.challenge.homefeed.repository.entity.BannerEntity;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class LocalBannerRepository implements BannerRepository {

    private final Map<Long, BannerEntity> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public List<BannerEntity> findAllActive() {
        LocalDateTime now = LocalDateTime.now();

        return storage.values().stream()
                .filter(item -> item.getValidUntil().isAfter(now) && item.getValidFrom().isBefore(now))
                .toList();
    }

    @Override
    public BannerEntity save(BannerEntity notification) {
        if (notification.getId() == null) {
            notification.setId(idGenerator.getAndIncrement());
        }

        storage.put(notification.getId(), notification);
        return notification;
    }
}
