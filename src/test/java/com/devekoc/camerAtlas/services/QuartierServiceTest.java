package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.dto.quartier.QuartierCreateDTO;
import com.devekoc.camerAtlas.dto.quartier.QuartierListerDTO;
import com.devekoc.camerAtlas.entities.Arrondissement;
import com.devekoc.camerAtlas.entities.Quartier;
import com.devekoc.camerAtlas.repositories.ArrondissementRepository;
import com.devekoc.camerAtlas.repositories.QuartierRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuartierServiceTest {

    @Mock
    private QuartierRepository quartierRepository;

    @Mock
    private ArrondissementRepository arrondissementRepository;

    @InjectMocks
    private QuartierService quartierService;

    private QuartierCreateDTO quartierCreateDTO;
    private Quartier quartier;
    private Arrondissement arrondissement;

    @BeforeEach
    void setUp() {
        arrondissement = new Arrondissement();
        arrondissement.setId(1);
        arrondissement.setNom("Yaoundé I");

        quartierCreateDTO = new QuartierCreateDTO(
                "Nlongkak",
                "Nlongkak",
                arrondissement.getId()
        );

        quartier = new Quartier();
        quartier.setId(1);
        quartier.setNom("Nlongkak");
        quartier.setSousPrefecture(arrondissement);
    }

    @DisplayName("Test de création de quartier qui réussit")
    @Test
    void creerQuartier_avecDtoValide_doitRetournerQuartierListerDto() {
        // Arrange
        when(quartierRepository.existsByNom(anyString())).thenReturn(false);
        when(arrondissementRepository.findById(anyInt())).thenReturn(Optional.of(arrondissement));
        when(quartierRepository.save(any(Quartier.class))).thenReturn(quartier);

        // Act
        QuartierListerDTO resultat = quartierService.creer(quartierCreateDTO);

        // Assert
        assertThat(resultat).isNotNull();
        assertThat(resultat.nom()).isEqualTo(quartierCreateDTO.nom());
        assertThat(resultat.idSousPrefecture()).isEqualTo(arrondissement.getId());
        assertThat(resultat.nomSousPrefecture()).isEqualTo(arrondissement.getNom());
    }

    @DisplayName("Test de création de quartier avec un nom qui existe déjà")
    @Test
    void creerQuartier_avecNomExistant_doitLancerDataIntegrityViolationException() {
        // Arrange
        when(quartierRepository.existsByNom(quartierCreateDTO.nom())).thenReturn(true);

        // Act & Assert
        DataIntegrityViolationException exception = assertThrows(
                DataIntegrityViolationException.class,
                () -> quartierService.creer(quartierCreateDTO)
        );

        assertThat(exception.getMessage()).contains("Un quartier avec le nom '" + quartierCreateDTO.nom() + "' existe déjà");
        verify(quartierRepository, never()).save(any(Quartier.class));
    }

    @DisplayName("Test de création de quartier avec un arrondissement inexistant")
    @Test
    void creerQuartier_avecArrondissementInexistant_doitLancerEntityNotFoundException() {
        // Arrange
        when(quartierRepository.existsByNom(anyString())).thenReturn(false);
        when(arrondissementRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> quartierService.creer(quartierCreateDTO)
        );

        assertThat(exception.getMessage()).contains("Aucun arrondissement trouvé avec l'ID : " + quartierCreateDTO.idSousPrefecture());
        verify(quartierRepository, never()).save(any(Quartier.class));
    }
}