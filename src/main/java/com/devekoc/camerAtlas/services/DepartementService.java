package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.entities.Departement;
import com.devekoc.camerAtlas.repositories.DepartementRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartementService {
    private final DepartementRepository departementRepository;

    public DepartementService(DepartementRepository departementRepository) {
        this.departementRepository = departementRepository;
    }

    public Departement creer(Departement departement) {
        return departementRepository.save(departement);
    }

    public List<Departement> lister() {
        return departementRepository.findAll();
    }

    public Departement rechercher(int id) {
        return departementRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Le département n'existe pas")
        );
    }

    public Departement rechercher(String nom) {
        return departementRepository.findByNom(nom).orElseThrow(
                () -> new EntityNotFoundException("Le département n'existe pas")
        );
    }

    public Departement modifier(int id, Departement departement) {
        Departement ancienDepartement = departementRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Le département n'existe pas !")
        );
        ancienDepartement.setNom(departement.getNom());
        ancienDepartement.setPopulation(departement.getPopulation());
        ancienDepartement.setCoordonnees(departement.getCoordonnees());
        ancienDepartement.setSuperficie(departement.getSuperficie());
        ancienDepartement.setPrefecture(departement.getPrefecture());
        ancienDepartement.setRegion(departement.getRegion());
        return departementRepository.save(ancienDepartement);

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
