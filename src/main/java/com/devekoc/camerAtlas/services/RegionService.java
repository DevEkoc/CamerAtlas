package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.entities.Region;
import com.devekoc.camerAtlas.repositories.RegionRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RegionService {
    private final RegionRepository regionRepository;

    public RegionService(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    public Region creer(Region region) {
        return regionRepository.save(region);
    }

    public List<Region> lister() {
        return regionRepository.findAll();
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
