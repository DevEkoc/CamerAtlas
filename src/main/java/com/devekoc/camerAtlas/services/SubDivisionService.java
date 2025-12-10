package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.dto.subDivision.SubDivisionCreateDTO;
import com.devekoc.camerAtlas.dto.subDivision.SubDivisionListDTO;
import com.devekoc.camerAtlas.dto.subDivision.SubDivisionWithNeighborhoodsDTO;
import com.devekoc.camerAtlas.entities.*;
import com.devekoc.camerAtlas.mappers.SubDivisionMapper;
import com.devekoc.camerAtlas.repositories.AppointmentRepository;
import com.devekoc.camerAtlas.repositories.DelimitationRepository;
import com.devekoc.camerAtlas.repositories.SubDivisionRepository;
import com.devekoc.camerAtlas.repositories.DivisionRepository;
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
public class SubDivisionService {
    private final SubDivisionRepository subDivisionRepository;
    private final DivisionRepository divisionRepository;
    private final MediaService mediaService;
    private final AppointmentRepository appointmentRepository;
    private final DelimitationRepository delimitationRepository;
    private static final String IMAGE_SUBDIRECTORY = "subDivisions";

    public SubDivisionListDTO create(SubDivisionCreateDTO dto) throws IOException {
        validateUniqueName(dto.name());
        Division division = getDivisionOrThrow(dto.divisionId());
        String imagePath = mediaService.saveImage(dto.image(), IMAGE_SUBDIRECTORY);
        SubDivision subDivision = SubDivisionMapper.fromCreateDTO(dto, new SubDivision(), division, imagePath);
        SubDivision saved = subDivisionRepository.save(subDivision);
        log.info("Arrondissement '{}' (ID: {}) créé avec succès.", saved.getName(), saved.getId());
        return SubDivisionMapper.toListDTO(saved, Collections.emptyMap(), Collections.emptyMap());
    }

    public List<SubDivisionListDTO> createSeveral(List<SubDivisionCreateDTO> dtos) throws IOException {
        List<SubDivisionListDTO> subDivisions = new ArrayList<>();
        for (SubDivisionCreateDTO dto : dtos) {
            subDivisions.add(create(dto));
        }
        return subDivisions;
    }

    public List<SubDivisionListDTO> listAll() {
        List<SubDivision> subDivisions = subDivisionRepository.findAll();
        if (subDivisions.isEmpty()) return Collections.emptyList();

        List<Integer> ids = subDivisions.stream().map(SubDivision::getId).toList();
        Map<Integer, Optional<Appointment>> appointments = getActiveAppointments(ids);
        Map<Integer, List<Delimitation>> delimitations = getDelimitations(ids);

        return subDivisions.stream()
                .map(s -> SubDivisionMapper.toListDTO(s, appointments, delimitations))
                .toList()
        ;
    }

    public SubDivisionListDTO find(int id) {
        SubDivision subDivision = findBySubDivisionId(id);
        return mapSingleSubDivision(subDivision);
    }

    public SubDivisionListDTO find(String name) {
        SubDivision subDivision = subDivisionRepository.findByName(name).orElseThrow(
                () -> new EntityNotFoundException("Aucun Arrondissement trouvé avec le name : " + name)
        );
        return mapSingleSubDivision(subDivision);
    }

    public SubDivisionWithNeighborhoodsDTO findWithNeighborhoods(int id) {
        SubDivision subDivision = findBySubDivisionId(id);
        List<Integer> ids = List.of(subDivision.getId());

        Map<Integer, Optional<Appointment>> appointments = getActiveAppointments(ids);
        Map<Integer, List<Delimitation>> delimitations = getDelimitations(ids);

        return SubDivisionMapper.toSubDivisionWithNeighborhoods(subDivision, appointments, delimitations);
    }

