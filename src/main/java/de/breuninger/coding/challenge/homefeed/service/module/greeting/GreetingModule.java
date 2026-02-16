package de.breuninger.coding.challenge.homefeed.service.module.greeting;

import de.breuninger.coding.challenge.homefeed.config.HomefeedModuleConfigurationProperties;
import de.breuninger.coding.challenge.homefeed.repository.UserRepository;
import de.breuninger.coding.challenge.homefeed.repository.entity.UserEntity;
import de.breuninger.coding.challenge.homefeed.service.module.GreetingEntry;
import de.breuninger.coding.challenge.homefeed.service.module.HomefeedEntry;
import de.breuninger.coding.challenge.homefeed.service.module.HomefeedModule;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GreetingModule implements HomefeedModule {
    private static final Logger logger = LoggerFactory.getLogger(GreetingModule.class);

    private static final String TYPE = "greeting";
    private static final int DEFAULT_PRIORITY = 20;

    private final UserRepository userRepository;
    private final HomefeedModuleConfigurationProperties homefeedModuleConfigProperties;

    public GreetingModule(UserRepository userRepository,
                          HomefeedModuleConfigurationProperties homefeedModuleConfigProperties) {
        this.userRepository = userRepository;
        this.homefeedModuleConfigProperties = homefeedModuleConfigProperties;
    }


    @Override
    public List<HomefeedEntry> getEntries(String userId) {
        if(StringUtils.isBlank(userId)) {
            return List.of(new GreetingEntry("Welcome! Discover amazing products"));
        }

        UserEntity user = userRepository.findById(userId);

        if (user == null) {
            logger.info("Could not find user for Id {}", userId);
            return List.of(new GreetingEntry("Welcome! Discover amazing products"));
        }

        // TODO maybe extract to a greeting service, since users might have preferences in how they want to be addressed, also need to check if user has entered all data
        String greeting = "Hello " + user.getFirstname() + " " + user.getSurname();
        return List.of(new GreetingEntry(greeting));
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public int getPriority() {
        return homefeedModuleConfigProperties.getPriorities().getOrDefault(TYPE, DEFAULT_PRIORITY);
    }
}
