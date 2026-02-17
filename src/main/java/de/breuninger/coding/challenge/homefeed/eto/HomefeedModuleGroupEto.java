package de.breuninger.coding.challenge.homefeed.eto;

import java.util.List;

public record HomefeedModuleGroupEto(String id, String type, String displayType, List<HomefeedModuleEto> items) {
}
