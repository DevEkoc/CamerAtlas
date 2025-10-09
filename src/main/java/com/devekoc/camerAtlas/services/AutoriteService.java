package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.entities.Autorite;
import com.devekoc.camerAtlas.repositories.AutoriteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
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
                () -> new EntityNotFoundException("Aucune Autorité trouvée avec l'ID : " + id)
        );
    }

    public void modifier(int id, Autorite autorite) {
        Autorite existante = rechercher(id);
        if (autorite.getId() != id) {
            throw new IllegalArgumentException("L'ID dans l'URL ne correspond pas à l'ID de l'objet !");
        }
        BeanUtils.copyProperties(autorite, existante, "id");
        autoriteRepository.save(existante);
    }

    public void supprimer(int id) {
        autoriteRepository.delete(rechercher(id));
    }
}
