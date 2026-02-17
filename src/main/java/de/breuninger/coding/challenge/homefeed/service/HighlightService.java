package de.breuninger.coding.challenge.homefeed.service;

import de.breuninger.coding.challenge.homefeed.repository.HighlightRepository;
import de.breuninger.coding.challenge.homefeed.repository.entity.HighlightEntity;
import de.breuninger.coding.challenge.homefeed.repository.entity.UserEntity;
import de.breuninger.coding.challenge.homefeed.service.mapper.HomefeedEntryMapper;
import de.breuninger.coding.challenge.homefeed.service.module.HomefeedEntry;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class HighlightService {

    private  final HighlightRepository highlightRepository;

    public HighlightService(HighlightRepository highlightRepository) {
        this.highlightRepository = highlightRepository;
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

        List<HighlightEntity> matchingHighlights = activeHighlights.stream()
                .filter(h -> hasOverlap(h.getTargetCategories(), userContext.preferredCategories()))
                .toList();

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
        LocalDateTime today = LocalDateTime.now();

        return highlightRepository.findAll().stream()
                .filter(h -> isActive(h, today))
                .limit(2)
                .map(HomefeedEntryMapper::toHighlightEntry)
                .collect(Collectors.toUnmodifiableList());
    }

    private boolean isActive(HighlightEntity highlight, LocalDateTime date) {
        return !date.isBefore(highlight.getValidFrom()) && !date.isAfter(highlight.getValidUntil());
    }

    private boolean hasOverlap(List<String> targetCategories, Set<String> userCategories) {
        if (targetCategories == null || userCategories == null) {
            return false;
        }
        return targetCategories.stream().anyMatch(userCategories::contains);
    }
}
