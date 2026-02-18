package de.breuninger.coding.challenge.homefeed.service;

import de.breuninger.coding.challenge.homefeed.config.CacheConfiguration;
import de.breuninger.coding.challenge.homefeed.config.HomefeedModuleConfigurationProperties;
import de.breuninger.coding.challenge.homefeed.repository.UserRepository;
import de.breuninger.coding.challenge.homefeed.repository.entity.UserEntity;
import de.breuninger.coding.challenge.homefeed.service.module.HomefeedModule;
import de.breuninger.coding.challenge.homefeed.service.module.HomefeedModuleGroup;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class HomefeedService {
    private static final Logger logger = LoggerFactory.getLogger(HomefeedService.class);
    private final List<HomefeedModule> homefeedModules;
    private final UserRepository userRepository;
    private final ExecutorService executorService;
    private final HomefeedModuleConfigurationProperties configProperties;

    public HomefeedService(List<HomefeedModule> homefeedModules,
                          UserRepository userRepository,
                          @Qualifier("homefeedModuleExecutor") ExecutorService executorService,
                          HomefeedModuleConfigurationProperties configProperties) {
        this.homefeedModules = homefeedModules;
        this.userRepository = userRepository;
        this.executorService = executorService;
        this.configProperties = configProperties;
    }

    @Cacheable(value = CacheConfiguration.HOMEFEED_CACHE, unless = "#result.isEmpty()", key = "#userId ?: 'anonymous'")
    public List<HomefeedModuleGroup> getHomefeed(String userId) {
        logger.info("Processing homefeed for user {}", StringUtils.isBlank(userId) ? "anonymous" : userId);

        UserContext context = buildUserContext(userId);
        long timeoutMs = configProperties.getExecutionTimeoutMs();

        logger.debug("Fetching homefeed from {} modules in parallel with {}ms timeout",
                homefeedModules.size(), timeoutMs);

        // Module data is fetched in parallel with a timeout that is configured in application properties
        // This makes sure no homefeed request takes too long to return data.
        // Reduces dependency on some other modules that might be overloaded or stuttering
        List<CompletableFuture<HomefeedModuleGroup>> futures = homefeedModules.stream()
                .map(module -> {
                    String moduleType = module.getType();
                    return CompletableFuture.supplyAsync(() -> {
                        try {
                            logger.debug("Processing module: {}", moduleType);
                            String moduleId = generateModuleId(moduleType);
                            HomefeedModuleGroup group = new HomefeedModuleGroup(
                                    moduleId,
                                    moduleType,
                                    module.getDisplayType(),
                                    module.getEntries(context)
                            );
                            if (group.entries().isEmpty()) {
                                logger.debug("Module {} returned no entries", moduleType);
                            }
                            return group;
                        } catch (Exception e) {
                            logger.warn("Module {} failed with exception: {}", moduleType, e.getMessage(), e);
                            return null;
                        }
                    }, executorService)
                    .orTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                    .exceptionally(ex -> {
                        logger.warn("Module {} timed out or failed after {}ms", moduleType, timeoutMs);
                        return null;
                    });
                })
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        List<HomefeedModuleGroup> result = futures.stream()
                .map(CompletableFuture::join)
                .filter(group -> group != null && !group.entries().isEmpty())
                .sorted(Comparator.comparingInt(group ->
                        homefeedModules.stream()
                                .filter(m -> m.getType().equals(group.type()))
                                .findFirst()
                                .map(HomefeedModule::getPriority)
                                .orElse(Integer.MAX_VALUE)
                ))
                .toList();

        logger.info("Generated homefeed with {} non-empty modules (out of {} attempted)",
                result.size(), homefeedModules.size());
        return result;
    }

    private UserContext buildUserContext(String userId) {
        if (StringUtils.isBlank(userId)) {
            logger.debug("Building anonymous user context");
            return UserContext.anonymous();
        }

        UserEntity user = userRepository.findById(userId);
        if (user == null) {
            logger.warn("User {} not found, treating as anonymous", userId);
            return UserContext.anonymous();
        }

        logger.debug("Building authenticated user context for user {}", userId);
        Set<String> userSegments = Set.of(user.getUserSegment(), "ALL");

        return UserContext.authenticated(
                userId,
                user.getFirstname(),
                user.getSurname(),
                userSegments,
                user.getPreferredCategories()
        );
    }

    private String generateModuleId(String moduleType) {
        long timestamp = System.currentTimeMillis();
        String shortUuid = UUID.randomUUID().toString().substring(0, 8);
        return String.format("%s_%d_%s", moduleType, timestamp, shortUuid);
    }
}
