package de.breuninger.coding.challenge.homefeed.service;

import de.breuninger.coding.challenge.homefeed.service.module.HomefeedModule;
import de.breuninger.coding.challenge.homefeed.service.module.HomefeedModuleGroup;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class HomefeedService {
    private final List<HomefeedModule> homefeedModules;

    public HomefeedService(List<HomefeedModule> homefeedModules) {
        this.homefeedModules = homefeedModules;
    }

    public List<HomefeedModuleGroup> getHomefeed(String userId) {
        return homefeedModules.stream()
                .sorted(Comparator.comparingInt(HomefeedModule::getPriority))
                .map(module -> new HomefeedModuleGroup(
                        module.getType(),
                        module.getEntries(userId)
                ))
                .filter(group -> !group.entries().isEmpty())
                .toList();
    }
}
