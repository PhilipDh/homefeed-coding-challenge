package de.breuninger.coding.challenge.homefeed.service;

import de.breuninger.coding.challenge.homefeed.service.module.HomefeedModule;
import de.breuninger.coding.challenge.homefeed.service.module.HomefeedModuleGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class HomefeedService {
    private static final Logger logger = LoggerFactory.getLogger(HomefeedService.class);
    private final List<HomefeedModule> homefeedModules;

    public HomefeedService(List<HomefeedModule> homefeedModules) {
        this.homefeedModules = homefeedModules;
    }

    public List<HomefeedModuleGroup> getHomefeed(String userId) {
        // Avoid streaming all modules when not at debug level, SLF4J 2.4 has native lambda support, but sb 4.02 does use this version // TODO maybe fix
        logger.atDebug().log("fetching homefeed from modules: {}",
                homefeedModules.stream().map(item -> item.getClass().getName()).toList());

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
