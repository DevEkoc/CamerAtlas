package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.entities.Autorite;
import com.devekoc.camerAtlas.repositories.AutoriteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AutoriteService {
    private final AutoriteRepository autoriteRepository;

    public AutoriteService(AutoriteRepository autoriteRepository) {
        this.autoriteRepository = autoriteRepository;
    }

    public void creer(Autorite autorite) {
        autoriteRepository.save(autorite);
    }

    public List<Autorite> lister() {
        return autoriteRepository.findAll();
    }

    public Autorite rechercher(int id) {
        return autoriteRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Autorité non trouvée !")
        );
    }

    public void modifier(int id, Autorite autorite) {
        Autorite ancienAdmin = rechercher(id);
        if (autorite.getId() != id) {
            throw new IllegalArgumentException("L'ID dans l'URL ne correspond pas à l'ID de l'objet !");
        }
        ancienAdmin.setNom(autorite.getNom());
        ancienAdmin.setPrenom(autorite.getPrenom());
        ancienAdmin.setDateNaissance(autorite.getDateNaissance());
        autoriteRepository.save(ancienAdmin);
    }

    public void supprimer(int id) {
        autoriteRepository.delete(rechercher(id));
    }
}
