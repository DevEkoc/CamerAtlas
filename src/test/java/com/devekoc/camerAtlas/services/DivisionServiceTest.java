package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.dto.division.DivisionCreateDTO;
import com.devekoc.camerAtlas.dto.division.DivisionListDTO;
import com.devekoc.camerAtlas.entities.Division;
import com.devekoc.camerAtlas.entities.Region;
import com.devekoc.camerAtlas.entities.SubDivision;
import com.devekoc.camerAtlas.repositories.AppointmentRepository;
import com.devekoc.camerAtlas.repositories.DelimitationRepository;
import com.devekoc.camerAtlas.repositories.DivisionRepository;
import com.devekoc.camerAtlas.repositories.RegionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DivisionServiceTest {

    @InjectMocks
    private DivisionService divisionService;

    @Mock
    private DivisionRepository divisionRepository;
    @Mock private RegionRepository regionRepository;
    @Mock private MediaService mediaService;
    @Mock private AppointmentRepository appointmentRepository;
    @Mock private DelimitationRepository delimitationRepository;

    // ============================
    // CREATE
    // ============================

    @Test
    void create_shouldCreateDivisionSuccessfully() throws Exception {
        // arrange
        DivisionCreateDTO dto = new DivisionCreateDTO(
                "Littoral",
                1000,
                3000000,
                "3.867, 9.216",
                "Douala",
                1,
                mockMultipart()
        );

        Region region = new Region();
        region.setId(1);
        region.setName("Littoral");

        Division saved = new Division();
        saved.setId(10);
        saved.setName("Littoral");
        saved.setRegion(region);
        saved.setImage("/img/divisions/test.png");

        when(divisionRepository.existsByName("Littoral")).thenReturn(false);
        when(regionRepository.findById(1)).thenReturn(Optional.of(region));
        when(mediaService.saveImage(any(), eq("divisions"))).thenReturn("/img/divisions/test.png");
        when(divisionRepository.save(any(Division.class))).thenReturn(saved);

        // act
        DivisionListDTO result = divisionService.create(dto);

        // assert
        assertEquals(10, result.id());
        assertEquals("Littoral", result.name());

        verify(divisionRepository).existsByName("Littoral");
        verify(divisionRepository).save(any(Division.class));
    }

    @Test
    void create_shouldThrow_whenNameAlreadyExists() {
        DivisionCreateDTO dto = new DivisionCreateDTO(
                "Littoral",
                1000,
                2000000,
                "gps",
                "Douala",
                1,
                mockMultipart()
        );

        when(divisionRepository.existsByName("Littoral")).thenReturn(true);

        assertThrows(
                DataIntegrityViolationException.class,
                () -> divisionService.create(dto)
        );

        verify(divisionRepository).existsByName("Littoral");
    }

    @Test
    void create_shouldThrow_whenRegionNotFound() {
        DivisionCreateDTO dto = new DivisionCreateDTO(
                "Littoral", 1000, 2_000_000, "gps", "Douala", 99, mockMultipart()
        );

        when(divisionRepository.existsByName("Littoral")).thenReturn(false);
        when(regionRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> divisionService.create(dto)
        );
    }

    // ============================
    // FIND
    // ============================

    @Test
    void find_shouldReturnDivision() {
        Region region = new Region();
        region.setId(5);
        region.setName("Centre");

        Division division = new Division();
        division.setId(5);
        division.setName("Centre");
        division.setRegion(region);

        when(divisionRepository.findById(5)).thenReturn(Optional.of(division));
        when(appointmentRepository.findByCirconscription_IdInAndEndDateIsNull(List.of(5)))
                .thenReturn(List.of());
        when(delimitationRepository.findByCirconscription_IdIn(List.of(5)))
                .thenReturn(List.of());

        DivisionListDTO result = divisionService.find(5);

        assertEquals(5, result.id());
        assertEquals("Centre", result.name());
    }

    @Test
    void find_shouldThrow_whenNotFound() {
        when(divisionRepository.findById(123)).thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> divisionService.find(123)
        );
    }

    // ============================
    // UPDATE
    // ============================

    @Test
    void update_shouldUpdateDivisionSuccessfully() throws Exception {
        // --- Arrange ---
        int id = 10;

        Region region = new Region();
        region.setId(1);
        region.setName("Centre");

        Division existing = new Division();
        existing.setId(id);
        existing.setName("Littoral");
        existing.setRegion(region);      // ⬅⬅⬅ OBLIGATOIRE POUR ÉVITER LE NPE
        existing.setImage("old.png");

        DivisionCreateDTO dto = new DivisionCreateDTO(
                "Littoral", 100, 200_000, "4.0;9.7", "Bonajo",
                1, mockMultipart()
        );

        when(divisionRepository.findById(id)).thenReturn(Optional.of(existing));
        when(regionRepository.findById(1)).thenReturn(Optional.of(region));
        lenient().when(mediaService.saveImage(any(), anyString())).thenReturn("new.png");
        when(divisionRepository.save(any())).thenReturn(existing);

        when(appointmentRepository.findByCirconscription_IdInAndEndDateIsNull(List.of(id)))
                .thenReturn(List.of());

        when(delimitationRepository.findByCirconscription_IdIn(List.of(id)))
                .thenReturn(List.of());

        // --- Act ---
        DivisionListDTO result = divisionService.update(id, dto);

        // --- Assert ---
        assertEquals("Littoral", result.name());
        assertEquals(1, result.regionId());
    }


    @Test
    void update_shouldThrow_whenNameAlreadyExists() {
        Division existing = new Division();
        existing.setId(7);
        existing.setName("Nord");

        DivisionCreateDTO dto = new DivisionCreateDTO(
                "Est", 500, 700000, "gps", "Bertoua", 1, null
        );

        when(divisionRepository.findById(7)).thenReturn(Optional.of(existing));
        when(divisionRepository.existsByName("Est")).thenReturn(true);

        assertThrows(
                DataIntegrityViolationException.class,
                () -> divisionService.update(7, dto)
        );
    }

    // ============================
    // DELETE
    // ============================

    @Test
    void delete_shouldDeleteWhenNoSubdivisions() {
        Division division = new Division();
        division.setId(8);
        division.setName("Nord");

        when(divisionRepository.findById(8)).thenReturn(Optional.of(division));

        division.setSubDivisionsList(List.of()); // pas de subdivisions

        divisionService.delete(8);

        verify(mediaService).deleteImage(division.getImage());
        verify(divisionRepository).delete(division);
    }

    @Test
    void delete_shouldThrow_whenHasSubdivisions() {
        Division division = new Division();
        division.setId(8);
        division.setSubDivisionsList(List.of(new SubDivision()));

        when(divisionRepository.findById(8)).thenReturn(Optional.of(division));

        assertThrows(
                DataIntegrityViolationException.class,
                () -> divisionService.delete(8)
        );

        verify(divisionRepository, never()).delete(any());
    }

    // ============================
    // UTILITAIRE
    // ============================

    private MultipartFile mockMultipart() {
        MultipartFile file = mock(MultipartFile.class);
        lenient().when(file.isEmpty()).thenReturn(false);
        return file;
    }
}

