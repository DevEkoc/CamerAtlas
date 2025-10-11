package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.dto.affectation.AffectationCreateDTO;
import com.devekoc.camerAtlas.dto.affectation.AffectationListerDTO;
import com.devekoc.camerAtlas.dto.quartier.QuartierListerDTO;
import com.devekoc.camerAtlas.entities.Affectation;
import com.devekoc.camerAtlas.entities.Autorite;
import com.devekoc.camerAtlas.entities.Circonscription;
import com.devekoc.camerAtlas.entities.Quartier;
import com.devekoc.camerAtlas.exceptions.PositionAlreadyFilledException;
import com.devekoc.camerAtlas.repositories.AffectationRepository;
import com.devekoc.camerAtlas.repositories.AutoriteRepository;
import com.devekoc.camerAtlas.repositories.CirconscriptionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AffectationService {
    private final AffectationRepository affectationRepository;
    private final CirconscriptionRepository circonscriptionRepository;
    private final AutoriteRepository autoriteRepository;

    public AffectationService(AffectationRepository affectationRepository, CirconscriptionRepository circonscriptionRepository, AutoriteRepository autoriteRepository) {
        this.affectationRepository = affectationRepository;
        this.circonscriptionRepository = circonscriptionRepository;
        this.autoriteRepository = autoriteRepository;
    }

    public AffectationListerDTO creer(AffectationCreateDTO dto) {
        Circonscription circonscription = circonscriptionRepository.findById(dto.idCirconscription()).orElseThrow(
                ()-> new EntityNotFoundException("Aucune Circonscription trouvée pour l'ID : " + dto.idCirconscription())
        );
        Autorite autorite = autoriteRepository.findById(dto.idAutorite()).orElseThrow(
                ()-> new EntityNotFoundException("Aucune Autorité trouvée pour l'ID : " + dto.idAutorite())
        );

        circonscriptionIsFree(null, circonscription);
        autoriteIsFree(null, autorite);

        // Si les deux vérifications passent, on sauvegarde l'affectation.
        Affectation saved = affectationRepository.save(Affectation.fromCreateDTO(dto, autorite, circonscription));
        return toDTO(saved);
    }

    public List<AffectationListerDTO> lister() {
        return affectationRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public AffectationListerDTO modifier(int id, AffectationCreateDTO dto) {
        Affectation existante = affectationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Aucune Affectation trouvée avec l'ID : " + id)
        );
        Circonscription circonscription = circonscriptionRepository.findById(dto.idCirconscription()).orElseThrow(
                ()-> new EntityNotFoundException("Aucune Circonscription trouvée pour l'ID : " + dto.idCirconscription())
        );
        Autorite autorite = autoriteRepository.findById(dto.idAutorite()).orElseThrow(
                ()-> new EntityNotFoundException("Aucune Autorité trouvée pour l'ID : " + dto.idAutorite())
        );

        circonscriptionIsFree(null, circonscription);
        autoriteIsFree(null, autorite);

        // Si les deux vérifications passent, on MAJ l'affectation.
        // La MAJ à la main avec la méthode 'updateFromDTO'
        // est plus performante que 'BeanUtils.copyProperties'
        existante.updateFromDTO(dto, autorite, circonscription);
        return toDTO(affectationRepository.save(existante));
    }

    public void supprimer(int id) {
        if (!affectationRepository.existsById(id)) {
            throw new EntityNotFoundException("Aucune Affectation trouvée avec l'ID : " + id);
        }
        affectationRepository.deleteById(id);
    }

    /**
     * Vérifie que la circonscription n'est pas déjà occupée par une affectation active.
     * @param idToExclude ID de l'affectation à exclure lors de la vérification (null pour création, ID pour modification)
     * @param circonscription La circonscription à vérifier
     * @throws PositionAlreadyFilledException si la circonscription est déjà occupée
     */
    public void circonscriptionIsFree (Integer idToExclude, Circonscription circonscription) {
        boolean occupe = (idToExclude == null)
                // idToExclude est null, donc il s'agit d'une création
                ? affectationRepository.existsByCirconscriptionAndDateFinIsNull(circonscription)
                // idToExclude a une valeur, il s'agit d'une modification
                : affectationRepository.existsByCirconscriptionAndDateFinIsNullAndIdNot(circonscription, idToExclude)
                ;

        if (occupe) {
            throw new PositionAlreadyFilledException(
                    "Validation échouée : La circonscription '" + circonscription.getNom()
                            + "' est déjà occupée par une autorité active."
            );
        }
    }

    /**
     * Vérifie que l'autorité n'est pas déjà en poste actif ailleurs.
     * @param idToExclude ID de l'affectation à exclure lors de la vérification (null pour création, ID pour modification)
     * @param autorite L'autorité à vérifier
     * @throws PositionAlreadyFilledException si l'autorité est déjà en poste actif
     */
    public void autoriteIsFree (Integer idToExclude, Autorite autorite) {
        boolean occupe = (idToExclude == null)
                ? affectationRepository.existsByAutoriteAndDateFinIsNull(autorite)
                : affectationRepository.existsByAutoriteAndDateFinIsNullAndIdNot(autorite, idToExclude)
                ;
        // Règle métier 2 : Vérifier si l'autorité est déjà en poste ailleurs.
        if (occupe) {
            throw new PositionAlreadyFilledException(
                    "Validation échouée : L'autorité '" + autorite.getNom() + " " + autorite.getPrenom() + "' est déjà en poste actif ailleurs."
            );
        }
    }

    public AffectationListerDTO toDTO (Affectation affectation) {
        return new AffectationListerDTO(
                affectation.getId(),
                affectation.getAutorite().getId(),
                affectation.getCirconscription().getId(),
                affectation.getFonction(),
                affectation.getDateDebut(),
                affectation.getDateFin()
        );
    }
}
