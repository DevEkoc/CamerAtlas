package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.entities.Administrateur;
import com.devekoc.camerAtlas.repositories.AdministratorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdministratorService {
    public AdministratorRepository administratorRepository;

    public AdministratorService(AdministratorRepository administratorRepository) {
        this.administratorRepository = administratorRepository;
    }

    public void creer(Administrateur administrateur) {
        administratorRepository.save(administrateur);
    }

    public List<Administrateur> listerAdministrateurs() {
        return administratorRepository.findAll();
    }

    public Administrateur rechercher(int id) {
        return administratorRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Administrateur non trouvé !")
        );
    }

    public void modifier(int id, Administrateur administrateur) {
        Administrateur ancienAdmin = rechercher(id);
        if (administrateur.getId() != id) {
            throw new IllegalArgumentException("L'ID dans l'URL ne correspond pas à l'ID de l'objet !");
        }
        ancienAdmin.setNom(administrateur.getNom());
        ancienAdmin.setPrenom(administrateur.getPrenom());
        ancienAdmin.setDateNaissance(administrateur.getDateNaissance());
        administratorRepository.save(ancienAdmin);
    }

    public void supprimer(int id) {
        administratorRepository.delete(rechercher(id));
    }
}
