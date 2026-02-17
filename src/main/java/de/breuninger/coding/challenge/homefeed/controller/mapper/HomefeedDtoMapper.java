package de.breuninger.coding.challenge.homefeed.controller.mapper;

import de.breuninger.coding.challenge.homefeed.eto.*;
import de.breuninger.coding.challenge.homefeed.service.module.*;

import java.util.List;

public class HomefeedDtoMapper {

    public static HomefeedModuleGroupEto toGroupDto(HomefeedModuleGroup group) {
        List<HomefeedModuleEto> items = group.entries().stream()
                .map(HomefeedDtoMapper::toDto)
                .toList();

        return new HomefeedModuleGroupEto(
                group.id(),
                group.type(),
                items
        );
    }

    private static HomefeedModuleEto toDto(HomefeedEntry entry) {
        return switch (entry) {
            case GreetingEntry greetingEntry -> new GreetingModuleEto(greetingEntry.greeting());
            case BannerEntry bannerEntry -> new BannerModuleEto(bannerEntry.title(), bannerEntry.message(), bannerEntry.bannerType(), bannerEntry.imageUrl(), bannerEntry.iconName(), bannerEntry.actionUrl(), bannerEntry.actionLabel());
            case HighlightEntry highlightEntry -> new HighlightModuleEto(highlightEntry.title(), highlightEntry.description(), highlightEntry.imageUrl());
        };
    }
}
