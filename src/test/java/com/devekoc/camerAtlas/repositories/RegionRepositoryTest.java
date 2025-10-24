package com.devekoc.camerAtlas.repositories;

import com.devekoc.camerAtlas.entities.Region;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RegionRepositoryTest {

    @Autowired
    private RegionRepository regionRepository;

    private Region region;

    @BeforeEach
    void setUp() {
        // Given
        region = new Region();
        region.setNom("Est");
        region.setChefLieu("Bertoua");
        region.setCodeMineralogique("ES");
        region.setPopulation(1000000);
        region.setSuperficie(109002);
    }

    @DisplayName("Test de sauvegarde et de recherche par ID d'une région")
    @Test
    void whenSaveRegion_thenFindById_shouldReturnRegion() {
        // When
        Region savedRegion = regionRepository.save(region);

        // Then
        Optional<Region> foundRegionOpt = regionRepository.findById(savedRegion.getId());

        assertThat(foundRegionOpt).isPresent();
        assertThat(foundRegionOpt.get().getNom()).isEqualTo("Est");
        assertThat(foundRegionOpt.get().getChefLieu()).isEqualTo("Bertoua");
        assertThat(foundRegionOpt.get().getId()).isEqualTo(1);
    }

    @DisplayName("Test de recherche d'une région par son nom")
    @Test
    void givenRegionExists_whenFindByNom_shouldReturnRegion() {
        // Given
        regionRepository.save(region);

        // When
        Optional<Region> foundRegionOpt = regionRepository.findByNom("Est");

        // Then
        assertThat(foundRegionOpt).isPresent();
        assertThat(foundRegionOpt.get().getNom()).isEqualTo("Est");
    }

    @DisplayName("Test de l'existence d'une région par son nom")
    @Test
    void givenRegionExists_whenExistsByNom_shouldReturnTrue() {
        // Given
        regionRepository.save(region);

        // When
        boolean exists = regionRepository.existsByNom("Est");

        // Then
        assertThat(exists).isTrue();
    }

    @DisplayName("Test de non-existence d'une région par son nom")
    @Test
    void givenNoRegion_whenExistsByNom_shouldReturnFalse() {
        // When
        boolean exists = regionRepository.existsByNom("Sud");

        // Then
        assertThat(exists).isFalse();
    }

    @DisplayName("Test de suppression d'une région par son nom")
    @Test
    void givenRegionExists_whenDeleteByNom_shouldRemoveRegion() {
        // Given
        regionRepository.save(region);
        assertThat(regionRepository.existsByNom("Est")).isTrue();

        // When
        regionRepository.deleteByNom("Est");

        // Then
        boolean exists = regionRepository.existsByNom("Est");
        assertThat(exists).isFalse();
    }
}
