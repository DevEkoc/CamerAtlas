package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.dto.departement.DepartementCreateDTO;
import com.devekoc.camerAtlas.dto.departement.DepartementListerDTO;
import com.devekoc.camerAtlas.entities.Departement;
import com.devekoc.camerAtlas.entities.Region;
import com.devekoc.camerAtlas.repositories.DepartementRepository;
import com.devekoc.camerAtlas.repositories.RegionRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DepartementService {
    private final DepartementRepository departementRepository;
    private final RegionRepository regionRepository;

    public DepartementService(DepartementRepository departementRepository, RegionRepository regionRepository) {
        this.departementRepository = departementRepository;
        this.regionRepository = regionRepository;
    }

    public DepartementListerDTO creer(DepartementCreateDTO dto) {
        if (departementRepository.existsByNom(dto.nom())) {
            throw new DataIntegrityViolationException(
                    "Un département avec le nom '" + dto.nom() + "' existe déjà"
            );
        }

        Region region = regionRepository.findById(dto.idRegion()).orElseThrow(
                ()-> new EntityNotFoundException("Aucune région trouvée avec l'ID : " + dto.idRegion())
        );

        // La copie à la main avec la méthode statique 'fromCreateDTO'
        // est plus performante que 'BeanUtils.copyProperties'
        Departement saved = departementRepository.save(Departement.fromCreateDTO(dto, region));
        return toDTO(saved);
    }

    public List<DepartementListerDTO> lister() {
        return departementRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public DepartementListerDTO rechercher(int id) {
        Departement departement = departementRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Le département n'existe pas")
        );

        return toDTO(departement);
    }

    public DepartementListerDTO rechercher(String nom) {
        Departement departement = departementRepository.findByNom(nom).orElseThrow(
                () -> new EntityNotFoundException("Le département n'existe pas")
        );

        return toDTO(departement);
    }

    public DepartementListerDTO modifier(int id, DepartementCreateDTO dto) {
        Departement existant = departementRepository.findById(id).orElseThrow(
                ()-> new EntityNotFoundException("Aucun Département trouvé avec l'ID : " + id)
        );

        if (departementRepository.existsByNomAndIdNot(dto.nom(), id)) {
            throw new DataIntegrityViolationException(
                    "Un département avec le nom '" + dto.nom() + "' existe déjà"
            );
        }

        Region region = regionRepository.findById(dto.idRegion()).orElseThrow(
                ()-> new EntityNotFoundException("Aucune région trouvée avec l'ID : " + dto.idRegion())
        );

        // La copie à la main avec la méthode statique 'updateFromDTO'
        // est plus performante que 'BeanUtils.copyProperties'
        existant.updateFromDTO(dto, region);
        return toDTO(departementRepository.save(existant));

    }

    public void supprimer(int id) {
        if (!departementRepository.existsById(id)) {
            throw new EntityNotFoundException("Le département n'existe pas");
        }
        departementRepository.deleteById(id);
    }

    @Transactional
    // Transactional car deleteByNom n'est pas une méthode CRUD standart
    public void supprimer(String nom) {
        if (!departementRepository.existsByNom(nom)) {
            throw new EntityNotFoundException("Le département n'existe pas");
        }
        departementRepository.deleteByNom(nom);
    }

    public DepartementListerDTO toDTO (Departement departement) {
        return new DepartementListerDTO(
                departement.getId(),
                departement.getNom(),
                departement.getSuperficie(),
                departement.getPopulation(),
                departement.getCoordonnees(),
                departement.getPrefecture(),
                departement.getRegion().getId(),
                departement.getRegion().getNom()
        );
    }
}
