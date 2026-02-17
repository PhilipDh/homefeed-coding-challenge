package de.breuninger.coding.challenge.homefeed.service.module;

import de.breuninger.coding.challenge.homefeed.service.ModuleDisplayTypeEnum;
import de.breuninger.coding.challenge.homefeed.service.UserContext;

import java.util.List;

public interface HomefeedModule {

    List<HomefeedEntry> getEntries(UserContext context);
    String getType();
    int getPriority();
    ModuleDisplayTypeEnum getDisplayType();
}
