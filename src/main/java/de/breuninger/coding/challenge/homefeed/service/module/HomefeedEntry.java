package de.breuninger.coding.challenge.homefeed.service.module;

public sealed interface HomefeedEntry permits BannerEntry, GreetingEntry, HighlightEntry {
}
