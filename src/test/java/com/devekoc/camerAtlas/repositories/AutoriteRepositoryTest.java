package com.devekoc.camerAtlas.repositories;

import com.devekoc.camerAtlas.entities.Autorite;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AutoriteRepositoryTest {

    @Autowired
    private AutoriteRepository autoriteRepository;

    private Autorite autorite;

    @BeforeEach
    void setUp() {
        autorite = new Autorite();
        autorite.setNom("Mbarga");
        autorite.setPrenom("Jean");
        autorite.setDateNaissance(LocalDate.of(1970, 1, 1));
    }

    @DisplayName("Test de sauvegarde et de recherche par ID d'une autorité")
    @Test
    void whenSaveAutorite_thenFindById_shouldReturnAutorite() {
        // When
        Autorite savedAutorite = autoriteRepository.save(autorite);

        // Then
        Optional<Autorite> foundAutoriteOpt = autoriteRepository.findById(savedAutorite.getId());

        assertThat(foundAutoriteOpt).isPresent();
        assertThat(foundAutoriteOpt.get().getNom()).isEqualTo("Mbarga");
        assertThat(foundAutoriteOpt.get().getPrenom()).isEqualTo("Jean");
    }
}