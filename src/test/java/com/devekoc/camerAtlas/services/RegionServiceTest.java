package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.dto.region.RegionCreateDTO;
import com.devekoc.camerAtlas.dto.region.RegionListerDTO;
import com.devekoc.camerAtlas.entities.Region;
import com.devekoc.camerAtlas.repositories.RegionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegionServiceTest {

    @Mock
    private RegionRepository regionRepository;

    @InjectMocks
    private RegionService regionService;

    private RegionCreateDTO regionCreateDTO;
    private Region region;

    @BeforeEach
    void setUp() {
        regionCreateDTO = new RegionCreateDTO(
                "Littoral",
                10000,
                2000000,
                "coords",
                "Douala",
                "LT"
        );

        region = new Region();
        region.setId(1); // Simulating a saved entity
        region.setNom("Littoral");
        region.setChefLieu("Douala");
        region.setListeDepartements(new ArrayList<>());
    }

    @DisplayName("Test de création de région qui réussit")
    @Test
    void creerRegion_avecDtoValide_doitRetournerRegionListerDto() {
        // Arrange
        when(regionRepository.existsByNom(anyString())).thenReturn(false);
        when(regionRepository.save(any(Region.class))).thenReturn(region);

        // Act
        RegionListerDTO resultat = regionService.creer(regionCreateDTO);

        // Assert
        assertThat(resultat).isNotNull();
        assertThat(resultat.nom()).isEqualTo(regionCreateDTO.nom());
        assertThat(resultat.chefLieu()).isEqualTo(regionCreateDTO.chefLieu());
    }

    @DisplayName("Test de création de région qui réussit (vérification de l'appel)")
    @Test
    void creerRegion_avecDtoValide_doitAppelerSaveSurLeRepository() {
        // Arrange
        when(regionRepository.existsByNom(anyString())).thenReturn(false);
        when(regionRepository.save(any(Region.class))).thenReturn(region);

        // Act
        regionService.creer(regionCreateDTO);

        // Assert
        verify(regionRepository, times(1)).save(any(Region.class));
    }

    @DisplayName("Test de création de région avec un nom qui existe déjà")
    @Test
    void creerRegion_avecNomExistant_doitLancerDataIntegrityViolationException() {
        // Arrange
        when(regionRepository.existsByNom(regionCreateDTO.nom())).thenReturn(true);

        // Act & Assert
        DataIntegrityViolationException exception = assertThrows(
                DataIntegrityViolationException.class,
                () -> regionService.creer(regionCreateDTO)
        );

        assertThat(exception.getMessage()).contains("Une région avec le nom : " + regionCreateDTO.nom() + " existe déjà !");
        verify(regionRepository, never()).save(any(Region.class));
    }
}
