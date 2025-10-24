package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.dto.delimitation.DelimitationCreateDTO;
import com.devekoc.camerAtlas.dto.delimitation.DelimitationListerDTO;
import com.devekoc.camerAtlas.entities.Circonscription;
import com.devekoc.camerAtlas.entities.Delimitation;
import com.devekoc.camerAtlas.entities.Frontiere;
import com.devekoc.camerAtlas.entities.Region;
import com.devekoc.camerAtlas.enumerations.TypeFrontiere;
import com.devekoc.camerAtlas.repositories.CirconscriptionRepository;
import com.devekoc.camerAtlas.repositories.DelimitationRepository;
import com.devekoc.camerAtlas.repositories.FrontiereRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DelimitationServiceTest {

    @Mock
    private DelimitationRepository delimitationRepository;
    @Mock
    private FrontiereRepository frontiereRepository;
    @Mock
    private CirconscriptionRepository circonscriptionRepository;

    @InjectMocks
    private DelimitationService delimitationService;

    @Test
    void doit_creer_et_retourner_une_delimitation_dto_quand_les_entites_existent() {
        // Arrange
        Circonscription circonscription = new Region();
        circonscription.setNom("Test Circonscription");
        circonscription.setCoordonnees("coords");
        circonscription.setSuperficie(123456);
        circonscription.setPopulation(456321);
        Frontiere frontiere = new Frontiere(1, TypeFrontiere.EST, "Limite Est");
        DelimitationCreateDTO createDTO = new DelimitationCreateDTO(1, 1, "Test Circonscription", "EST");

        when(circonscriptionRepository.findById(1)).thenReturn(Optional.of(circonscription));
        when(frontiereRepository.findById(1)).thenReturn(Optional.of(frontiere));

        Delimitation delimitationEnregistree = new Delimitation(1, circonscription, frontiere);
        when(delimitationRepository.save(any(Delimitation.class))).thenReturn(delimitationEnregistree);

        // Act
        DelimitationListerDTO resultat = delimitationService.creer(createDTO);

        // Assert
        assertThat(resultat).isNotNull();
        assertThat(resultat.codeCirconscription()).isEqualTo(circonscription.getId());
        assertThat(resultat.idFrontiere()).isEqualTo(frontiere.getId());
        verify(delimitationRepository, times(1)).save(any(Delimitation.class));
    }

    @Test
    void doit_lever_une_exception_quand_frontiere_non_trouvee() {
        DelimitationCreateDTO createDTO = new DelimitationCreateDTO(1, 99, "Test", "NORD");
        when(frontiereRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> delimitationService.creer(createDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Aucune Frontière trouvée");
    }

    @Test
    void doit_lever_une_exception_quand_circonscription_non_trouvee() {
        DelimitationCreateDTO createDTO = new DelimitationCreateDTO(99, 1, "Test", "NORD");
        when(frontiereRepository.findById(1)).thenReturn(Optional.of(new Frontiere())); // Frontiere exists
        when(circonscriptionRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> delimitationService.creer(createDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Aucune Circonscription trouvée");
    }

    @Test
    void doit_retourner_une_liste_de_dtos() {
        Circonscription circonscription = new Region();
        circonscription.setNom("Test Circonscription");
        circonscription.setCoordonnees("coords");
        circonscription.setSuperficie(123456);
        circonscription.setPopulation(456321);
        Frontiere frontiere = new Frontiere(1, TypeFrontiere.SUD, "Limite Sud");
        Delimitation delimitation = new Delimitation(1, circonscription, frontiere);
        when(delimitationRepository.findAll()).thenReturn(Collections.singletonList(delimitation));

        List<DelimitationListerDTO> resultat = delimitationService.lister();

        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).nomCirconscription()).isEqualTo("Test Circonscription");
        assertThat(resultat.get(0).typeFrontiere()).isEqualTo(TypeFrontiere.SUD);
    }

    @Test
    void doit_appeler_deleteById_quand_la_delimitation_existe() {
        int id = 1;
        when(delimitationRepository.existsById(id)).thenReturn(true);
        doNothing().when(delimitationRepository).deleteById(id);

        delimitationService.supprimer(id);

        verify(delimitationRepository, times(1)).deleteById(id);
    }

    @Test
    void doit_lever_une_exception_quand_la_delimitation_a_supprimer_n_existe_pas() {
        int id = 99;
        when(delimitationRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> delimitationService.supprimer(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Aucune Délimitation trouvée");
    }
}
