package com.devekoc.camerAtlas.repositories;

import com.devekoc.camerAtlas.entities.Affectation;
import com.devekoc.camerAtlas.entities.Autorite;
import com.devekoc.camerAtlas.entities.Circonscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AffectationRepository extends JpaRepository<Affectation,Integer> {

    /**
     * Vérifie s'il existe une affectation active (sans date de fin) pour une circonscription donnée.
     * @param circonscription La circonscription à vérifier.
     * @return true si une affectation active existe, false sinon.
     */
    boolean existsByCirconscriptionAndDateFinIsNull(Circonscription circonscription);

    boolean existsByCirconscriptionAndDateFinIsNullAndIdNot(Circonscription circonscription, int id);

    /**
     * Vérifie s'il existe une affectation active (sans date de fin) pour une autorité donnée.
     * @param autorite L'autorité à vérifier.
     * @return true si une affectation active existe, false sinon.
     */
    boolean existsByAutoriteAndDateFinIsNull(Autorite autorite);

    boolean existsByAutoriteAndDateFinIsNullAndIdNot(Autorite autorite, int id);


}
