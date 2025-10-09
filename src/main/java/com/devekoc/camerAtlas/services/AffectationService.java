package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.entities.Affectation;
import com.devekoc.camerAtlas.exceptions.PositionAlreadyFilledException;
import com.devekoc.camerAtlas.repositories.AffectationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AffectationService {
    private final AffectationRepository affectationRepository;

    public AffectationService(AffectationRepository affectationRepository) {
        this.affectationRepository = affectationRepository;
    }

    /**
     * Vérifie que la circonscription n'est pas déjà occupée par une affectation active.
     * @param affectation L'affectation à vérifier
     * @param idToExclude ID à exclure lors de la vérification (null pour création, ID pour modification)
     * @return true si la circonscription est libre
     * @throws PositionAlreadyFilledException si la circonscription est déjà occupée
     */
    public boolean circonscriptionIsFree (Affectation affectation, Integer idToExclude) {
        boolean occupe = (idToExclude == null)
                // idToExclude est null, donc il s'agit d'une création
                ? affectationRepository.existsByCirconscriptionAndDateFinIsNull(affectation.getCirconscription())
                // idToExclude a une valeur, il s'agit d'une modification
                : affectationRepository.existsByCirconscriptionAndDateFinIsNullAndIdNot(affectation.getCirconscription(), idToExclude)
        ;

        if (occupe) {
            throw new PositionAlreadyFilledException(
                    "Validation échouée : La circonscription '" + affectation.getCirconscription().getNom()
                    + "' est déjà occupée par une autorité active."
            );
        }
        return true;
    }

    /**
     * Vérifie que l'autorité n'est pas déjà en poste actif ailleurs.
     * @param affectation L'affectation à vérifier
     * @param idToExclude ID à exclure lors de la vérification (null pour création, ID pour modification)
     * @return true si l'autorité est libre
     * @throws PositionAlreadyFilledException si l'autorité est déjà en poste actif
     */
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
        Affectation existante = affectationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Aucune Affectation trouvée avec l'ID : " + id)
        );
        if (affectation.getId() != id) {
            throw new IllegalArgumentException("L'ID dans l'URL ne correspond pas à l'ID de l'objet !");
        }
        if (circonscriptionIsFree(affectation, id) && autoriteIsFree(affectation, id)) {
            BeanUtils.copyProperties(affectation, existante, "id");
            affectationRepository.save(existante);
        }
    }

    public void supprimer(int id) {
        if (!affectationRepository.existsById(id)) {
            throw new EntityNotFoundException("Aucune Affectation trouvée avec l'ID : " + id);
        }
        affectationRepository.deleteById(id);
    }
}
