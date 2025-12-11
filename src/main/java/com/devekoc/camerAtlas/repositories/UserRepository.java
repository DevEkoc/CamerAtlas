package com.devekoc.camerAtlas.repositories;

import com.devekoc.camerAtlas.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByName(String name);

    boolean existsByEmail(String name);

    Optional<UserDetails> findByEmail(String username);

    boolean existsByPseudo(String pseudo);
}
