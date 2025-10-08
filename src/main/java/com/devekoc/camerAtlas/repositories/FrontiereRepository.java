package com.devekoc.camerAtlas.repositories;

import com.devekoc.camerAtlas.entities.Frontiere;
import com.devekoc.camerAtlas.enumerations.TypeFrontiere;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FrontiereRepository extends JpaRepository<Frontiere, Integer> {
    List<Frontiere> findByType(TypeFrontiere typeFrontiere);
}
