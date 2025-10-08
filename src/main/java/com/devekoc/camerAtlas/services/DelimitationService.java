package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.dto.DelimitationRequestDTO;
import com.devekoc.camerAtlas.entities.Circonscription;
import com.devekoc.camerAtlas.entities.Delimitation;
import com.devekoc.camerAtlas.entities.Frontiere;
import com.devekoc.camerAtlas.entities.primaryKeys.DelimitationPK;
import com.devekoc.camerAtlas.repositories.CirconscriptionRepository;
import com.devekoc.camerAtlas.repositories.DelimitationRepository;
import com.devekoc.camerAtlas.repositories.FrontiereRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DelimitationService {
    private final DelimitationRepository delimitationRepository;
    private final FrontiereRepository frontiereRepository;
    private final CirconscriptionRepository circonscriptionRepository;

    public DelimitationService(DelimitationRepository delimitationRepository, FrontiereRepository frontiereRepository, CirconscriptionRepository circonscriptionRepository) {
        this.delimitationRepository = delimitationRepository;
        this.frontiereRepository = frontiereRepository;
        this.circonscriptionRepository = circonscriptionRepository;
    }

    public void creer(DelimitationRequestDTO delimitationRequest) {
        // Étape 1: Récupérer les entités managées à partir de la base de données.
        // orElseThrow() déballe l'Optional et lève une exception si l'entité n'est pas trouvée.
        Frontiere frontiere = frontiereRepository.findById(delimitationRequest.idFrontiere())
                .orElseThrow(() -> new EntityNotFoundException("Frontière non trouvée avec l'ID : " + delimitationRequest.idFrontiere()));

        Circonscription circonscription = circonscriptionRepository.findById(delimitationRequest.codeCirconscription())
                .orElseThrow(() -> new EntityNotFoundException("Circonscription non trouvée avec l'ID : " + delimitationRequest.codeCirconscription()));

        // Étape 2: Utiliser le constructeur de convenance pour créer la nouvelle entité Delimitation.
        // JPA sait que 'frontiere' et 'circonscription' sont des entités existantes et ne tentera pas de les recréer.

        // Étape 3: Sauvegarder la nouvelle entité de relation.
        delimitationRepository.save(new Delimitation(frontiere, circonscription));
    }

    public List<Delimitation> lister() {
        return delimitationRepository.findAll();
    }

    public void supprimer(DelimitationPK id) {
        if (!delimitationRepository.existsById(id)) {
            throw new EntityNotFoundException("Délimitation introuvable !");
        }
        delimitationRepository.deleteById(id);
    }
}
