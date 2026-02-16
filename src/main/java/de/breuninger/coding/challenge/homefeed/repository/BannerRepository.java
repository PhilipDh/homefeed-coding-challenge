package de.breuninger.coding.challenge.homefeed.repository;

import de.breuninger.coding.challenge.homefeed.repository.entity.BannerEntity;

import java.util.List;

public interface BannerRepository {
    List<BannerEntity> findAllActive();

    BannerEntity save(BannerEntity notification);
}
