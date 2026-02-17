package de.breuninger.coding.challenge.homefeed.service;

public enum ModuleDisplayTypeEnum {
    SINGLE("single"),
    GRID("grid"),
    CAROUSEL("carousel");

    private String value;

    ModuleDisplayTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
