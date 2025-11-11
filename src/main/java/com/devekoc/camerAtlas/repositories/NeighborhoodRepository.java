package com.devekoc.camerAtlas.repositories;

import com.devekoc.camerAtlas.entities.Neighborhood;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NeighborhoodRepository extends JpaRepository<Neighborhood, Integer> {
    Optional<Neighborhood> findByName(String name);

    void deleteByName(String name);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Integer id);
}
