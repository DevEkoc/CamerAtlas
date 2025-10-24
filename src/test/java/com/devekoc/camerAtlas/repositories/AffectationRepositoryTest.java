package com.devekoc.camerAtlas.repositories;

import com.devekoc.camerAtlas.entities.Affectation;
import com.devekoc.camerAtlas.entities.Autorite;
import com.devekoc.camerAtlas.entities.Circonscription;
import com.devekoc.camerAtlas.entities.Region;
import com.devekoc.camerAtlas.enumerations.Fonction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AffectationRepositoryTest {

    @Autowired
    private AffectationRepository affectationRepository;
    @Autowired
    private AutoriteRepository autoriteRepository;
    @Autowired
    private RegionRepository regionRepository; // Using Region as a concrete Circonscription

    private Autorite autorite;
    private Circonscription circonscription;

    @BeforeEach
    void setUp() {
        autorite = new Autorite(null, "Test", "Autorite", LocalDate.of(1980, 1, 1));
        autoriteRepository.save(autorite);

        Region region = new Region();
        region.setNom("Test Region");
        region.setChefLieu("Test ChefLieu");
        region.setPopulation(1000);
        region.setSuperficie(1000);
        circonscription = regionRepository.save(region);
    }

    private Affectation creerAffectation(Autorite aut, Circonscription circ, LocalDate dateFin) {
        Affectation affectation = new Affectation();
        affectation.setAutorite(aut);
        affectation.setCirconscription(circ);
        affectation.setFonction(Fonction.GOUVERNEUR);
        affectation.setDateDebut(LocalDate.now().minusYears(1));
        affectation.setDateFin(dateFin);
        return affectationRepository.save(affectation);
    }

    @Test
    @DisplayName("Doit retourner vrai si la circonscription a une affectation active")
    void existsByCirconscriptionAndDateFinIsNull_quandAffectationActiveExiste() {
        creerAffectation(autorite, circonscription, null); // Active affectation
        boolean exists = affectationRepository.existsByCirconscriptionAndDateFinIsNull(circonscription);
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Doit retourner faux si la circonscription n'a pas d'affectation active")
    void existsByCirconscriptionAndDateFinIsNull_quandAffectationActiveNExistePas() {
        creerAffectation(autorite, circonscription, LocalDate.now()); // Inactive affectation
        boolean exists = affectationRepository.existsByCirconscriptionAndDateFinIsNull(circonscription);
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Doit retourner vrai si l'autorité a une affectation active")
    void existsByAutoriteAndDateFinIsNull_quandAffectationActiveExiste() {
        creerAffectation(autorite, circonscription, null); // Active affectation
        boolean exists = affectationRepository.existsByAutoriteAndDateFinIsNull(autorite);
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Doit retourner faux si l'autorité n'a pas d'affectation active")
    void existsByAutoriteAndDateFinIsNull_quandAffectationActiveNExistePas() {
        creerAffectation(autorite, circonscription, LocalDate.now()); // Inactive affectation
        boolean exists = affectationRepository.existsByAutoriteAndDateFinIsNull(autorite);
        assertThat(exists).isFalse();
    }
}