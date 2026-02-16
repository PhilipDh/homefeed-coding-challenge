package de.breuninger.coding.challenge.homefeed.eto;

public record BannerModuleEto(String title, String message, String bannerType, String imageUrl,
                              String iconName, String actionUrl, String actionLabel) implements HomefeedModuleEto {
}
