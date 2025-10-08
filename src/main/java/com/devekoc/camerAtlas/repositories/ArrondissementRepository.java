package com.devekoc.camerAtlas.repositories;

import com.devekoc.camerAtlas.entities.Arrondissement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArrondissementRepository extends JpaRepository<Arrondissement, Integer> {
    Optional<Arrondissement> findByNom(String nom);

    void deleteByNom(String nom);

    boolean existsByNom(String nom);
}
