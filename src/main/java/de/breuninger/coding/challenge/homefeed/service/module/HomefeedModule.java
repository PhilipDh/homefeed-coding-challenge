package de.breuninger.coding.challenge.homefeed.service.module;

import java.util.List;

public interface HomefeedModule {

    List<HomefeedEntry> getEntries(String userId);
    String getType();
    int getPriority();
}
