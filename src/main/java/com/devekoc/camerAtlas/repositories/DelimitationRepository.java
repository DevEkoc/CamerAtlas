package com.devekoc.camerAtlas.repositories;

import com.devekoc.camerAtlas.entities.Circonscription;
import com.devekoc.camerAtlas.entities.Delimitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DelimitationRepository extends JpaRepository<Delimitation, Integer> {
    List<Delimitation> findByCirconscription(Circonscription circonscription);
    List<Delimitation> findByCirconscription_IdIn(List<Integer> circonscriptionIds);
}
