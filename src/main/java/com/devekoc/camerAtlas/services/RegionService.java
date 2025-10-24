package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.dto.departement.DepartementListerDansRegionDTO;
import com.devekoc.camerAtlas.dto.region.RegionCreateDTO;
import com.devekoc.camerAtlas.dto.region.RegionListerDTO;
import com.devekoc.camerAtlas.entities.Region;
import com.devekoc.camerAtlas.repositories.RegionRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class RegionService {
    private final RegionRepository regionRepository;

    public RegionService(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    public RegionListerDTO creer(RegionCreateDTO dto) {
        if (regionRepository.existsByNom(dto.nom())) {
            throw new DataIntegrityViolationException(
                    "Une région avec le nom : " + dto.nom() + " existe déjà !"
            );
        }
        // La copie à la main avec la méthode statique 'fromCreateDTO'
        // est plus performante que 'BeanUtils.copyProperties'
        Region saved = regionRepository.save(Region.fromCreateDTO(dto));
        return toDTO(saved);
    }

    public List<RegionListerDTO> lister() {
        return regionRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public RegionListerDTO rechercher(int id) {
        Region region = regionRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("La région n'existe pas")
        );

        return toDTO(region);
    }

    public RegionListerDTO rechercher(String nom) {
        Region region = regionRepository.findByNom(nom).orElseThrow(
                () -> new EntityNotFoundException("La région n'existe pas")
        );

        return toDTO(region);
    }

    public RegionListerDTO modifier(int id, RegionCreateDTO dto) {
        Region existante = regionRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("La région n'existe pas !")
        );

        // Vérifier si le nouveau nom n'existe pas déjà
        if (!existante.getNom().equals(dto.nom()) && regionRepository.existsByNom(dto.nom())) {
            throw new DataIntegrityViolationException(
                    "Une région avec le nom '" + dto.nom() + "' existe déjà"
            );
        }

        // La copie à la main avec la méthode statique 'updateFromDTO'
        // est plus performante que 'BeanUtils.copyProperties'
        existante.updateFromDTO(dto);
        return toDTO(regionRepository.save(existante));
    }

    public void supprimer(int id) {
        Region existante = regionRepository.findById(id).orElseThrow(
                ()-> new EntityNotFoundException("La région n'existe pas")
        );
        if (!existante.getListeDepartements().isEmpty()) {
            throw new DataIntegrityViolationException("Impossible de supprimer une région contenant des départements");
        }
        regionRepository.deleteById(id);
    }

    @Transactional
    // Transactional car deleteByNom n'est pas une méthode CRUD standard
    public void supprimer(String nom) {
        Region existante = regionRepository.findByNom(nom).orElseThrow(
                ()-> new EntityNotFoundException("La région n'existe pas")
        );
        if (!existante.getListeDepartements().isEmpty()) {
            throw new DataIntegrityViolationException("Impossible de supprimer une région contenant des départements");
        }
        regionRepository.deleteByNom(nom);
    }

    private RegionListerDTO toDTO (Region region) {
        List<DepartementListerDansRegionDTO> departements = Optional.ofNullable(region.getListeDepartements())
                .orElse(Collections.emptyList())
                .stream()
                .map( dept -> new DepartementListerDansRegionDTO(
                        dept.getId(),
                        dept.getNom(),
                        dept.getSuperficie(),
                        dept.getPopulation(),
                        dept.getCoordonnees(),
                        dept.getPrefecture()
                ))
                .toList();
        return new RegionListerDTO(
                region.getId(),
                region.getNom(),
                region.getSuperficie(),
                region.getPopulation(),
                region.getCoordonnees(),
                region.getChefLieu(),
                region.getCodeMineralogique(),
                departements
        );
    }
}
