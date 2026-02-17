package de.breuninger.coding.challenge.homefeed.service.module.banner;

import de.breuninger.coding.challenge.homefeed.config.HomefeedModuleConfigurationProperties;
import de.breuninger.coding.challenge.homefeed.repository.BannerRepository;
import de.breuninger.coding.challenge.homefeed.repository.entity.BannerEntity;
import de.breuninger.coding.challenge.homefeed.service.ModuleDisplayTypeEnum;
import de.breuninger.coding.challenge.homefeed.service.UserContext;
import de.breuninger.coding.challenge.homefeed.service.mapper.HomefeedEntryMapper;
import de.breuninger.coding.challenge.homefeed.service.module.HomefeedEntry;
import de.breuninger.coding.challenge.homefeed.service.module.HomefeedModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BannerModule implements HomefeedModule {
    private static final Logger logger = LoggerFactory.getLogger(BannerModule.class);

    private static final String TYPE = "banner";
    private static final int DEFAULT_PRIORITY = 10;

    private final BannerRepository bannerRepository;
    private final HomefeedModuleConfigurationProperties homefeedModuleConfigProperties;

    public BannerModule(BannerRepository bannerRepository,
                        HomefeedModuleConfigurationProperties homefeedModuleConfigProperties) {
        this.bannerRepository = bannerRepository;
        this.homefeedModuleConfigProperties = homefeedModuleConfigProperties;
    }

    @Override
    public List<HomefeedEntry> getEntries(UserContext context) {
        logger.debug("Getting banners for {} user",
                context.isAnonymous() ? "anonymous" : "authenticated");

        List<BannerEntity> bannerEntities = bannerRepository.findAllActive();

        return bannerEntities.stream()
                .filter(item -> matchesUserSegments(item.getTargetUserSegments(), context.userSegments()))
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

    @Override
    public ModuleDisplayTypeEnum getDisplayType() {
        return ModuleDisplayTypeEnum.CAROUSEL;
    }

    private boolean matchesUserSegments(List<String> targetSegments, Set<String> userSegments) {
        return targetSegments.stream().anyMatch(userSegments::contains);
    }
}