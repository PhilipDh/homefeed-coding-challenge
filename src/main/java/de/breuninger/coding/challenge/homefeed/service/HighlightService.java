package de.breuninger.coding.challenge.homefeed.service;

import de.breuninger.coding.challenge.homefeed.repository.HighlightRepository;
import de.breuninger.coding.challenge.homefeed.repository.entity.HighlightEntity;
import de.breuninger.coding.challenge.homefeed.service.mapper.HomefeedEntryMapper;
import de.breuninger.coding.challenge.homefeed.service.module.HomefeedEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class HighlightService {
    private static final Logger logger = LoggerFactory.getLogger(HighlightService.class);

    private  final HighlightRepository highlightRepository;
    private final PersonalizationEngine personalizationEngine;

    public HighlightService(HighlightRepository highlightRepository,
                            PersonalizationEngine personalizationEngine) {
        this.highlightRepository = highlightRepository;
        this.personalizationEngine = personalizationEngine;
    }

    public List<HomefeedEntry> getHighlightsForUser(UserContext userContext) {
        if (userContext.isAnonymous()) {
            return getPopularHighlights();
        }

        List<HighlightEntity> allHighlights = highlightRepository.findAll();
        LocalDateTime today = LocalDateTime.now();

        List<HighlightEntity> activeHighlights = allHighlights.stream()
                .filter(h -> isActive(h, today))
                .toList();

        List<HighlightEntity> matchingHighlights = personalizationEngine.filterByCategories(activeHighlights,
                userContext.preferredCategories(),
                HighlightEntity::getTargetCategories);

        if (matchingHighlights.isEmpty()) {
            return getPopularHighlights();
        }

        long seed = (userContext.userId().hashCode() * 31L) + today.toEpochSecond(ZoneOffset.UTC);
        List<HighlightEntity> shuffled = new java.util.ArrayList<>(matchingHighlights);
        Collections.shuffle(shuffled, new Random(seed));

        return shuffled.stream()
                .limit(2)
                .map(HomefeedEntryMapper::toHighlightEntry)
                .collect(Collectors.toUnmodifiableList());
    }

    public List<HomefeedEntry> getPopularHighlights() {
        logger.debug("Fetching popular highlights");
        LocalDateTime today = LocalDateTime.now();

        List<HomefeedEntry> popularHighlights = highlightRepository.findAll().stream()
                .filter(h -> isActive(h, today))
                .limit(2)
                .map(HomefeedEntryMapper::toHighlightEntry)
                .collect(Collectors.toUnmodifiableList());

        logger.debug("Returning {} popular highlights", popularHighlights.size());
        return popularHighlights;
    }

    private boolean isActive(HighlightEntity highlight, LocalDateTime date) {
        return !date.isBefore(highlight.getValidFrom()) && !date.isAfter(highlight.getValidUntil());
    }
}
