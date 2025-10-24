package com.devekoc.camerAtlas.repositories;

import com.devekoc.camerAtlas.entities.Arrondissement;
import com.devekoc.camerAtlas.entities.Departement;
import com.devekoc.camerAtlas.entities.Quartier;
import com.devekoc.camerAtlas.entities.Region;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class QuartierRepositoryTest {

    @Autowired
    private QuartierRepository quartierRepository;
    @Autowired
    private ArrondissementRepository arrondissementRepository;
    @Autowired
    private DepartementRepository departementRepository;
    @Autowired
    private RegionRepository regionRepository;

    private Quartier quartier;

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

        Arrondissement arrondissement = new Arrondissement();
        arrondissement.setNom("Yaoundé I");
        arrondissement.setSousPrefecture("Nlongkak");
        arrondissement.setPopulation(300000);
        arrondissement.setSuperficie(50);
        arrondissement.setDepartement(departement);
        arrondissementRepository.save(arrondissement);

        quartier = new Quartier();
        quartier.setNom("Nlongkak");
        quartier.setNomPopulaire("Nlongkak");
        quartier.setSousPrefecture(arrondissement);
    }

    @DisplayName("Test de sauvegarde et de recherche par ID d'un quartier")
    @Test
    void whenSaveQuartier_thenFindById_shouldReturnQuartier() {
        // When
        Quartier savedQuartier = quartierRepository.save(quartier);

        // Then
        Optional<Quartier> foundQuartierOpt = quartierRepository.findById(savedQuartier.getId());

        assertThat(foundQuartierOpt).isPresent();
        assertThat(foundQuartierOpt.get().getNom()).isEqualTo("Nlongkak");
        assertThat(foundQuartierOpt.get().getSousPrefecture().getNom()).isEqualTo("Yaoundé I");
    }

    @DisplayName("Test de recherche d'un quartier par son nom")
    @Test
    void givenQuartierExists_whenFindByNom_shouldReturnQuartier() {
        // Given
        quartierRepository.save(quartier);

        // When
        Optional<Quartier> foundQuartierOpt = quartierRepository.findByNom("Nlongkak");

        // Then
        assertThat(foundQuartierOpt).isPresent();
        assertThat(foundQuartierOpt.get().getNom()).isEqualTo("Nlongkak");
    }

    @DisplayName("Test de l'existence d'un quartier par son nom")
    @Test
    void givenQuartierExists_whenExistsByNom_shouldReturnTrue() {
        // Given
        quartierRepository.save(quartier);

        // When
        boolean exists = quartierRepository.existsByNom("Nlongkak");

        // Then
        assertThat(exists).isTrue();
    }

    @DisplayName("Test de suppression d'un quartier par son nom")
    @Test
    void givenQuartierExists_whenDeleteByNom_shouldRemoveQuartier() {
        // Given
        quartierRepository.save(quartier);
        assertThat(quartierRepository.existsByNom("Nlongkak")).isTrue();

        // When
        quartierRepository.deleteByNom("Nlongkak");

        // Then
        boolean exists = quartierRepository.existsByNom("Nlongkak");
        assertThat(exists).isFalse();
    }
}