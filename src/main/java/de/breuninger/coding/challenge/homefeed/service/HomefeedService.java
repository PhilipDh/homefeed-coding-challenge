package de.breuninger.coding.challenge.homefeed.service;

import de.breuninger.coding.challenge.homefeed.repository.UserRepository;
import de.breuninger.coding.challenge.homefeed.repository.entity.UserEntity;
import de.breuninger.coding.challenge.homefeed.service.module.HomefeedModule;
import de.breuninger.coding.challenge.homefeed.service.module.HomefeedModuleGroup;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class HomefeedService {
    private static final Logger logger = LoggerFactory.getLogger(HomefeedService.class);
    private final List<HomefeedModule> homefeedModules;
    private final UserRepository userRepository;

    public HomefeedService(List<HomefeedModule> homefeedModules, UserRepository userRepository) {
        this.homefeedModules = homefeedModules;
        this.userRepository = userRepository;
    }

    public List<HomefeedModuleGroup> getHomefeed(String userId) {
        logger.info("Processing homefeed for user {}", StringUtils.isBlank(userId) ? "anonymous" : userId);

        UserContext context = buildUserContext(userId);

        logger.atDebug().log("Fetching homefeed from modules: {}",
                homefeedModules.stream().map(item -> item.getClass().getName()).toList());

        return homefeedModules.stream()
                .sorted(Comparator.comparingInt(HomefeedModule::getPriority))
                .map(module -> {
                    String moduleId = generateModuleId(module.getType());
                    return new HomefeedModuleGroup(
                            moduleId,
                            module.getType(),
                            module.getDisplayType(),
                            module.getEntries(context)
                    );
                })
                .filter(group -> !group.entries().isEmpty())
                .toList();
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
