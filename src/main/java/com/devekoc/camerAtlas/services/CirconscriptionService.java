package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.entities.Circonscription;
import com.devekoc.camerAtlas.repositories.CirconscriptionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
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
                () -> new EntityNotFoundException("Aucune Circonscription trouvée avec l'ID : " + id)
        );
    }

    public void modifier(int id, Circonscription circonscription) {
        Circonscription existante = rechercher(id);
        if (circonscription.getId() != id) {
            throw new IllegalArgumentException("L'ID dans l'URL ne correspond pas à l'ID de l'objet !");
        }
        BeanUtils.copyProperties(circonscription, existante, "id");
        circonscriptionRepository.save(existante);
    }

    public void supprimer(int id) {
        circonscriptionRepository.delete(rechercher(id));
    }
}
