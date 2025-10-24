package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.dto.affectation.AffectationCreateDTO;
import com.devekoc.camerAtlas.entities.Affectation;
import com.devekoc.camerAtlas.entities.Autorite;
import com.devekoc.camerAtlas.entities.Circonscription;
import com.devekoc.camerAtlas.entities.Region;
import com.devekoc.camerAtlas.enumerations.Fonction;
import com.devekoc.camerAtlas.exceptions.PositionAlreadyFilledException;
import com.devekoc.camerAtlas.repositories.AffectationRepository;
import com.devekoc.camerAtlas.repositories.AutoriteRepository;
import com.devekoc.camerAtlas.repositories.CirconscriptionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AffectationServiceTest {

    @Mock
    private AffectationRepository affectationRepository;
    @Mock
    private AutoriteRepository autoriteRepository;
    @Mock
    private CirconscriptionRepository circonscriptionRepository;

    @InjectMocks
    private AffectationService affectationService;

    private Autorite autorite;
    private Circonscription circonscription;
    private AffectationCreateDTO createDTO;

    @BeforeEach
    void setUp() {
        autorite = new Autorite(1, "Test", "Autorite", LocalDate.of(1980, 1, 1));
        circonscription = new Region(); // Using Region as a concrete implementation
        circonscription.setId(1);
        circonscription.setNom("Test Region");

        createDTO = new AffectationCreateDTO(
                autorite.getId(),
                circonscription.getId(),
                Fonction.GOUVERNEUR,
                LocalDate.now().minusDays(10),
                null
        );
    }

    @Test
    @DisplayName("Échec de création : la circonscription est déjà occupée")
    void creer_quandCirconscriptionOccupee_doitLancerPositionAlreadyFilledException() {
        // Arrange
        when(circonscriptionRepository.findById(anyInt())).thenReturn(Optional.of(circonscription));
        when(autoriteRepository.findById(anyInt())).thenReturn(Optional.of(autorite));
        when(affectationRepository.existsByCirconscriptionAndDateFinIsNull(circonscription)).thenReturn(true);

        // Act & Assert
        assertThrows(PositionAlreadyFilledException.class, () -> affectationService.creer(createDTO));
    }

    @Test
    @DisplayName("Échec de création : l'autorité est déjà occupée")
    void creer_quandAutoriteOccupee_doitLancerPositionAlreadyFilledException() {
        // Arrange
        when(circonscriptionRepository.findById(anyInt())).thenReturn(Optional.of(circonscription));
        when(autoriteRepository.findById(anyInt())).thenReturn(Optional.of(autorite));
        when(affectationRepository.existsByCirconscriptionAndDateFinIsNull(circonscription)).thenReturn(false);
        when(affectationRepository.existsByAutoriteAndDateFinIsNull(autorite)).thenReturn(true);

        // Act & Assert
        assertThrows(PositionAlreadyFilledException.class, () -> affectationService.creer(createDTO));
    }

    @Test
    @DisplayName("Échec de création : autorité non trouvée")
    void creer_quandAutoriteNonTrouvee_doitLancerEntityNotFoundException() {
        // Arrange
        when(circonscriptionRepository.findById(anyInt())).thenReturn(Optional.of(circonscription));
        when(autoriteRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> affectationService.creer(createDTO));
    }
}