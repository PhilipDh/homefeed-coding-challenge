package de.breuninger.coding.challenge.homefeed.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Service
public class PersonalizationEngine {
    private static final Logger logger = LoggerFactory.getLogger(PersonalizationEngine.class);

    public <T> List<T> filterByCategories(
            List<T> items,
            Set<String> userPreferredCategories,
            Function<T, List<String>> categoryExtractor) {

        if (userPreferredCategories == null || userPreferredCategories.isEmpty()) {
            logger.debug("No user preferred categories provided, returning all {} items", items.size());
            return items;
        }

        logger.debug("Filtering {} items by {} preferred categories",
                items.size(), userPreferredCategories.size());

        List<T> filtered = items.stream()
                .filter(item -> {
                    List<String> targetCategories = categoryExtractor.apply(item);
                    return hasOverlap(targetCategories, userPreferredCategories);
                })
                .toList();

        logger.debug("Filtered down to {} matching items", filtered.size());
        return filtered;
    }

    private boolean hasOverlap(List<String> targetCategories, Set<String> userCategories) {
        if (targetCategories == null || userCategories == null) {
            return false;
        }
        return targetCategories.stream().anyMatch(userCategories::contains);
    }

}