    public SubDivisionListDTO update(int id, SubDivisionCreateDTO dto) throws IOException {
        SubDivision existing = findBySubDivisionId(id);
        if (!existing.getName().equals(dto.name())) {
            validateUniqueName(dto.name());
        }
        Division division = getDivisionOrThrow(dto.divisionId());
        String oldImagePath = existing.getImage();
        String newImagePath = handleImageUpdate(dto.image(), oldImagePath);

        SubDivisionMapper.fromCreateDTO(dto, existing, division, newImagePath);
        SubDivision updated = subDivisionRepository.save(existing);
        // Si l’image a été remplacée, supprimer l’ancienne
        if (!Objects.equals(newImagePath, oldImagePath)) {
            mediaService.deleteImage(oldImagePath);
        }

        // On log la MAJ
        log.info("Arrondissement '{}' (ID: {}) mis à jour avec succès.", updated.getName(), id);
        // On retourne l'arrondissement modifié et transformé en DTO
        return mapSingleSubDivision(updated);
    }

    @Transactional
    public void delete(int id) {
        deleteSubDivision(findBySubDivisionId(id));
    }

    @Transactional
    public void delete(String name) {
        SubDivision existing = subDivisionRepository.findByName(name).orElseThrow(
                ()-> new EntityNotFoundException("L'arrondissement n'existe pas")
        );
        deleteSubDivision(existing);
    }

    private void deleteSubDivision(SubDivision subDivision) {
        if (!subDivision.getNeighbourhoodsList().isEmpty()) {
            throw new DataIntegrityViolationException("Impossible de supprimer un arrondissement contenant des quartiers");
        }
        mediaService.deleteImage(subDivision.getImage());
        subDivisionRepository.delete(subDivision);
        log.info("Arrondissement '{}' (ID: {}) supprimé avec succès.", subDivision.getName(), subDivision.getId());
    }

    private Division getDivisionOrThrow(int id) {
        return divisionRepository.findById(id).orElseThrow(
                ()-> new EntityNotFoundException("Aucun département trouvé avec l'ID : " + id)
        );
    }

    private void validateUniqueName(String name) {
        if (subDivisionRepository.existsByName(name)) {
            throw new DataIntegrityViolationException(
                    "Un arrondissement avec le name '" + name + "' existe déjà"
            );
        }
    }

    private Map<Integer, Optional<Appointment>> getActiveAppointments(List<Integer> ids) {
        return appointmentRepository.findByCirconscription_IdInAndEndDateIsNull(ids).stream()
                .collect(Collectors.toMap(
                        app -> app.getCirconscription().getId(), // la clé est l'id de l'affectation.
                        Optional::of, // la valeur est l'affectation elle-même
                        (app, replacement) -> app
                ));
    }

    // Charge les délimitations frontalières
    private Map<Integer, List<Delimitation>> getDelimitations(List<Integer> ids) {
        return delimitationRepository.findByCirconscription_IdIn(ids).stream()
                .collect(Collectors.groupingBy(del -> del.getCirconscription().getId()));
    }

    private SubDivision findBySubDivisionId(int id) {
        return subDivisionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aucun département trouvé avec l'ID " + id));
    }

    private SubDivisionListDTO mapSingleSubDivision (SubDivision subDivision) {
        int id = subDivision.getId();
        Map<Integer, Optional<Appointment>> appointments = getActiveAppointments(List.of(id));
        Map<Integer, List<Delimitation>> delimitations = getDelimitations(List.of(id));
        return SubDivisionMapper.toListDTO(subDivision, appointments, delimitations);
    }

    /** Met à jour l’image si nécessaire et renvoie le nouveau chemin */
    private String handleImageUpdate(MultipartFile newImage, String oldPath) throws IOException {
        if (newImage != null && !newImage.isEmpty()) {
            return mediaService.saveImage(newImage, IMAGE_SUBDIRECTORY);
        }
        return oldPath; // garde l’ancienne si aucune nouvelle image
    }
}
