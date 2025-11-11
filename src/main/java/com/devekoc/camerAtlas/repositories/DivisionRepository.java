package com.devekoc.camerAtlas.repositories;

import com.devekoc.camerAtlas.entities.Division;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DivisionRepository extends JpaRepository<Division, Integer> {
    Optional<Division> findByName(String name);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Integer id);
}
