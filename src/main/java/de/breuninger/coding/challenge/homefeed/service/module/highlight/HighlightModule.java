package de.breuninger.coding.challenge.homefeed.service.module.highlight;

import de.breuninger.coding.challenge.homefeed.config.HomefeedModuleConfigurationProperties;
import de.breuninger.coding.challenge.homefeed.service.HighlightService;
import de.breuninger.coding.challenge.homefeed.service.ModuleDisplayTypeEnum;
import de.breuninger.coding.challenge.homefeed.service.UserContext;
import de.breuninger.coding.challenge.homefeed.service.module.HomefeedEntry;
import de.breuninger.coding.challenge.homefeed.service.module.HomefeedModule;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HighlightModule implements HomefeedModule {
    private static final String TYPE = "highlight";
    private static final int DEFAULT_PRIORITY = 30;

    private final HighlightService highlightService;
    private final HomefeedModuleConfigurationProperties homefeedModuleConfigProperties;

    public HighlightModule(HighlightService highlightService,
                           HomefeedModuleConfigurationProperties homefeedModuleConfigProperties) {
        this.highlightService = highlightService;
        this.homefeedModuleConfigProperties = homefeedModuleConfigProperties;
    }

    @Override
    public List<HomefeedEntry> getEntries(UserContext context) {
        List<HomefeedEntry> highlights = highlightService.getHighlightsForUser(context);

        return highlights;
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
        return ModuleDisplayTypeEnum.GRID;
    }
}
