package com.devekoc.camerAtlas.repositories;

import com.devekoc.camerAtlas.entities.Arrondissement;
import com.devekoc.camerAtlas.entities.Departement;
import com.devekoc.camerAtlas.entities.Region;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ArrondissementRepositoryTest {

    @Autowired
    private ArrondissementRepository arrondissementRepository;
    @Autowired
    private DepartementRepository departementRepository;
    @Autowired
    private RegionRepository regionRepository;

    private Arrondissement arrondissement;

    @BeforeEach
    void setUp() {
        // Given
        Region region = new Region();
        region.setNom("Centre");
        region.setChefLieu("Yaoundé");
        region.setCodeMineralogique("CE");
        region.setPopulation(4000000);
        region.setSuperficie(68953);
        regionRepository.save(region);

        Departement departement = new Departement();
        departement.setNom("Mfoundi");
        departement.setPrefecture("Yaoundé");
        departement.setPopulation(2000000);
        departement.setSuperficie(297);
        departement.setRegion(region);
        departementRepository.save(departement);

        arrondissement = new Arrondissement();
        arrondissement.setNom("Yaoundé I");
        arrondissement.setSousPrefecture("Nlongkak");
        arrondissement.setPopulation(300000);
        arrondissement.setSuperficie(50);
        arrondissement.setDepartement(departement);
    }

    @DisplayName("Test de sauvegarde et de recherche par ID d'un arrondissement")
    @Test
    void whenSaveArrondissement_thenFindById_shouldReturnArrondissement() {
        // When
        Arrondissement savedArrondissement = arrondissementRepository.save(arrondissement);

        // Then
        Optional<Arrondissement> foundArrondissementOpt = arrondissementRepository.findById(savedArrondissement.getId());

        assertThat(foundArrondissementOpt).isPresent();
        assertThat(foundArrondissementOpt.get().getNom()).isEqualTo("Yaoundé I");
        assertThat(foundArrondissementOpt.get().getSousPrefecture()).isEqualTo("Nlongkak");
        assertThat(foundArrondissementOpt.get().getDepartement().getNom()).isEqualTo("Mfoundi");
    }

    @DisplayName("Test de recherche d'un arrondissement par son nom")
    @Test
    void givenArrondissementExists_whenFindByNom_shouldReturnArrondissement() {
        // Given
        arrondissementRepository.save(arrondissement);

        // When
        Optional<Arrondissement> foundArrondissementOpt = arrondissementRepository.findByNom("Yaoundé I");

        // Then
        assertThat(foundArrondissementOpt).isPresent();
        assertThat(foundArrondissementOpt.get().getNom()).isEqualTo("Yaoundé I");
    }

    @DisplayName("Test de l'existence d'un arrondissement par son nom")
    @Test
    void givenArrondissementExists_whenExistsByNom_shouldReturnTrue() {
        // Given
        arrondissementRepository.save(arrondissement);

        // When
        boolean exists = arrondissementRepository.existsByNom("Yaoundé I");

        // Then
        assertThat(exists).isTrue();
    }

    @DisplayName("Test de non-existence d'un arrondissement par son nom")
    @Test
    void givenNoArrondissement_whenExistsByNom_shouldReturnFalse() {
        // When
        boolean exists = arrondissementRepository.existsByNom("Douala I");

        // Then
        assertThat(exists).isFalse();
    }

    @DisplayName("Test de suppression d'un arrondissement par son nom")
    @Test
    void givenArrondissementExists_whenDeleteByNom_shouldRemoveArrondissement() {
        // Given
        arrondissementRepository.save(arrondissement);
        assertThat(arrondissementRepository.existsByNom("Yaoundé I")).isTrue();

        // When
        arrondissementRepository.deleteByNom("Yaoundé I");

        // Then
        boolean exists = arrondissementRepository.existsByNom("Yaoundé I");
        assertThat(exists).isFalse();
    }
}