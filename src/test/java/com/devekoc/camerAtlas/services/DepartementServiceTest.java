package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.dto.departement.DepartementCreateDTO;
import com.devekoc.camerAtlas.dto.departement.DepartementListerDTO;
import com.devekoc.camerAtlas.entities.Departement;
import com.devekoc.camerAtlas.entities.Region;
import com.devekoc.camerAtlas.repositories.DepartementRepository;
import com.devekoc.camerAtlas.repositories.RegionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Transactional
public class DepartementServiceTest {

    @Mock
    private DepartementRepository departementRepository;

    @Mock
    private RegionRepository regionRepository;

    @InjectMocks
    private DepartementService departementService;

    @Test
    void doitCreerUnDepartement() {
        DepartementCreateDTO createDTO = new DepartementCreateDTO(
                "Lom-et-Djerem",
                26345,
                228700,
                "Latitude Nord, Longitude Est",
                "Bertoua",
                1
        );

        Region region = new Region();
        region.setId(1);
        region.setNom("Est");
        region.setPopulation(123467);
        region.setSuperficie(123456);
        region.setChefLieu("Bertoua");
        region.setCodeMineralogique("ES");

        Departement departement = new Departement();
        departement.setNom(createDTO.nom());
        departement.setRegion(region);

        when(regionRepository.findById(1)).thenReturn(Optional.of(region));
        when(departementRepository.existsByNom(anyString())).thenReturn(false);
        when(departementRepository.save(any(Departement.class))).thenReturn(departement);

        DepartementListerDTO result = departementService.creer(createDTO);

        assertThat(result).isNotNull();
        assertThat(result.nom()).isEqualTo("Lom-et-Djerem");
        verify(departementRepository).save(any(Departement.class));
    }

    @Test
    void doitLeverUneExceptionSiLeNomDuDepartementExisteDeja() {
        DepartementCreateDTO createDTO = new DepartementCreateDTO(
                "Lom-et-Djerem",
                26345,
                228700,
                "Latitude Nord, Longitude Est",
                "Bertoua",
                1
        );

//        when(regionRepository.findById(1)).thenReturn(Optional.of(new Region()));
        when(departementRepository.existsByNom("Lom-et-Djerem")).thenReturn(true);

        assertThrows(DataIntegrityViolationException.class, () -> {
            departementService.creer(createDTO);
        });

        verify(departementRepository, never()).save(any(Departement.class));
    }

    @Test
    void doitLeverUneExceptionSiLaRegionNExistePas() {
        DepartementCreateDTO createDTO = new DepartementCreateDTO(
                "Lom-et-Djerem",
                26345,
                228700,
                "Latitude Nord, Longitude Est",
                "Bertoua",
                99 // ID inexistant
        );

        when(regionRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            departementService.creer(createDTO);
        });

        verify(departementRepository, never()).save(any(Departement.class));
    }

    @Test
    void doitListerTousLesDepartements() {
        Departement departement = new Departement();
        departement.setNom("Kadey");
        departement.setPopulation(123467);
        departement.setSuperficie(123456);
        departement.setPrefecture("Batouri");
        departement.setCoordonnees("coords");

        Region region = new Region();
        region.setNom("Est");
        region.setPopulation(123467);
        region.setSuperficie(123456);
        region.setChefLieu("Bertoua");
        region.setCoordonnees("coords");
        region.setCodeMineralogique("ES");

        departement.setRegion(region);

        when(departementRepository.findAll()).thenReturn(Collections.singletonList(departement));

        List<DepartementListerDTO> results = departementService.lister();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).nom()).isEqualTo("Kadey");
        assertThat(results.get(0).nomRegion()).isEqualTo("Est");
    }
}
