package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.dto.arrondissement.ArrondissementCreateDTO;
import com.devekoc.camerAtlas.dto.arrondissement.ArrondissementListerDTO;
import com.devekoc.camerAtlas.entities.Arrondissement;
import com.devekoc.camerAtlas.entities.Departement;
import com.devekoc.camerAtlas.repositories.ArrondissementRepository;
import com.devekoc.camerAtlas.repositories.DepartementRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArrondissementServiceTest {

    @Mock
    private ArrondissementRepository arrondissementRepository;

    @Mock
    private DepartementRepository departementRepository;

    @InjectMocks
    private ArrondissementService arrondissementService;

    private ArrondissementCreateDTO arrondissementCreateDTO;
    private Arrondissement arrondissement;
    private Departement departement;

    @BeforeEach
    void setUp() {
        departement = new Departement();
        departement.setId(1);
        departement.setNom("Mfoundi");
        departement.setPopulation(123456);
        departement.setSuperficie(123456);
        departement.setCoordonnees("coords");
        departement.setPrefecture("Yaoundé");

        arrondissementCreateDTO = new ArrondissementCreateDTO(
                "Yaoundé I",
                50,
                300000,
                "coords",
                "Nlongkak",
                departement.getId()
        );

        arrondissement = new Arrondissement();
        arrondissement.setId(1);
        arrondissement.setNom("Yaoundé I");
        arrondissement.setDepartement(departement);
        arrondissement.setListeQuartiers(new ArrayList<>());
    }

    @DisplayName("Test de création d'arrondissement qui réussit")
    @Test
    void creerArrondissement_avecDtoValide_doitRetournerArrondissementListerDto() {
        // Arrange
        when(arrondissementRepository.existsByNom(anyString())).thenReturn(false);
        when(departementRepository.findById(anyInt())).thenReturn(Optional.of(departement));
        when(arrondissementRepository.save(any(Arrondissement.class))).thenReturn(arrondissement);

        // Act
        ArrondissementListerDTO resultat = arrondissementService.creer(arrondissementCreateDTO);

        // Assert
        assertThat(resultat).isNotNull();
        assertThat(resultat.nom()).isEqualTo(arrondissementCreateDTO.nom());
        assertThat(resultat.idDepartement()).isEqualTo(departement.getId());
        assertThat(resultat.nomDepartement()).isEqualTo(departement.getNom());
    }

    @DisplayName("Test de création d'arrondissement qui réussit (vérification de l'appel)")
    @Test
    void creerArrondissement_avecDtoValide_doitAppelerSaveSurLeRepository() {
        // Arrange
        when(arrondissementRepository.existsByNom(anyString())).thenReturn(false);
        when(departementRepository.findById(anyInt())).thenReturn(Optional.of(departement));
        when(arrondissementRepository.save(any(Arrondissement.class))).thenReturn(arrondissement);

        // Act
        arrondissementService.creer(arrondissementCreateDTO);

        // Assert
        verify(arrondissementRepository, times(1)).save(any(Arrondissement.class));
    }

    @DisplayName("Test de création d'arrondissement avec un nom qui existe déjà")
    @Test
    void creerArrondissement_avecNomExistant_doitLancerDataIntegrityViolationException() {
        // Arrange
        when(arrondissementRepository.existsByNom(arrondissementCreateDTO.nom())).thenReturn(true);

        // Act & Assert
        DataIntegrityViolationException exception = assertThrows(
                DataIntegrityViolationException.class,
                () -> arrondissementService.creer(arrondissementCreateDTO)
        );

        assertThat(exception.getMessage()).contains("Un arrondissement avec le nom '" + arrondissementCreateDTO.nom() + "' existe déjà");
        verify(arrondissementRepository, never()).save(any(Arrondissement.class));
    }

    @DisplayName("Test de création d'arrondissement avec un département inexistant")
    @Test
    void creerArrondissement_avecDepartementInexistant_doitLancerEntityNotFoundException() {
        // Arrange
        when(arrondissementRepository.existsByNom(anyString())).thenReturn(false);
        when(departementRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> arrondissementService.creer(arrondissementCreateDTO)
        );

        assertThat(exception.getMessage()).contains("Aucun département trouvé avec l'ID : " + arrondissementCreateDTO.idDepartement());
        verify(arrondissementRepository, never()).save(any(Arrondissement.class));
    }
}