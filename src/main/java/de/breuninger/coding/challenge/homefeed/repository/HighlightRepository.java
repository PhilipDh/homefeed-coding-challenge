package de.breuninger.coding.challenge.homefeed.repository;

import de.breuninger.coding.challenge.homefeed.repository.entity.HighlightEntity;

import java.util.List;

public interface HighlightRepository {
    List<HighlightEntity> findAll();
    HighlightEntity save(HighlightEntity highlight);
}
