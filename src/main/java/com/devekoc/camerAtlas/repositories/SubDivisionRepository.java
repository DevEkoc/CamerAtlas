package com.devekoc.camerAtlas.repositories;

import com.devekoc.camerAtlas.entities.SubDivision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubDivisionRepository extends JpaRepository<SubDivision, Integer> {
    Optional<SubDivision> findByName(String name);

    void deleteByName(String name);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Integer id);
}
