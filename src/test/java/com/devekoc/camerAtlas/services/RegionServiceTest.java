package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.dto.region.RegionCreateDTO;
import com.devekoc.camerAtlas.dto.region.RegionListDTO;
import com.devekoc.camerAtlas.entities.Region;
import com.devekoc.camerAtlas.repositories.AppointmentRepository;
import com.devekoc.camerAtlas.repositories.DelimitationRepository;
import com.devekoc.camerAtlas.repositories.RegionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegionServiceTest {

    @Mock
    private RegionRepository regionRepository;
    @Mock
    private MediaService mediaService;
    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private DelimitationRepository delimitationRepository;

    @InjectMocks
    private RegionService regionService;

    private Region region;

    @BeforeEach
    void setup() {
        region = new Region();
        region.setId(1);
        region.setName("Centre");
        region.setPopulation(500000);
        region.setSurface(20000);
        region.setGpsCoordinates("3.8;11.5");
        region.setCapital("Yaoundé");
        region.setMineralogicalCode("CE");
        region.setImage("old.png");
    }

    // --------------------------------------------------------------
    //                     CREATE
    // --------------------------------------------------------------

    @Test
    void create_shouldCreateRegionSuccessfully() throws Exception {
        RegionCreateDTO dto = new RegionCreateDTO(
                "Centre", 20000, 500000, "3.8;11.5", "Yaoundé", "CE", mockFile("test.png")
        );

        when(regionRepository.existsByName("Centre")).thenReturn(false);
        when(mediaService.saveImage(any(), eq("regions"))).thenReturn("img.png");

        Region saved = new Region();
        saved.setId(1);
        saved.setName("Centre");
        saved.setImage("img.png");

        when(regionRepository.save(any())).thenReturn(saved);

        RegionListDTO result = regionService.create(dto);

        assertThat(result.name()).isEqualTo("Centre");
        assertThat(result.imgUrl()).contains("img.png");
        verify(mediaService).saveImage(any(), eq("regions"));
        verify(regionRepository).save(any());
    }


    @Test
    void create_shouldThrowIfNameExists() {
        RegionCreateDTO dto = new RegionCreateDTO(
                "Centre", 20000, 500000, "3.8;11.5", "Yaoundé", "CE", mock(MultipartFile.class)
        );

        when(regionRepository.existsByName("Centre")).thenReturn(true);

        assertThatThrownBy(() -> regionService.create(dto))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("existe déjà");
    }

    // --------------------------------------------------------------
    //                     FIND
    // --------------------------------------------------------------

    @Test
    void find_shouldReturnRegion() {
        when(regionRepository.findById(1)).thenReturn(Optional.of(region));
        when(appointmentRepository.findByCirconscription_IdInAndEndDateIsNull(List.of(1))).thenReturn(List.of());
        when(delimitationRepository.findByCirconscription_IdIn(List.of(1))).thenReturn(List.of());

        RegionListDTO result = regionService.find(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.name()).isEqualTo("Centre");
    }

    @Test
    void find_shouldThrowIfNotFound() {
        when(regionRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> regionService.find(99))
                .isInstanceOf(EntityNotFoundException.class);
    }

    // --------------------------------------------------------------
    //                     LIST ALL
    // --------------------------------------------------------------

    @Test
    void listAll_shouldReturnEmptyListWhenNoRegion() {
        when(regionRepository.findAll()).thenReturn(List.of());

        assertThat(regionService.listAll()).isEmpty();
    }

    @Test
    void listAll_shouldReturnRegions() {
        when(regionRepository.findAll()).thenReturn(List.of(region));
        when(appointmentRepository.findByCirconscription_IdInAndEndDateIsNull(List.of(1))).thenReturn(List.of());
        when(delimitationRepository.findByCirconscription_IdIn(List.of(1))).thenReturn(List.of());

        List<RegionListDTO> result = regionService.listAll();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().name()).isEqualTo("Centre");
    }

    // --------------------------------------------------------------
    //                     UPDATE
    // --------------------------------------------------------------

    @Test
    void update_shouldUpdateSuccessfully() throws Exception {
        RegionCreateDTO dto = new RegionCreateDTO(
                "Centre", 30000, 600000, "3.7;11.4", "Yaoundé", "CE", mockFile("new.png")
        );

        when(regionRepository.findById(1)).thenReturn(Optional.of(region));
        when(mediaService.saveImage(any(), eq("regions"))).thenReturn("new.png");
        when(regionRepository.save(any())).thenReturn(region);

        when(appointmentRepository.findByCirconscription_IdInAndEndDateIsNull(List.of(1))).thenReturn(List.of());
        when(delimitationRepository.findByCirconscription_IdIn(List.of(1))).thenReturn(List.of());

        RegionListDTO result = regionService.update(1, dto);

        assertThat(result.population()).isEqualTo(600000);
        verify(mediaService).deleteImage("old.png");
    }


    // --------------------------------------------------------------
    //                     DELETE
    // --------------------------------------------------------------

    @Test
    void delete_shouldDeleteRegion() {
        region.setDivisionsList(List.of()); // pas de départements → suppression autorisée

        when(regionRepository.findById(1)).thenReturn(Optional.of(region));

        regionService.delete(1);

        verify(regionRepository).delete(region);
        verify(mediaService).deleteImage("old.png");
    }

    @Test
    void delete_shouldThrowIfHasDivisions() {
        region.setDivisionsList(List.of(new com.devekoc.camerAtlas.entities.Division()));

        when(regionRepository.findById(1)).thenReturn(Optional.of(region));

        assertThatThrownBy(() -> regionService.delete(1))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    // --------------------------------------------------------------
    //                     HELPERS
    // --------------------------------------------------------------

    private MultipartFile mockFile(String name) {
        MultipartFile file = mock(MultipartFile.class);
        lenient().when(file.isEmpty()).thenReturn(false);
        lenient().when(file.getOriginalFilename()).thenReturn(name);
        return file;
    }
}

