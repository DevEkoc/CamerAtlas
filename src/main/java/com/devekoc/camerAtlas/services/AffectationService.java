package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.entities.Affectation;
import com.devekoc.camerAtlas.exceptions.PositionAlreadyFilledException;
import com.devekoc.camerAtlas.repositories.AffectationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AffectationService {
    private final AffectationRepository affectationRepository;

    public AffectationService(AffectationRepository affectationRepository) {
        this.affectationRepository = affectationRepository;
    }

    public boolean circonscriptionIsFree (Affectation affectation, Integer idToExclude) {
        boolean occupe = (idToExclude == null)
                // idToExclude est null, donc il s'agit d'une création
                ? affectationRepository.existsByCirconscriptionAndDateFinIsNull(affectation.getCirconscription())
                // idToExclude a une valeur, il s'agit d'une modification
                : affectationRepository.existsByCirconscriptionAndDateFinIsNullAndIdNot(affectation.getCirconscription(), idToExclude)
        ;

        if (occupe) {
            throw new PositionAlreadyFilledException(
                    "Validation échouée : La circonscription '" + affectation.getCirconscription().getNom() + "' est déjà occupée par une autorité active."
            );
        }
        return true;
    }

    public boolean autoriteIsFree (Affectation affectation, Integer idToExclude) {
        boolean occupe = (idToExclude == null)
                ? affectationRepository.existsByAutoriteAndDateFinIsNull(affectation.getAutorite())
                : affectationRepository.existsByAutoriteAndDateFinIsNullAndIdNot(affectation.getAutorite(), idToExclude)
        ;
        // Règle métier 2: Vérifier si l'autorité est déjà en poste ailleurs.
        if (occupe) {
            throw new PositionAlreadyFilledException(
                    "Validation échouée : L'autorité '" + affectation.getAutorite().getNom() + " " + affectation.getAutorite().getPrenom() + "' est déjà en poste actif ailleurs."
            );
        }
        return true;
    }

    public void creer(Affectation affectation) {
        if (circonscriptionIsFree(affectation, null) && autoriteIsFree(affectation, null)) {
            // Si les deux vérifications passent, on sauvegarde l'affectation.
            affectationRepository.save(affectation);
        }
    }

    public List<Affectation> lister() {
        return affectationRepository.findAll();
    }

    public void modifier(int id, Affectation affectation) {
        Affectation ancienneAffectation = affectationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Affectation non trouvée !")
        );
        if (affectation.getId() != id) {
            throw new IllegalArgumentException("L'ID dans l'URL ne correspond pas à l'ID de l'objet !");
        }
        if (circonscriptionIsFree(affectation, id) && autoriteIsFree(affectation, id)) {
            ancienneAffectation.setAutorite(affectation.getAutorite());
            ancienneAffectation.setCirconscription(affectation.getCirconscription());
            ancienneAffectation.setFonction(affectation.getFonction());
            ancienneAffectation.setDateDebut(affectation.getDateDebut());
            ancienneAffectation.setDateFin(affectation.getDateFin());
            affectationRepository.save(ancienneAffectation);
        }
    }

    public void supprimer(int id) {
        affectationRepository.deleteById(id);
    }
}
