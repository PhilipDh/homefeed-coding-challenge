package de.breuninger.coding.challenge.homefeed.repository.local;

import de.breuninger.coding.challenge.homefeed.repository.UserRepository;
import de.breuninger.coding.challenge.homefeed.repository.entity.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class LocalUserRepository implements UserRepository {
    private final Map<String, UserEntity> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public UserEntity findById(String userId) {
        return storage.get(userId);
    }

    @Override
    public UserEntity save(UserEntity user) {
        if(user.getId() == null) {
            user.setId(idGenerator.getAndIncrement());
        }

        storage.put(user.getUserId(), user);
        return user;
    }
}
