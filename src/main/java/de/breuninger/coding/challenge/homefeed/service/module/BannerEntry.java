package de.breuninger.coding.challenge.homefeed.service.module;

public record BannerEntry (String title, String message, String bannerType, String imageUrl,
                           String iconName, String actionUrl, String actionLabel) implements HomefeedEntry {
}
