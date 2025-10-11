package com.devekoc.camerAtlas.repositories;

import com.devekoc.camerAtlas.entities.Quartier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuartierRepository extends JpaRepository<Quartier, Integer> {
    Optional<Quartier> findByNom(String nom);

    void deleteByNom(String nom);

    boolean existsByNom(String nom);

    boolean existsByNomAndIdNot(String nom, Integer id);
}
