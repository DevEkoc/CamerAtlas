package com.devekoc.camerAtlas.repositories;

import com.devekoc.camerAtlas.entities.Departement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartementRepository extends JpaRepository<Departement, Integer> {
    Optional<Departement> findByNom(String nom);

    void deleteByNom(String nom);

    boolean existsByNom(String nom);

    boolean existsByNomAndNomNot(String nom, String nomExclu);

    boolean existsByNomAndIdNot(String nom,  Integer id);
}
