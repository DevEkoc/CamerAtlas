package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.entities.Circonscription;
import com.devekoc.camerAtlas.repositories.CirconscriptionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CirconscriptionService {
    private final CirconscriptionRepository circonscriptionRepository;

    public CirconscriptionService(CirconscriptionRepository circonscriptionRepository) {
        this.circonscriptionRepository = circonscriptionRepository;
    }

    public void creer(Circonscription circonscription) {
        circonscriptionRepository.save(circonscription);
    }

    public List<Circonscription> lister() {
        return circonscriptionRepository.findAll();
    }

    public Circonscription rechercher(int id) {
        return circonscriptionRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Circonscription non trouvée !")
        );
    }

    public void modifier(int id, Circonscription circonscription) {
        Circonscription ancienneCirc = rechercher(id);
        if (circonscription.getId() != id) {
            throw new IllegalArgumentException("L'ID dans l'URL ne correspond pas à l'ID de l'objet !");
        }
        ancienneCirc.setNom(circonscription.getNom());
        ancienneCirc.setPopulation(circonscription.getPopulation());
        ancienneCirc.setSuperficie(circonscription.getSuperficie());
        ancienneCirc.setCoordonnees(circonscription.getCoordonnees());
        circonscriptionRepository.save(ancienneCirc);
    }

    public void supprimer(int id) {
        circonscriptionRepository.delete(rechercher(id));
    }
}
