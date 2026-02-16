package de.breuninger.coding.challenge.homefeed.repository;

import de.breuninger.coding.challenge.homefeed.repository.entity.UserEntity;

public interface UserRepository {

    UserEntity findById(String userId);

    UserEntity save(UserEntity user);
}
