package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.dto.delimitation.DelimitationCreateDTO;
import com.devekoc.camerAtlas.dto.delimitation.DelimitationListDTO;
import com.devekoc.camerAtlas.entities.Circonscription;
import com.devekoc.camerAtlas.entities.Delimitation;
import com.devekoc.camerAtlas.entities.Region;
import com.devekoc.camerAtlas.enumerations.BorderType;
import com.devekoc.camerAtlas.repositories.CirconscriptionRepository;
import com.devekoc.camerAtlas.repositories.DelimitationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;


import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DelimitationServiceTest {

    @Mock
    private DelimitationRepository delimitationRepository;

    @Mock
    private CirconscriptionRepository circonscriptionRepository;

    @InjectMocks
    private DelimitationService service;

    Circonscription circonscription;
    Delimitation delimitation;

    @BeforeEach
    void init() {
        circonscription = new Region();
        circonscription.setId(10);
        circonscription.setName("Centre");

        delimitation = new Delimitation();
        delimitation.setId(1);
        delimitation.setBorderName("Sanaga Maritime");
        delimitation.setBorderType(BorderType.NORD);
        delimitation.setCirconscription(circonscription);
    }

    // -------------------------------------------------------------
    // CREATE
    // -------------------------------------------------------------

    @Test
    void create_shouldCreateSuccessfully() {
        DelimitationCreateDTO dto = new DelimitationCreateDTO(
                10,
                BorderType.EST,
                "Sanaga Maritime"
        );

        when(circonscriptionRepository.findById(10))
                .thenReturn(Optional.of(circonscription));

        when(delimitationRepository.save(any()))
                .thenReturn(delimitation);

        DelimitationListDTO result = service.create(dto);

        assertThat(result.delimitationId()).isEqualTo(1);
        assertThat(result.borderName()).isEqualTo("Sanaga Maritime");
        assertThat(result.circonscriptionId()).isEqualTo(10);

        verify(delimitationRepository).save(any());
    }

    @Test
    void create_shouldThrow_whenCirconscriptionNotFound() {
        DelimitationCreateDTO dto = new DelimitationCreateDTO(
                999,
                BorderType.OUEST,
                "Sanaga Maritime"
        );

        when(circonscriptionRepository.findById(999))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Aucune Circonscription trouvée");

        verify(delimitationRepository, never()).save(any());
    }

    // -------------------------------------------------------------
    // CREATE SEVERAL
    // -------------------------------------------------------------

    @Test
    void createSeveral_shouldCreateAll() {
        DelimitationCreateDTO dto1 = new DelimitationCreateDTO(10, BorderType.NORD_EST, "A");
        DelimitationCreateDTO dto2 = new DelimitationCreateDTO(10, BorderType.NORD_OUEST, "B");

        when(circonscriptionRepository.findById(10))
                .thenReturn(Optional.of(circonscription));
        when(delimitationRepository.save(any())).thenReturn(delimitation);

        List<DelimitationListDTO> result = service.createSeveral(List.of(dto1, dto2));

        assertThat(result).hasSize(2);
        verify(delimitationRepository, times(2)).save(any());
    }

    // -------------------------------------------------------------
    // LIST ALL
    // -------------------------------------------------------------

    @Test
    void listAll_shouldReturnAll() {
        when(delimitationRepository.findAll())
                .thenReturn(List.of(delimitation));

        List<DelimitationListDTO> result = service.listAll();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().delimitationId()).isEqualTo(1);
    }

    @Test
    void listAll_shouldReturnEmptyList_whenNoData() {
        when(delimitationRepository.findAll())
                .thenReturn(Collections.emptyList());

        List<DelimitationListDTO> result = service.listAll();

        assertThat(result).isEmpty();
    }

    // -------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------

    @Test
    void delete_shouldDeleteSuccessfully() {
        when(delimitationRepository.existsById(1))
                .thenReturn(true);

        service.delete(1);

        verify(delimitationRepository).deleteById(1);
    }

    @Test
    void delete_shouldThrow_whenNotFound() {
        when(delimitationRepository.existsById(1))
                .thenReturn(false);

        assertThatThrownBy(() -> service.delete(1))
                .isInstanceOf(EntityNotFoundException.class);

        verify(delimitationRepository, never()).deleteById(any());
    }
}

