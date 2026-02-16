package de.breuninger.coding.challenge.homefeed.service.module.banner;

import de.breuninger.coding.challenge.homefeed.config.HomefeedModuleConfigurationProperties;
import de.breuninger.coding.challenge.homefeed.repository.BannerRepository;
import de.breuninger.coding.challenge.homefeed.repository.UserRepository;
import de.breuninger.coding.challenge.homefeed.repository.entity.BannerEntity;
import de.breuninger.coding.challenge.homefeed.repository.entity.UserEntity;
import de.breuninger.coding.challenge.homefeed.service.mapper.HomefeedEntryMapper;
import de.breuninger.coding.challenge.homefeed.service.module.HomefeedEntry;
import de.breuninger.coding.challenge.homefeed.service.module.HomefeedModule;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BannerModule implements HomefeedModule {
    private static final Logger logger = LoggerFactory.getLogger(BannerModule.class);

    private static final String TYPE = "banner";
    private static final int DEFAULT_PRIORITY = 10;

    private final UserRepository userRepository;
    private final BannerRepository bannerRepository;
    private final HomefeedModuleConfigurationProperties homefeedModuleConfigProperties;

    public BannerModule(UserRepository userRepository,
                        BannerRepository bannerRepository,
                        HomefeedModuleConfigurationProperties homefeedModuleConfigProperties) {
        this.userRepository = userRepository;
        this.bannerRepository = bannerRepository;
        this.homefeedModuleConfigProperties = homefeedModuleConfigProperties;
    }

    @Override
    public List<HomefeedEntry> getEntries(String userId) {
        logger.debug("Getting banners for user {}", StringUtils.isBlank(userId) ? "anonymous" : userId);

        List<BannerEntity> bannerEntities = bannerRepository.findAllActive();
        if(StringUtils.isBlank(userId)) {
            return bannerEntities.stream()
                    .filter(item -> item.getTargetUserSegments().contains("ALL"))
                    .map(HomefeedEntryMapper::toBannerEntry)
                    .collect(Collectors.toUnmodifiableList());

        }

        UserEntity user = userRepository.findById(userId);

        if (user == null) {
            logger.info("Could not find user for Id {}", userId);
            return bannerEntities.stream()
                    .filter(item -> item.getTargetUserSegments().contains("ALL"))
                    .map(HomefeedEntryMapper::toBannerEntry)
                    .collect(Collectors.toUnmodifiableList());
        }

        String userSegment = user.getUserSegment();
        return bannerEntities.stream()
                .filter(item -> matchesUserSegment(item.getTargetUserSegments(), userSegment))
                .map(HomefeedEntryMapper::toBannerEntry)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public int getPriority() {
        return homefeedModuleConfigProperties.getPriorities().getOrDefault(TYPE, DEFAULT_PRIORITY);
    }

    private boolean matchesUserSegment(List<String> targetSegments, String userSegment) {
        return targetSegments.contains("ALL") || targetSegments.contains(userSegment);
    }
}
