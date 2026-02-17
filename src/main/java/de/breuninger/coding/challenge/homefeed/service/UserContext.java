package de.breuninger.coding.challenge.homefeed.service;

import java.util.Set;

public record UserContext(
        String userId,
        String firstname,
        String surname,
        Set<String> userSegments,
        boolean isAnonymous
) {

    public static UserContext anonymous() {
        return new UserContext(null, null, null, Set.of("ALL"), true);
    }

    public static UserContext authenticated(String userId, String firstname, String surname, Set<String> userSegments) {
        return new UserContext(userId, firstname, surname, userSegments, false);
    }
}
