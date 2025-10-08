package com.devekoc.camerAtlas.repositories;

import com.devekoc.camerAtlas.entities.Delimitation;
import com.devekoc.camerAtlas.entities.primaryKeys.DelimitationPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DelimitationRepository extends JpaRepository<Delimitation, DelimitationPK> {
}
