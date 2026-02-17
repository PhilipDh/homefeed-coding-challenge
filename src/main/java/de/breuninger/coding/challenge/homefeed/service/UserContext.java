package de.breuninger.coding.challenge.homefeed.service;

import java.util.Set;

public record UserContext(
        String userId,
        String firstname,
        String surname,
        Set<String> userSegments,
        boolean isAnonymous,
        Set<String> preferredCategories
) {

    public static UserContext anonymous() {
        return new UserContext(null, null, null, Set.of("ALL"), true, Set.of());
    }

    public static UserContext authenticated(String userId, String firstname, String surname, Set<String> userSegments,  Set<String> preferredCategories) {
        return new UserContext(userId, firstname, surname, userSegments, false, preferredCategories);
    }
}
