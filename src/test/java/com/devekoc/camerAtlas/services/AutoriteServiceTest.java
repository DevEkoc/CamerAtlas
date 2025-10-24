package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.entities.Autorite;
import com.devekoc.camerAtlas.repositories.AutoriteRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutoriteServiceTest {

    @Mock
    private AutoriteRepository autoriteRepository;

    @InjectMocks
    private AutoriteService autoriteService;

    private Autorite autorite;

    @BeforeEach
    void setUp() {
        autorite = new Autorite();
        autorite.setId(1);
        autorite.setNom("Mbarga");
        autorite.setPrenom("Jean");
        autorite.setDateNaissance(LocalDate.of(1970, 1, 1));
    }

    @DisplayName("Test de création d'autorité")
    @Test
    void creerAutorite_doitAppelerSave() {
        // Act
        autoriteService.creer(autorite);
        // Assert
        verify(autoriteRepository, times(1)).save(any(Autorite.class));
    }

    @DisplayName("Test de recherche d'autorité qui réussit")
    @Test
    void rechercherAutorite_avecIdExistant_doitRetournerAutorite() {
        // Arrange
        when(autoriteRepository.findById(1)).thenReturn(Optional.of(autorite));
        // Act
        Autorite resultat = autoriteService.rechercher(1);
        // Assert
        assertThat(resultat).isNotNull();
        assertThat(resultat.getNom()).isEqualTo("Mbarga");
    }

    @DisplayName("Test de recherche d'autorité qui échoue")
    @Test
    void rechercherAutorite_avecIdInexistant_doitLancerEntityNotFoundException() {
        // Arrange
        when(autoriteRepository.findById(anyInt())).thenReturn(Optional.empty());
        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> autoriteService.rechercher(99));
    }
}