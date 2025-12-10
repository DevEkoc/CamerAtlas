package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.dto.division.DivisionCreateDTO;
import com.devekoc.camerAtlas.dto.division.DivisionListDTO;
import com.devekoc.camerAtlas.dto.division.DivisionWithSubDivisionsDTO;
import com.devekoc.camerAtlas.entities.*;
import com.devekoc.camerAtlas.mappers.DivisionMapper;
import com.devekoc.camerAtlas.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class DivisionService {

    private static final String IMAGE_SUBDIRECTORY = "divisions";

    private final DivisionRepository divisionRepository;
    private final RegionRepository regionRepository;
    private final MediaService mediaService;
    private final AppointmentRepository appointmentRepository;
    private final DelimitationRepository delimitationRepository;

    /* ===================== CRÉATION ===================== */

    public DivisionListDTO create(DivisionCreateDTO dto) throws IOException {
        validateUniqueName(dto.name());
        Region region = getRegionOrThrow(dto.regionId());

        String imagePath = mediaService.saveImage(dto.image(), IMAGE_SUBDIRECTORY);

        Division division = DivisionMapper.fromCreateDTO(dto, new Division(), region, imagePath);
        Division saved = divisionRepository.save(division);

        log.info("Département '{}' (ID: {}) créé avec succès", saved.getName(), saved.getId());

        return DivisionMapper.toListDTO(saved, Collections.emptyMap(), Collections.emptyMap());
    }

    public List<DivisionListDTO> createSeveral(List<DivisionCreateDTO> dtos) throws IOException {
        List<DivisionListDTO> divisions = new ArrayList<>();
        for (DivisionCreateDTO dto : dtos) divisions.add(create(dto));
        return divisions;
    }

    /* ===================== LECTURE ===================== */

    public List<DivisionListDTO> listAll() {
        List<Division> divisions = divisionRepository.findAll();
        if (divisions.isEmpty()) return Collections.emptyList();

        List<Integer> ids = divisions.stream().map(Division::getId).toList();

        Map<Integer, Optional<Appointment>> appointments = getActiveAppointments(ids);
        Map<Integer, List<Delimitation>> delimitations = getDelimitations(ids);

        return divisions.stream()
                .map(d -> DivisionMapper.toListDTO(d, appointments, delimitations))
                .toList();
    }


    public DivisionListDTO find(int id) {
        Division division = findByDivisionId(id);
        return mapSingleDivision(division);
    }

    public DivisionListDTO find(String name) {
        Division division = divisionRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Le département n'existe pas"));
        return mapSingleDivision(division);
    }

    public DivisionWithSubDivisionsDTO findWithSubDivisions(int id) {
        Division division = findByDivisionId(id);

        List<Integer> ids = new ArrayList<>();
        ids.add(division.getId());
        ids.addAll(division.getSubDivisionsList().stream().map(SubDivision::getId).toList());

        Map<Integer, Optional<Appointment>> appointments = getActiveAppointments(ids);
        Map<Integer, List<Delimitation>> delimitations = getDelimitations(ids);

        return DivisionMapper.toDivisionWithSubDivisions (
            division,
            appointments.getOrDefault(division.getId(), Optional.empty()),
            delimitations.getOrDefault(division.getId(), Collections.emptyList()),
            appointments,
            delimitations
        );
    }

    /* ===================== MISE À JOUR ===================== */

    public DivisionListDTO update(int id, DivisionCreateDTO dto) throws IOException {
        Division existing = findByDivisionId(id);

        if (!existing.getName().equals(dto.name())) {
            validateUniqueName(dto.name());
        }
        Region region = getRegionOrThrow(dto.regionId());

        String oldImagePath = existing.getImage();
        String newImagePath = handleImageUpdate(dto.image(), oldImagePath);

        DivisionMapper.fromCreateDTO(dto, existing, region, newImagePath);
        Division updated = divisionRepository.save(existing);

        // Si l’image a été remplacée, supprimer l’ancienne
        if (!Objects.equals(newImagePath, oldImagePath)) {
            mediaService.deleteImage(oldImagePath);
        }

        log.info("Département '{}' (ID: {}) mis à jour avec succès", updated.getName(), id);
        return mapSingleDivision(updated);
    }

    /* ===================== SUPPRESSION ===================== */

    @Transactional
    public void delete(int id) {
        deleteDivision(findByDivisionId(id));
    }

    @Transactional
    public void delete(String name) {
        Division division = divisionRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Aucun département trouvé avec le name " + name));
        deleteDivision(division);
    }

    private void deleteDivision(Division division) {
        // Vérifie que le département ne contient pas d’arrondissements
        if (!division.getSubDivisionsList().isEmpty()) {
            throw new DataIntegrityViolationException("Impossible de supprimer un département contenant des arrondissements");
        }
        mediaService.deleteImage(division.getImage());
        divisionRepository.delete(division);
        log.info("Département '{}' (ID: {}) supprimé avec succès", division.getName(), division.getId());
    }

    /* ===================== MÉTHODES UTILITAIRES ===================== */

    // Charge les autorités actives (gouverneurs / préfets / etc.)
    private Map<Integer, Optional<Appointment>> getActiveAppointments(List<Integer> ids) {
        return appointmentRepository.findByCirconscription_IdInAndEndDateIsNull(ids).stream()
                .collect(Collectors.toMap(
                        app -> app.getCirconscription().getId(), // la clé est l'id de l'affectation
                        Optional::of, // la valeur est l'affectation elle-même
                        (app, replacement) -> app
                ));
    }

    // Charge les délimitations frontalières
    private Map<Integer, List<Delimitation>> getDelimitations(List<Integer> ids) {
        return delimitationRepository.findByCirconscription_IdIn(ids).stream()
                .collect(Collectors.groupingBy(del -> del.getCirconscription().getId()));
    }

    // Raccourci pour récupérer une région ou lancer une exception claire
    private Region getRegionOrThrow(int id) {
        return regionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aucune région trouvée avec l'ID : " + id));
    }

    // Permet d’obtenir un seul département enrichi de ses relations (évite la duplication dans find(id) et find(name))
    private DivisionListDTO mapSingleDivision(Division division) {
        int id = division.getId();
        Map<Integer, Optional<Appointment>> appointments = getActiveAppointments(List.of(id));
        Map<Integer, List<Delimitation>> delimitations = getDelimitations(List.of(id));
        return DivisionMapper.toListDTO(division, appointments, delimitations);
    }

    private void validateUniqueName (String name) {
        if (divisionRepository.existsByName(name)) {
            throw new DataIntegrityViolationException("Un département nommé '" + name + "' existe déjà");
        }
    }

    /** Charge un département ou lance une exception si absente */
    private Division findByDivisionId(int id) {
        return divisionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Le département n'existe pas"));
    }

    /** Met à jour l’image si nécessaire et renvoie le nouveau chemin */
    private String handleImageUpdate(MultipartFile newImage, String oldPath) throws IOException {
        if (newImage != null && !newImage.isEmpty()) {
            return mediaService.saveImage(newImage, IMAGE_SUBDIRECTORY);
        }
        return oldPath; // garde l’ancienne si aucune nouvelle image
    }
}
