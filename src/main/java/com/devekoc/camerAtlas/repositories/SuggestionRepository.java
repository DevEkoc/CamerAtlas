package com.devekoc.camerAtlas.repositories;

import com.devekoc.camerAtlas.entities.Suggestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SuggestionRepository extends JpaRepository<Suggestion, Integer> {
}
