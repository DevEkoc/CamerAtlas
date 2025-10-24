package com.devekoc.camerAtlas.repositories;

import com.devekoc.camerAtlas.entities.Frontiere;
import com.devekoc.camerAtlas.enumerations.TypeFrontiere;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class FrontiereRepositoryTest {

    @Autowired
    private FrontiereRepository frontiereRepository;

    @Test
    void doit_pouvoir_enregistrer_et_lire_une_frontiere() {
        Frontiere nouvelleFrontiere = new Frontiere(null, TypeFrontiere.NORD, "Limite Nord");
        Frontiere frontiereEnregistree = frontiereRepository.save(nouvelleFrontiere);

        assertThat(frontiereEnregistree).isNotNull();
        assertThat(frontiereEnregistree.getId()).isNotNull();
        assertThat(frontiereEnregistree.getType()).isEqualTo(TypeFrontiere.NORD);
        assertThat(frontiereEnregistree.getLimite()).isEqualTo("Limite Nord");
    }

    @Test
    void doit_retourner_une_liste_de_frontieres_par_type() {
        frontiereRepository.save(new Frontiere(null, TypeFrontiere.EST, "Limite Est 1"));
        frontiereRepository.save(new Frontiere(null, TypeFrontiere.OUEST, "Limite Ouest"));
        frontiereRepository.save(new Frontiere(null, TypeFrontiere.EST, "Limite Est 2"));

        List<Frontiere> frontieresEst = frontiereRepository.findByType(TypeFrontiere.EST);

        assertThat(frontieresEst).hasSize(2);
        assertThat(frontieresEst).extracting(Frontiere::getType).containsOnly(TypeFrontiere.EST);
    }

    @Test
    void doit_retourner_une_liste_vide_si_aucun_type_ne_correspond() {
        frontiereRepository.save(new Frontiere(null, TypeFrontiere.NORD, "Limite Nord"));

        List<Frontiere> frontieresSud = frontiereRepository.findByType(TypeFrontiere.SUD);

        assertThat(frontieresSud).isEmpty();
    }
}