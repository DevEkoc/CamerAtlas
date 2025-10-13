package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.dto.delimitation.DelimitationCreateDTO;
import com.devekoc.camerAtlas.dto.delimitation.DelimitationListerDTO;
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

    public DelimitationListerDTO creer(DelimitationCreateDTO dto) {
        // Étape 1 : Récupérer les entités managées à partir de la base de données.
        // orElseThrow() déballe l'Optional et lève une exception si l'entité n'est pas trouvée.
        Frontiere frontiere = frontiereRepository.findById(dto.idFrontiere())
                .orElseThrow(() -> new EntityNotFoundException("Aucune Frontière trouvée avec l'ID : " + dto.idFrontiere()));

        Circonscription circonscription = circonscriptionRepository.findById(dto.codeCirconscription())
                .orElseThrow(() -> new EntityNotFoundException("Aucune Circonscription trouvée avec l'ID : " + dto.codeCirconscription()));

        // Étape 2 : Utiliser le constructeur de convenance pour créer la nouvelle entité Delimitation.
        // JPA sait que 'frontiere' et 'circonscription' sont des entités existantes et ne tentera pas de les recréer.

        // Étape 3 : Sauvegarder la nouvelle entité de relation.
        return toDTO(delimitationRepository.save(new Delimitation(frontiere, circonscription)));
    }

    public List<DelimitationListerDTO> lister() {
        return delimitationRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public void supprimer(int id) {
        if (!delimitationRepository.existsById(id)) {
            throw new EntityNotFoundException("Aucune Délimitation trouvée avec l'ID : " + id);
        }
        delimitationRepository.deleteById(id);
    }

    public DelimitationListerDTO toDTO (Delimitation delimitation) {
        return new  DelimitationListerDTO(
                delimitation.getId(),
                delimitation.getCirconscription().getId(),
                delimitation.getFrontiere().getId(),
                delimitation.getCirconscription().getNom(),
                delimitation.getFrontiere().getType(),
                delimitation.getFrontiere().getLimite()
        );
    }
}
