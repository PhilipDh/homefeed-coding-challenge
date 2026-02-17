package de.breuninger.coding.challenge.homefeed.service.module;

import de.breuninger.coding.challenge.homefeed.service.ModuleDisplayTypeEnum;

import java.util.List;

public record HomefeedModuleGroup(String id, String type, ModuleDisplayTypeEnum displayType, List<HomefeedEntry> entries) {
}
