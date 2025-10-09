package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.entities.Frontiere;
import com.devekoc.camerAtlas.enumerations.TypeFrontiere;
import com.devekoc.camerAtlas.repositories.FrontiereRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FrontiereService {
    private final FrontiereRepository frontiereRepository;

    public FrontiereService(FrontiereRepository frontiereRepository) {
        this.frontiereRepository = frontiereRepository;
    }

    public void creer(Frontiere frontiere) {
        frontiereRepository.save(frontiere);
    }

    public List<Frontiere> lister() {
        return frontiereRepository.findAll();
    }

    public List<Frontiere> rechercherParType(TypeFrontiere typeFrontiere) {
        return frontiereRepository.findByType(typeFrontiere);
    }


}
