package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.dto.neighborhood.NeighborhoodCreateDTO;
import com.devekoc.camerAtlas.dto.neighborhood.NeighborhoodListDTO;
import com.devekoc.camerAtlas.entities.Neighborhood;
import com.devekoc.camerAtlas.entities.SubDivision;
import com.devekoc.camerAtlas.repositories.NeighborhoodRepository;
import com.devekoc.camerAtlas.repositories.SubDivisionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;


import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NeighborhoodServiceTest {

    @Mock
    NeighborhoodRepository neighborhoodRepository;
    @Mock
    SubDivisionRepository subDivisionRepository;

    @InjectMocks NeighborhoodService service;

    Neighborhood neighborhood;
    SubDivision subDivision;

    @BeforeEach
    void init() {
        subDivision = new SubDivision();
        subDivision.setId(5);
        subDivision.setName("Melen");

        neighborhood = new Neighborhood();
        neighborhood.setId(1);
        neighborhood.setName("Etoudi");
        neighborhood.setPopularName("Le Château");
        neighborhood.setSubDivisionalOffice(subDivision);
    }

    @Test
    void create_shouldCreateNeighborhoodSuccessfully() {

        NeighborhoodCreateDTO dto = new NeighborhoodCreateDTO(
                "Etoudi",
                "Le Château",
                5
        );

        when(neighborhoodRepository.existsByName("Etoudi")).thenReturn(false);
        when(subDivisionRepository.findById(5)).thenReturn(Optional.of(subDivision));
        when(neighborhoodRepository.save(any())).thenReturn(neighborhood);

        NeighborhoodListDTO result = service.create(dto);

        assertThat(result.name()).isEqualTo("Etoudi");
        assertThat(result.popularName()).isEqualTo("Le Château");
        assertThat(result.subDivisionalOfficeId()).isEqualTo(5);

        verify(neighborhoodRepository).save(any());
    }

    @Test
    void create_shouldThrowException_whenNameAlreadyExists() {

        NeighborhoodCreateDTO dto = new NeighborhoodCreateDTO("Etoudi", "Le Château", 5);

        when(neighborhoodRepository.existsByName("Etoudi")).thenReturn(true);

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("existe déjà");
    }

    @Test
    void create_shouldThrowException_whenSubDivisionNotFound() {

        NeighborhoodCreateDTO dto = new NeighborhoodCreateDTO("Etoudi", "Le Château", 5);

        when(neighborhoodRepository.existsByName("Etoudi")).thenReturn(false);
        when(subDivisionRepository.findById(5)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Aucun arrondissement");
    }

    @Test
    void listAll_shouldReturnList() {

        when(neighborhoodRepository.findAll()).thenReturn(List.of(neighborhood));

        List<NeighborhoodListDTO> list = service.listAll();

        assertThat(list).hasSize(1);
        assertThat(list.getFirst().name()).isEqualTo("Etoudi");
    }

    @Test
    void find_shouldReturnNeighborhood() {

        when(neighborhoodRepository.findById(1)).thenReturn(Optional.of(neighborhood));

        NeighborhoodListDTO dto = service.find(1);

        assertThat(dto.name()).isEqualTo("Etoudi");
    }

    @Test
    void find_shouldThrowException_whenNotFound() {

        when(neighborhoodRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.find(1))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void findByName_shouldReturnNeighborhood() {

        when(neighborhoodRepository.findByName("Etoudi"))
                .thenReturn(Optional.of(neighborhood));

        NeighborhoodListDTO dto = service.find("Etoudi");

        assertThat(dto.popularName()).isEqualTo("Le Château");
    }

    @Test
    void update_shouldUpdateSuccessfully() {

        NeighborhoodCreateDTO dto = new NeighborhoodCreateDTO(
                "Etoudi",
                "New Popular",
                5
        );

        when(neighborhoodRepository.findById(1)).thenReturn(Optional.of(neighborhood));
        when(subDivisionRepository.findById(5)).thenReturn(Optional.of(subDivision));
        when(neighborhoodRepository.save(any())).thenReturn(neighborhood);

        NeighborhoodListDTO result = service.update(1, dto);

        assertThat(result.popularName()).isEqualTo("New Popular"); // original (mapper écrase dans l’entité)
        verify(neighborhoodRepository).save(any());
    }

    @Test
    void update_shouldThrowException_whenNewNameAlreadyExists() {

        NeighborhoodCreateDTO dto = new NeighborhoodCreateDTO(
                "New Name",
                "XYZ",
                5
        );

        when(neighborhoodRepository.findById(1)).thenReturn(Optional.of(neighborhood));
        when(neighborhoodRepository.existsByName("New Name")).thenReturn(true);

        assertThatThrownBy(() -> service.update(1, dto))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void delete_shouldDeleteSuccessfully() {

        when(neighborhoodRepository.findById(1)).thenReturn(Optional.of(neighborhood));

        service.delete(1);

        verify(neighborhoodRepository).delete(neighborhood);
    }

    @Test
    void deleteByName_shouldDeleteSuccessfully() {

        when(neighborhoodRepository.findByName("Etoudi"))
                .thenReturn(Optional.of(neighborhood));

        service.delete("Etoudi");

        verify(neighborhoodRepository).delete(neighborhood);
    }

}

