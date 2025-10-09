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

    public Departement creer(DepartementCreateDTO dto) {
        if (departementRepository.existsByNom(dto.nom())) {
            throw new DataIntegrityViolationException(
                    "Un département avec le nom '" + dto.nom() + "' existe déjà"
            );
        }

        Region region = regionRepository.findById(dto.idRegion()).orElseThrow(
                ()-> new EntityNotFoundException("Aucune région trouvée avec l'ID : " + dto.idRegion())
        );

        Departement departement = new Departement();
        BeanUtils.copyProperties(dto, departement);
        departement.setRegion(region);
        return departementRepository.save(departement);
    }

    public List<DepartementListerDTO> lister() {
        List<Departement> departements = departementRepository.findAll();
        List<DepartementListerDTO> dtos = new ArrayList<>();

        for (Departement departement : departements) {
            DepartementListerDTO dto = new DepartementListerDTO(
                    departement.getId(),
                    departement.getNom(),
                    departement.getSuperficie(),
                    departement.getPopulation(),
                    departement.getCoordonnees(),
                    departement.getPrefecture(),
                    departement.getRegion().getId(),
                    departement.getRegion().getNom()
            );
            dtos.add(dto);
        }

        return dtos;
    }

    public DepartementListerDTO rechercher(int id) {
        Departement departement = departementRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Le département n'existe pas")
        );

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

    public DepartementListerDTO rechercher(String nom) {
        Departement departement = departementRepository.findByNom(nom).orElseThrow(
                () -> new EntityNotFoundException("Le département n'existe pas")
        );

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

    public Departement modifier(int id, DepartementListerDTO dto) {
        Departement existant =  departementRepository.findById(id).orElseThrow(
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

        BeanUtils.copyProperties(dto, existant, "id");
        existant.setRegion(region);

        return departementRepository.save(existant);

    }

    public void supprimer(int id) {
        System.out.println("Suppression par id");
        if (departementRepository.existsById(id)) {
            departementRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Le département n'existe pas");
        }

    }

    @Transactional
    // Transactional car deleteByNom n'est pas une méthode CRUD standart
    public void supprimer(String nom) {
        System.out.println("Suppression par nom");
        if (departementRepository.existsByNom(nom)) {
            departementRepository.deleteByNom(nom);
        } else {
            throw new EntityNotFoundException("Le département n'existe pas");
        }
    }
}
