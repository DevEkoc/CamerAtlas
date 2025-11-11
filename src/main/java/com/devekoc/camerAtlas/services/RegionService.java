package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.dto.region.RegionCreateDTO;
import com.devekoc.camerAtlas.dto.region.RegionListDTO;
import com.devekoc.camerAtlas.dto.region.RegionListWithDivisionsDTO;
import com.devekoc.camerAtlas.entities.*;
import com.devekoc.camerAtlas.mappers.RegionMapper;
import com.devekoc.camerAtlas.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RegionService {

    private final RegionRepository regionRepository;
    private final MediaService mediaService;
    private final AppointmentRepository appointmentRepository;
    private final DelimitationRepository delimitationRepository;
    private static final String IMAGE_SUBDIRECTORY = "regions";

    public RegionService(RegionRepository regionRepository,
                         MediaService mediaService,
                         AppointmentRepository appointmentRepository,
                         DelimitationRepository delimitationRepository) {
        this.regionRepository = regionRepository;
        this.mediaService = mediaService;
        this.appointmentRepository = appointmentRepository;
        this.delimitationRepository = delimitationRepository;
    }

    /*--------------------------------------------*
     *                 CREATE
     *--------------------------------------------*/

    @Transactional
    public RegionListDTO create(RegionCreateDTO dto) throws IOException {
        validateUniqueName(dto.name());

        String imagePath = mediaService.saveImage(dto.image(), IMAGE_SUBDIRECTORY);
        Region region = RegionMapper.fromCreateDTO(dto, new Region(), imagePath);
        Region saved = regionRepository.save(region);

        log.info("Région '{}' (ID: {}) créée avec succès.", saved.getName(), saved.getId());
        return RegionMapper.toListDTO(saved, Optional.empty(), Collections.emptyList());
    }

    @Transactional
    public List<RegionListDTO> createSeveral(List<RegionCreateDTO> dtos) throws IOException {
        // Ici on réutilise "create" pour chaque DTO
        List<RegionListDTO> result = new ArrayList<>();
        for (RegionCreateDTO dto : dtos) result.add(create(dto));
        return result;
    }

    /*--------------------------------------------*
     *                 READ
     *--------------------------------------------*/

    public List<RegionListDTO> listAll() {
        List<Region> regions = regionRepository.findAll();
        if (regions.isEmpty()) return Collections.emptyList();

        // On crée un dictionnaire qui va associer le 'optional' du RDV actif correspondant à chaque ID de région
        Map<Integer, Optional<Appointment>> appointments = getActiveAppointments(
                regions.stream().map(Region::getId).toList());
        // On crée un dictionnaire qui va associer la liste des frontières correspondant à chaque ID de région
        Map<Integer, List<Delimitation>> delimitations = getDelimitations(
                regions.stream().map(Region::getId).toList());

        // Conversion vers DTOs riches
        return regions
                .stream()
                .map(r -> RegionMapper
                        .toListDTO(
                            r,
                            // récupère l'optional correspondant à la clé (r.getId()), à défaut, un optional vide
                            appointments.getOrDefault(r.getId(), Optional.empty()),
                            // récupère la liste correspondant à la clé (r.getId()), à défaut, une liste vide
                            delimitations.getOrDefault(r.getId(), Collections.emptyList())
                        )
                )
                .toList();
    }

    public RegionListDTO find(int id) {
        Region region = findRegionById(id);
        return mapRegionWithRelations(region);
    }

    public RegionListDTO find(String name) {
        Region region = regionRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Aucune région trouvée avec le nom : " + name));
        return mapRegionWithRelations(region);
    }

    public RegionListWithDivisionsDTO findWithDivisions(int id) {
        Region region = findRegionById(id);

        // Récupération des IDs : région + ses départements
        List<Integer> ids = new ArrayList<>();
        ids.add(region.getId());
        ids.addAll(region.getDivisionsList().stream().map(Division::getId).toList());

        Map<Integer, Optional<Appointment>> appointments = getActiveAppointments(ids);
        Map<Integer, List<Delimitation>> delimitations = getDelimitations(ids);

        return RegionMapper.toRegionWithDivisionsDTO(
                region,
                appointments.getOrDefault(region.getId(), Optional.empty()),
                delimitations.getOrDefault(region.getId(), Collections.emptyList()),
                appointments,
                delimitations
        );
    }

    /*--------------------------------------------*
     *                 UPDATE
     *--------------------------------------------*/

    @Transactional
    public RegionListDTO update(int id, RegionCreateDTO dto) throws IOException {
        Region existing = findRegionById(id);

        if (!existing.getName().equals(dto.name())) {
            validateUniqueName(dto.name());
        }

        String oldImagePath = existing.getImage();
        String newImagePath = handleImageUpdate(dto.image(), oldImagePath);

        // Mise à jour des champs
        RegionMapper.fromCreateDTO(dto, existing, newImagePath);
        Region saved = regionRepository.save(existing);

        // Suppression de l’ancienne image si remplacée
        if (!Objects.equals(newImagePath, oldImagePath)) {
            mediaService.deleteImage(oldImagePath);
        }

        log.info("Région '{}' (ID: {}) mise à jour avec succès.", saved.getName(), id);
        return mapRegionWithRelations(saved);
    }

    /*--------------------------------------------*
     *                 DELETE
     *--------------------------------------------*/

    @Transactional
    public void delete(int id) {
        deleteRegion(findRegionById(id));
    }

    @Transactional
    public void delete(String name) {
        Region existing = regionRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Aucune région trouvée avec le nom : " + name));
        deleteRegion(existing);
    }

    private void deleteRegion(Region region) {
        if (!region.getDivisionsList().isEmpty()) {
            throw new DataIntegrityViolationException("Impossible de supprimer une région contenant des départements.");
        }
        mediaService.deleteImage(region.getImage());
        regionRepository.delete(region);
        log.info("Région '{}' (ID: {}) supprimée avec succès.", region.getName(), region.getId());
    }

    /*--------------------------------------------*
     *         MÉTHODES UTILITAIRES PRIVÉES
     *--------------------------------------------*/

    /** Vérifie qu’un nom de région est unique en base */
    private void validateUniqueName(String name) {
        if (regionRepository.existsByName(name)) {
            throw new DataIntegrityViolationException("Une région avec le nom '" + name + "' existe déjà !");
        }
    }

    /** Charge une région ou lance une exception si absente */
    private Region findRegionById(int id) {
        return regionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aucune région trouvée avec l'ID : " + id));
    }

    /** Récupère les appointments actifs (gouverneurs) pour une liste d’IDs de circonscriptions */
    private Map<Integer, Optional<Appointment>> getActiveAppointments(List<Integer> ids) {
        // Ici, on récupère l'affectation qui correspond à l'id de circonscription (ici, l'id de région)
        // ça nous donne une liste d'affectations et pour chacune, on associe (Map) l'id de circonscription (a.getCirconscription().getId())
        // à l'optional de l'affectation. S'il y a doublon (de clé), on conserve la première
        return appointmentRepository.findByCirconscription_IdInAndEndDateIsNull(ids)
                .stream()
                .collect(Collectors.toMap(
                        a -> a.getCirconscription().getId(), // la clé est l'id de la circonscription.
                        Optional::of, // la valeur est l'optional de l'affectation.
                        (a, b) -> a // Gestion des collisions : si la BDD renvoie 2 affectations
                        // actives (données incohérentes), on garde la première trouvée.
                ));
    }

    /** Récupère toutes les délimitations liées à une liste d’IDs de circonscriptions */
    // Ici, on récupère dans une liste les frontières associées à chaque id de région contenu dans la liste passée en paramètre
    // Puis on regroupe dans une liste les frontières ayant un d.getCirconscription().getId() en commun
    // Et on associe (Map) enfin chaque id de circonscription à la liste de frontières correspondante
    private Map<Integer, List<Delimitation>> getDelimitations(List<Integer> ids) {
        return delimitationRepository.findByCirconscription_IdIn(ids)
                .stream()
                .collect(Collectors.groupingBy(d -> d.getCirconscription().getId()));
    }

    /** Met à jour l’image si nécessaire et renvoie le nouveau chemin */
    private String handleImageUpdate(MultipartFile newImage, String oldPath) throws IOException {
        if (newImage != null && !newImage.isEmpty()) {
            return mediaService.saveImage(newImage, IMAGE_SUBDIRECTORY);
        }
        return oldPath; // garde l’ancienne si aucune nouvelle image
    }

    /** Construit un DTO complet pour une région (avec gouverneur + frontières) */
    private RegionListDTO mapRegionWithRelations(Region region) {
        int id = region.getId();
        Optional<Appointment> appointment = appointmentRepository.findByCirconscription_IdInAndEndDateIsNull(List.of(id))
                .stream().findFirst();
        List<Delimitation> delimitations = delimitationRepository.findByCirconscription_IdIn(List.of(id));
        return RegionMapper.toListDTO(region, appointment, delimitations);
    }
}
