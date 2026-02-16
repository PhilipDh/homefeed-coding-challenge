package de.breuninger.coding.challenge.homefeed.controller.mapper;

import de.breuninger.coding.challenge.homefeed.eto.GreetingModuleEto;
import de.breuninger.coding.challenge.homefeed.eto.HomefeedModuleEto;
import de.breuninger.coding.challenge.homefeed.eto.HomefeedModuleGroupEto;
import de.breuninger.coding.challenge.homefeed.service.module.GreetingEntry;
import de.breuninger.coding.challenge.homefeed.service.module.HomefeedEntry;
import de.breuninger.coding.challenge.homefeed.service.module.HomefeedModuleGroup;

import java.util.List;

public class HomefeedDtoMapper {

    public static HomefeedModuleGroupEto toGroupDto(HomefeedModuleGroup group) {
        List<HomefeedModuleEto> items = group.entries().stream()
                .map(HomefeedDtoMapper::toDto)
                .toList();

        return new HomefeedModuleGroupEto(
                group.type(),
                items
        );
    }

    private static HomefeedModuleEto toDto(HomefeedEntry entry) {
        return switch (entry) {
            case GreetingEntry greetingEntry -> new GreetingModuleEto(greetingEntry.greeting());
        };
    }
}
