package com.devekoc.camerAtlas.repositories;

import com.devekoc.camerAtlas.entities.Circonscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CirconscriptionRepository extends JpaRepository<Circonscription, Integer> {
    boolean existsByNom(String nom);
}
