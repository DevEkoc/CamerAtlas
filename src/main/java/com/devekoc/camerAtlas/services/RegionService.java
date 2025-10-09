package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.dto.departement.DepartementListerDansRegionDTO;
import com.devekoc.camerAtlas.dto.region.RegionCreateDTO;
import com.devekoc.camerAtlas.dto.region.RegionListerDTO;
import com.devekoc.camerAtlas.entities.Departement;
import com.devekoc.camerAtlas.entities.Region;
import com.devekoc.camerAtlas.repositories.RegionRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RegionService {
    private final RegionRepository regionRepository;

    public RegionService(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    public Region creer(RegionCreateDTO dto) {
        if (regionRepository.existsByNom(dto.nom())) {
            throw new DataIntegrityViolationException(
                    "Une région avec le nom : " + dto.nom() + " existe déjà !"
            );
        }

        Region region = new Region();
        BeanUtils.copyProperties(dto, region);
        return regionRepository.save(region);
    }

    public List<RegionListerDTO> lister() {
        List<Region> regions = regionRepository.findAll();
        List<RegionListerDTO> dtos = new ArrayList<>();

        for (Region region : regions) {
            List<DepartementListerDansRegionDTO> dpt = new ArrayList<>();

            for (Departement departement : region.getListeDepartements()) {
                dpt.add(new DepartementListerDansRegionDTO(
                        departement.getId(),
                        departement.getNom(),
                        departement.getSuperficie(),
                        departement.getPopulation(),
                        departement.getCoordonnees(),
                        departement.getPrefecture()
                ));
            }
            RegionListerDTO dto = new RegionListerDTO(
                    region.getNom(),
                    region.getSuperficie(),
                    region.getPopulation(),
                    region.getCoordonnees(),
                    region.getChefLieu(),
                    region.getCodeMineralogique(),
                    dpt
            );
            dtos.add(dto);
        }
        return dtos;
    }

    public Region rechercher(int id) {
        return regionRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("La région n'existe pas")
        );
    }

    public Region rechercher(String nom) {
        return regionRepository.findByNom(nom).orElseThrow(
                () -> new EntityNotFoundException("La région n'existe pas")
        );
    }

    public Region modifier(int id, Region region) {
        Region ancienneRegion = regionRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("La région n'existe pas !")
        );
        ancienneRegion.setNom(region.getNom());
        ancienneRegion.setPopulation(region.getPopulation());
        ancienneRegion.setCoordonnees(region.getCoordonnees());
        ancienneRegion.setSuperficie(region.getSuperficie());
        ancienneRegion.setChefLieu(region.getChefLieu());
        ancienneRegion.setCodeMineralogique(region.getCodeMineralogique());
        return regionRepository.save(ancienneRegion);

    }

    public void supprimer(int id) {
        System.out.println("Suppression par id");
        if (regionRepository.existsById(id)) {
            regionRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("La région n'existe pas");
        }

    }

    @Transactional
    // Transactional car deleteByNom n'est pas une méthode CRUD standart
    public void supprimer(String nom) {
        System.out.println("Suppression par nom");
        if (regionRepository.existsByNom(nom)) {
            regionRepository.deleteByNom(nom);
        } else {
            throw new EntityNotFoundException("La région n'existe pas");
        }
    }
}
