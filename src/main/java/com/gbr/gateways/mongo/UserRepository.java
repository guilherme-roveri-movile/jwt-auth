package com.gbr.gateways.mongo;

import com.gbr.domains.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(final String username);
}
