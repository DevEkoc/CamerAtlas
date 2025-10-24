package com.devekoc.camerAtlas.repositories;

import com.devekoc.camerAtlas.entities.Circonscription;
import com.devekoc.camerAtlas.entities.Delimitation;
import com.devekoc.camerAtlas.entities.Frontiere;
import com.devekoc.camerAtlas.entities.Region;
import com.devekoc.camerAtlas.enumerations.TypeFrontiere;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class DelimitationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DelimitationRepository delimitationRepository;

    @Test
    void doit_pouvoir_enregistrer_et_lire_une_delimitation() {
        // 1. Créer et persister les dépendances
        Region circonscription = new Region();
        circonscription.setNom("Circonscription Test"); circonscription.setPopulation(213); circonscription.setSuperficie(321);
        circonscription.setCoordonnees("coords"); circonscription.setChefLieu("Test"); circonscription.setCodeMineralogique("TS");
        Circonscription circonscriptionEnregistree = entityManager.persist(circonscription);

        Frontiere frontiere = new Frontiere(null, TypeFrontiere.NORD, "Limite Test");
        Frontiere frontiereEnregistree = entityManager.persist(frontiere);

        // 2. Créer l'entité Delimitation
        Delimitation nouvelleDelimitation = new Delimitation(frontiereEnregistree, circonscriptionEnregistree);

        // 3. Enregistrer la délimitation
        Delimitation delimitationEnregistree = delimitationRepository.save(nouvelleDelimitation);

        // 4. Vérifier
        assertThat(delimitationEnregistree.getId()).isNotNull();
        Delimitation delimitationTrouvee = entityManager.find(Delimitation.class, delimitationEnregistree.getId());

        assertThat(delimitationTrouvee).isNotNull();
        assertThat(delimitationTrouvee.getCirconscription().getId()).isEqualTo(circonscriptionEnregistree.getId());
        assertThat(delimitationTrouvee.getFrontiere().getId()).isEqualTo(frontiereEnregistree.getId());
    }
}
