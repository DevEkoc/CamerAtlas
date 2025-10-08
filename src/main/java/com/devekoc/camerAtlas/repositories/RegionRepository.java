package com.devekoc.camerAtlas.repositories;

import com.devekoc.camerAtlas.entities.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Integer> {
    Optional<Region> findByNom(String nom);

    void deleteByNom(String nom);

    boolean existsByNom(String nom);
}
