package de.breuninger.coding.challenge.homefeed.service.mapper;

import de.breuninger.coding.challenge.homefeed.repository.entity.BannerEntity;
import de.breuninger.coding.challenge.homefeed.service.module.BannerEntry;

public class HomefeedEntryMapper {
    public static BannerEntry toBannerEntry(BannerEntity item) {
        return new BannerEntry(
                item.getTitle(),
                item.getMessage(),
                item.getBannerType(),
                item.getImageUrl(),
                item.getIconUrl(),
                item.getActionUrl(),
                item.getActionLabel()
        );
    }
}
