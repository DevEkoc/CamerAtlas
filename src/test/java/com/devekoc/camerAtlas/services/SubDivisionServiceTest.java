package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.dto.subDivision.SubDivisionCreateDTO;
import com.devekoc.camerAtlas.dto.subDivision.SubDivisionListDTO;
import com.devekoc.camerAtlas.entities.Division;
import com.devekoc.camerAtlas.entities.Neighborhood;
import com.devekoc.camerAtlas.entities.SubDivision;
import com.devekoc.camerAtlas.repositories.AppointmentRepository;
import com.devekoc.camerAtlas.repositories.DelimitationRepository;
import com.devekoc.camerAtlas.repositories.DivisionRepository;
import com.devekoc.camerAtlas.repositories.SubDivisionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;


import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubDivisionServiceTest {

    @Mock
    SubDivisionRepository subDivisionRepository;
    @Mock
    DivisionRepository divisionRepository;
    @Mock MediaService mediaService;
    @Mock
    AppointmentRepository appointmentRepository;
    @Mock
    DelimitationRepository delimitationRepository;

    @InjectMocks
    SubDivisionService service;

    SubDivision subDivision;
    Division division;

    @BeforeEach
    void init() {
        division = new Division();
        division.setId(10);
        division.setName("Vina");

        subDivision = new SubDivision();
        subDivision.setId(1);
        subDivision.setName("Ngaoundéré 1er");
        subDivision.setSurface(1000);
        subDivision.setPopulation(200000);
        subDivision.setSubDivisionalOffice("Sous-préfecture");
        subDivision.setDivision(division);
        subDivision.setImage("old.png");
    }

    MultipartFile mockImage() {
        return new MockMultipartFile(
                "image",
                "test.png",
                "image/png",
                "fake-image".getBytes()
        );
    }

    @Test
    void create_shouldCreateSubDivisionSuccessfully() throws Exception {

        SubDivisionCreateDTO dto = new SubDivisionCreateDTO(
                "Ngaoundéré 1er",
                1000,
                200000,
                "7.3;13.5",
                "Sous-préfecture",
                10,
                mockImage()
        );

        when(subDivisionRepository.existsByName("Ngaoundéré 1er")).thenReturn(false);
        when(divisionRepository.findById(10)).thenReturn(Optional.of(division));
        when(mediaService.saveImage(any(), eq("subDivisions"))).thenReturn("img.png");

        SubDivision saved = new SubDivision();
        saved.setId(1);
        saved.setName("Ngaoundéré 1er");
        saved.setImage("img.png");
        saved.setDivision(division);

        when(subDivisionRepository.save(any())).thenReturn(saved);

        SubDivisionListDTO result = service.create(dto);

        assertThat(result.name()).isEqualTo("Ngaoundéré 1er");
        assertThat(result.divisionId()).isEqualTo(10);
        assertThat(result.imgUrl()).contains("img.png");

        verify(mediaService).saveImage(any(), eq("subDivisions"));
        verify(subDivisionRepository).save(any());
    }

    @Test
    void find_shouldReturnSubDivision() {
        when(subDivisionRepository.findById(1)).thenReturn(Optional.of(subDivision));
        when(appointmentRepository.findByCirconscription_IdInAndEndDateIsNull(List.of(1)))
                .thenReturn(Collections.emptyList());
        when(delimitationRepository.findByCirconscription_IdIn(List.of(1)))
                .thenReturn(Collections.emptyList());

        SubDivisionListDTO dto = service.find(1);

        assertThat(dto.name()).isEqualTo("Ngaoundéré 1er");
    }

    @Test
    void listAll_shouldReturnListOfSubDivisions() {

        when(subDivisionRepository.findAll()).thenReturn(List.of(subDivision));

        when(appointmentRepository.findByCirconscription_IdInAndEndDateIsNull(List.of(1)))
                .thenReturn(Collections.emptyList());
        when(delimitationRepository.findByCirconscription_IdIn(List.of(1)))
                .thenReturn(Collections.emptyList());

        List<SubDivisionListDTO> list = service.listAll();

        assertThat(list).hasSize(1);
        assertThat(list.getFirst().name()).isEqualTo("Ngaoundéré 1er");
    }

    @Test
    void update_shouldUpdateSuccessfully() throws Exception {

        SubDivisionCreateDTO dto = new SubDivisionCreateDTO(
                "Ngaoundéré 1er",
                1500,
                250000,
                "7.4;13.6",
                "Nouvelle SP",
                10,
                mockImage()
        );

        when(subDivisionRepository.findById(1)).thenReturn(Optional.of(subDivision));
        when(divisionRepository.findById(10)).thenReturn(Optional.of(division));
        when(mediaService.saveImage(any(), eq("subDivisions"))).thenReturn("new.png");

        when(subDivisionRepository.save(any())).thenReturn(subDivision);

        when(appointmentRepository.findByCirconscription_IdInAndEndDateIsNull(List.of(1)))
                .thenReturn(Collections.emptyList());
        when(delimitationRepository.findByCirconscription_IdIn(List.of(1)))
                .thenReturn(Collections.emptyList());

        SubDivisionListDTO updated = service.update(1, dto);

        assertThat(updated.population()).isEqualTo(250000);
        verify(mediaService).deleteImage("old.png");
    }

    @Test
    void delete_shouldDeleteSuccessfully() {

        subDivision.setNeighbourhoodsList(Collections.emptyList());

        when(subDivisionRepository.findById(1)).thenReturn(Optional.of(subDivision));

        service.delete(1);

        verify(mediaService).deleteImage("old.png");
        verify(subDivisionRepository).delete(subDivision);
    }

    @Test
    void delete_shouldThrowException_whenHasNeighborhoods() {

        Neighborhood n = new Neighborhood();
        subDivision.setNeighbourhoodsList(List.of(n));

        when(subDivisionRepository.findById(1)).thenReturn(Optional.of(subDivision));

        assertThatThrownBy(() -> service.delete(1))
                .isInstanceOf(DataIntegrityViolationException.class);
    }


}

