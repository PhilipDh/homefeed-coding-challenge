package de.breuninger.coding.challenge.homefeed.service.module;

import java.util.List;

public record HomefeedModuleGroup(String type, List<HomefeedEntry> entries) {
}
