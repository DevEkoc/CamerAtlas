package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.entities.Arrondissement;
import com.devekoc.camerAtlas.repositories.ArrondissementRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArrondissementService {
    private final ArrondissementRepository arrondissementRepository;

    public ArrondissementService(ArrondissementRepository arrondissementRepository) {
        this.arrondissementRepository = arrondissementRepository;
    }

    public Arrondissement creer(Arrondissement arrondissement) {
        return arrondissementRepository.save(arrondissement);
    }

    public List<Arrondissement> lister() {
        return arrondissementRepository.findAll();
    }

    public Arrondissement rechercher(int id) {
        return arrondissementRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("L'arrondissement n'existe pas")
        );
    }

    public Arrondissement rechercher(String nom) {
        return arrondissementRepository.findByNom(nom).orElseThrow(
                () -> new EntityNotFoundException("L'arrondissement n'existe pas")
        );
    }

    public Arrondissement modifier(int id, Arrondissement arrondissement) {
        Arrondissement ancienArrondissement = arrondissementRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("L'arrondissement n'existe pas")
        );
        ancienArrondissement.setNom(arrondissement.getNom());
        ancienArrondissement.setPopulation(arrondissement.getPopulation());
        ancienArrondissement.setCoordonnees(arrondissement.getCoordonnees());
        ancienArrondissement.setSuperficie(arrondissement.getSuperficie());
        ancienArrondissement.setSousPrefecture(arrondissement.getSousPrefecture());
        ancienArrondissement.setDepartement(ancienArrondissement.getDepartement());
        return arrondissementRepository.save(ancienArrondissement);

    }

    public void supprimer(int id) {
        System.out.println("Suppression par id");
        if (arrondissementRepository.existsById(id)) {
            arrondissementRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("L'arrondissement n'existe pas");
        }

    }

    @Transactional
    // Transactional car deleteByNom n'est pas une méthode CRUD standart
    public void supprimer(String nom) {
        System.out.println("Suppression par nom");
        if (arrondissementRepository.existsByNom(nom)) {
            arrondissementRepository.deleteByNom(nom);
        } else {
            throw new EntityNotFoundException("L'arrondissement n'existe pas");
        }
    }
}
