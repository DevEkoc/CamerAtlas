package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.dto.arrondissement.ArrondissementCreateDTO;
import com.devekoc.camerAtlas.dto.arrondissement.ArrondissementListerDTO;
import com.devekoc.camerAtlas.entities.Arrondissement;
import com.devekoc.camerAtlas.entities.Departement;
import com.devekoc.camerAtlas.repositories.ArrondissementRepository;
import com.devekoc.camerAtlas.repositories.DepartementRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArrondissementService {
    private final ArrondissementRepository arrondissementRepository;
    private final DepartementRepository departementRepository;

    public ArrondissementService(ArrondissementRepository arrondissementRepository, DepartementRepository departementRepository) {
        this.arrondissementRepository = arrondissementRepository;
        this.departementRepository = departementRepository;
    }

    public ArrondissementListerDTO creer(ArrondissementCreateDTO dto) {
        if (arrondissementRepository.existsByNom(dto.nom())) {
            throw new DataIntegrityViolationException(
                    "Un arrondissement avec le nom '" + dto.nom() + "' existe déjà"
            );
        }

        Departement departement = departementRepository.findById(dto.idDepartement()).orElseThrow(
                ()-> new EntityNotFoundException("Aucun département trouvé avec l'ID : " + dto.idDepartement())
        );

        // La copie à la main avec la méthode statique 'fromCreateDTO'
        // est plus performante que 'BeanUtils.copyProperties'
        Arrondissement saved = arrondissementRepository.save(Arrondissement.fromCreateDTO(dto, departement));
        return toDTO(saved);
    }

    public List<ArrondissementListerDTO> lister() {
        return arrondissementRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public ArrondissementListerDTO rechercher(int id) {
        Arrondissement arrondissement = arrondissementRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("L'arrondissement n'existe pas")
        );
        return toDTO(arrondissement);
    }

    public ArrondissementListerDTO rechercher(String nom) {
        Arrondissement arrondissement = arrondissementRepository.findByNom(nom).orElseThrow(
                () -> new EntityNotFoundException("L'arrondissement n'existe pas")
        );
        return toDTO(arrondissement);
    }

    public ArrondissementListerDTO modifier(int id, ArrondissementCreateDTO dto) {
        Arrondissement existant = arrondissementRepository.findById(id).orElseThrow(
                ()-> new EntityNotFoundException("Aucun Arrondissement trouvé avec l'ID : " + id)
        );

        if (arrondissementRepository.existsByNomAndIdNot(dto.nom(), id)) {
            throw new DataIntegrityViolationException(
                    "Un département avec le nom '" + dto.nom() + "' existe déjà"
            );
        }

        Departement departement = departementRepository.findById(dto.idDepartement()).orElseThrow(
                ()-> new EntityNotFoundException("Aucun Département trouvé avec l'ID : " + id)
        );

        // La copie à la main avec la méthode 'updateFromDTO'
        // est plus performante que 'BeanUtils.copyProperties'
        existant.updateFromDTO(dto, departement);
        return toDTO(arrondissementRepository.save(existant));
    }

    public void supprimer(int id) {
        Arrondissement existant = arrondissementRepository.findById(id).orElseThrow(
                ()-> new EntityNotFoundException("L'arrondissement n'existe pas")
        );

        if (!existant.getListeQuartiers().isEmpty()) {
            throw new DataIntegrityViolationException("Impossible de supprimer un arrondissement contenant des quartiers");
        }
        arrondissementRepository.deleteById(id);
    }

    @Transactional
    // Transactional car deleteByNom n'est pas une méthode CRUD standart
    public void supprimer(String nom) {
        Arrondissement existant = arrondissementRepository.findByNom(nom).orElseThrow(
                ()-> new EntityNotFoundException("L'arrondissement n'existe pas")
        );

        if (!existant.getListeQuartiers().isEmpty()) {
            throw new DataIntegrityViolationException("Impossible de supprimer un arrondissement contenant des quartiers");
        }
        arrondissementRepository.deleteByNom(nom);
    }

    public ArrondissementListerDTO toDTO (Arrondissement arrondissement) {
        return new ArrondissementListerDTO(
                arrondissement.getId(),
                arrondissement.getNom(),
                arrondissement.getSuperficie(),
                arrondissement.getPopulation(),
                arrondissement.getCoordonnees(),
                arrondissement.getSousPrefecture(),
                arrondissement.getDepartement().getId(),
                arrondissement.getDepartement().getNom()
        );
    }
}
