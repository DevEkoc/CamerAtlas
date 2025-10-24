package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.dto.quartier.QuartierCreateDTO;
import com.devekoc.camerAtlas.dto.quartier.QuartierListerDTO;
import com.devekoc.camerAtlas.entities.Quartier;
import com.devekoc.camerAtlas.entities.Arrondissement;
import com.devekoc.camerAtlas.repositories.QuartierRepository;
import com.devekoc.camerAtlas.repositories.ArrondissementRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuartierService {
    private final QuartierRepository quartierRepository;
    private final ArrondissementRepository arrondissementRepository;

    public QuartierService(QuartierRepository quartierRepository, ArrondissementRepository arrondissementRepository) {
        this.quartierRepository = quartierRepository;
        this.arrondissementRepository = arrondissementRepository;
    }

    public QuartierListerDTO creer(QuartierCreateDTO dto) {
        if (quartierRepository.existsByNom(dto.nom())) {
            throw new DataIntegrityViolationException(
                    "Un quartier avec le nom '" + dto.nom() + "' existe déjà"
            );
        }

        Arrondissement arrondissement = arrondissementRepository.findById(dto.idSousPrefecture()).orElseThrow(
                ()-> new EntityNotFoundException("Aucun arrondissement trouvé avec l'ID : " + dto.idSousPrefecture())
        );

        // La copie à la main avec la méthode statique 'fromCreateDTO'
        // est plus performante que 'BeanUtils.copyProperties'
        Quartier saved = quartierRepository.save(Quartier.fromCreateDTO(dto, arrondissement));
        return toDTO(saved);
    }

    public List<QuartierListerDTO> lister() {
        return quartierRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public QuartierListerDTO rechercher(int id) {
        Quartier quartier = quartierRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Le quartier n'existe pas")
        );
        return toDTO(quartier);
    }

    public QuartierListerDTO rechercher(String nom) {
        Quartier quartier = quartierRepository.findByNom(nom).orElseThrow(
                () -> new EntityNotFoundException("Le quartier n'existe pas")
        );
        return toDTO(quartier);
    }

    public QuartierListerDTO modifier(int id, QuartierCreateDTO dto) {
        Quartier existant = quartierRepository.findById(id).orElseThrow(
                ()-> new EntityNotFoundException("Aucun Quartier trouvé avec l'ID : " + id)
        );

        if (quartierRepository.existsByNomAndIdNot(dto.nom(), id)) {
            throw new DataIntegrityViolationException(
                    "Un quartier avec le nom '" + dto.nom() + "' existe déjà"
            );
        }

        Arrondissement arrondissement = arrondissementRepository.findById(dto.idSousPrefecture()).orElseThrow(
                ()-> new EntityNotFoundException("Aucun Arrondissement trouvé avec l'ID : " + dto.idSousPrefecture())
        );

        // Mise à jour des champs
        // La MAJ à la main avec la méthode 'updateFromDTO'
        // est plus performante que 'BeanUtils.copyProperties'
        existant.updateFromDTO(dto, arrondissement);

        return toDTO(quartierRepository.save(existant));
    }

    public void supprimer(int id) {
        if (!quartierRepository.existsById(id)) {
            throw new EntityNotFoundException("Le quartier n'existe pas");        }
        quartierRepository.deleteById(id);
    }

    @Transactional
    // Transactional car deleteByNom n'est pas une méthode CRUD standard
    public void supprimer(String nom) {
        if (!quartierRepository.existsByNom(nom)) {
            throw new EntityNotFoundException("Le quartier n'existe pas");
        }
        quartierRepository.deleteByNom(nom);
    }

    public QuartierListerDTO toDTO (Quartier quartier) {
        return new QuartierListerDTO(
                quartier.getId(),
                quartier.getNom(),
                quartier.getNomPopulaire(),
                quartier.getSousPrefecture().getId(),
                quartier.getSousPrefecture().getNom()
        );
    }
}
