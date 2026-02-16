package de.breuninger.coding.challenge.homefeed.repository.entity;

import java.time.LocalDateTime;
import java.util.List;

public class HighlightEntity {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private List<String> targetCategories; // TODO maybe later make this a separte table categories, same as user entity

    private LocalDateTime validFrom;
    private LocalDateTime validUntil;

    public HighlightEntity(Long id, String title, String description, String imageUrl,
                           List<String> targetCategories, LocalDateTime validFrom, LocalDateTime validUntil) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.targetCategories = targetCategories;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<String> getTargetCategories() {
        return targetCategories;
    }

    public void setTargetCategories(List<String> targetCategories) {
        this.targetCategories = targetCategories;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDateTime validUntil) {
        this.validUntil = validUntil;
    }
}
