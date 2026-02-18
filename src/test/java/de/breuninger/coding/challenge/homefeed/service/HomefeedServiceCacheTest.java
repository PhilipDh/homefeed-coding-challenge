package de.breuninger.coding.challenge.homefeed.service;

import de.breuninger.coding.challenge.homefeed.service.module.HomefeedModuleGroup;
import de.breuninger.coding.challenge.homefeed.testdata.TestDataConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.cache.type=caffeine")
@Import(TestDataConfiguration.class)
class HomefeedServiceCacheTest {

    @Autowired
    private HomefeedService homefeedService;

    @Autowired
    private CacheManager cacheManager;

    @Test
    void shouldCacheHomefeedResponseByUserId() {
        // Given
        String userId = "user123";
        var cache = cacheManager.getCache("homefeed");
        assertThat(cache).isNotNull();

        // When
        List<HomefeedModuleGroup> firstResult = homefeedService.getHomefeed(userId);

        // Then
        assertThat(firstResult).isNotEmpty();
        assertThat(cache.get(userId)).isNotNull();
        assertThat(Objects.requireNonNull(cache.get(userId)).get()).isEqualTo(firstResult);

        // When
        List<HomefeedModuleGroup> secondResult = homefeedService.getHomefeed(userId);

        // Then
        assertThat(secondResult).isSameAs(firstResult);
    }

    @Test
    void shouldCacheAnonymousUsersSeparately() {
        // Given
        var cache = cacheManager.getCache("homefeed");
        assertThat(cache).isNotNull();

        // When
        List<HomefeedModuleGroup> anonymousResult = homefeedService.getHomefeed(null);

        // Then
        assertThat(anonymousResult).isNotEmpty();
        assertThat(cache.get("anonymous")).isNotNull();
        assertThat(Objects.requireNonNull(cache.get("anonymous")).get()).isEqualTo(anonymousResult);
    }

    @Test
    void shouldMaintainSeparateCacheEntriesPerUser() {
        // Given
        String user1 = "user123";
        String user2 = "user456";

        // When
        homefeedService.getHomefeed(user1);
        homefeedService.getHomefeed(user2);

        var cache = cacheManager.getCache("homefeed");
        assertThat(cache).isNotNull();
        assertThat(cache.get(user1)).isNotNull();
        assertThat(cache.get(user2)).isNotNull();

        // Verify they're cached separately (even if content happens to be equal)
        assertThat(Objects.requireNonNull(cache.get(user1)).get())
                .isNotSameAs(Objects.requireNonNull(cache.get(user2)).get());
    }
}

