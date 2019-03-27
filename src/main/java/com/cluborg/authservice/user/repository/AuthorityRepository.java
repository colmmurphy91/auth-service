package com.cluborg.authservice.user.repository;

import com.cluborg.authservice.user.domain.Authority;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author duc-d
 * Spring Data MongoDB repository for the Authority entity.
 */
@Repository
public interface AuthorityRepository extends ReactiveMongoRepository<Authority, String> {
}
