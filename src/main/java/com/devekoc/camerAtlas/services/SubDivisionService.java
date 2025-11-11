//package com.devekoc.camerAtlas.services;
//
//import com.devekoc.camerAtlas.dto.subDivision.SubDivisionCreateDTO;
//import com.devekoc.camerAtlas.dto.subDivision.SubDivisionListDTO;
//import com.devekoc.camerAtlas.entities.Division;
//import com.devekoc.camerAtlas.entities.SubDivision;
//import com.devekoc.camerAtlas.mappers.SubDivisionMapper;
//import com.devekoc.camerAtlas.repositories.SubDivisionRepository;
//import com.devekoc.camerAtlas.repositories.DivisionRepository;
//import jakarta.persistence.EntityNotFoundException;
//import jakarta.transaction.Transactional;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//@Slf4j
//@Service
//public class SubDivisionService {
//    private final SubDivisionRepository subDivisionRepository;
//    private final DivisionRepository divisionRepository;
//    private final MediaService mediaService;
//    private static final String IMAGE_SUBDIRECTORY = "subDivisions";
//
//    public SubDivisionService(SubDivisionRepository subDivisionRepository, DivisionRepository divisionRepository, MediaService mediaService) {
//        this.subDivisionRepository = subDivisionRepository;
//        this.divisionRepository = divisionRepository;
//        this.mediaService = mediaService;
//    }
//
//    public SubDivisionListDTO create(SubDivisionCreateDTO dto) throws IOException {
//        if (subDivisionRepository.existsByName(dto.name())) {
//            throw new DataIntegrityViolationException(
//                    "Un arrondissement avec le name '" + dto.name() + "' existe déjà"
//            );
//        }
//
//        Division division = divisionRepository.findById(dto.divisionId()).orElseThrow(
//                ()-> new EntityNotFoundException("Aucun département trouvé avec l'ID : " + dto.divisionId())
//        );
//
//        String imagePath = mediaService.saveImage(dto.image(), IMAGE_SUBDIRECTORY);
//        SubDivision subDivision = SubDivisionMapper.fromCreateDTO(dto, new SubDivision(), division, imagePath);
//        SubDivision saved = subDivisionRepository.save(subDivision);
//        log.info("Arrondissement '{}' (ID: {}) créé avec succès.", saved.getName(), saved.getId());
//
//        return SubDivisionMapper.toListDTO(saved);
//    }
//
//    public List<SubDivisionListDTO> createSeveral(List<SubDivisionCreateDTO> dtos) throws IOException {
//        List<SubDivisionListDTO> subDivisions = new ArrayList<>();
//        for (SubDivisionCreateDTO dto : dtos) {
//            subDivisions.add(create(dto));
//        }
//
//        return subDivisions;
//    }
//
//    public List<SubDivisionListDTO> listAll() {
//        return subDivisionRepository.findAll().stream()
//                .map(SubDivisionMapper::toListDTO)
//                .toList();
//    }
//
//    public SubDivisionListDTO find(int id) {
//        SubDivision subDivision = subDivisionRepository.findById(id).orElseThrow(
//                () -> new EntityNotFoundException("L'arrondissement n'existe pas")
//        );
//        return SubDivisionMapper.toListDTO(subDivision);
//    }
//
//    public SubDivisionListDTO find(String name) {
//        SubDivision subDivision = subDivisionRepository.findByName(name).orElseThrow(
//                () -> new EntityNotFoundException("L'arrondissement n'existe pas")
//        );
//        return SubDivisionMapper.toListDTO(subDivision);
//    }
//
//    public SubDivisionListDTO update(int id, SubDivisionCreateDTO dto) throws IOException {
//        // On récupère l'arrondissement à modifier (grâce à l'id fourni dans l'URL), à défaut, on lève une exception.
//        SubDivision existing = subDivisionRepository.findById(id).orElseThrow(
//                ()-> new EntityNotFoundException("Aucun Arrondissement trouvé avec l'ID : " + id)
//        );
//
//        // On se rassure que le nouveau name n'entre pas en conflit avec ceux déjà en BD, le cas échéant, on lève une exception.
//        if (subDivisionRepository.existsByNameAndIdNot(dto.name(), id)) {
//            throw new DataIntegrityViolationException(
//                    "Un Arrondissement avec le name '" + dto.name() + "' existe déjà"
//            );
//        }
//
//        // On récupère le département correspondant à l'ID de département fourni dans le DTO, le cas échéant, on lève une exception.
//        Division division = divisionRepository.findById(dto.divisionId()).orElseThrow(
//                ()-> new EntityNotFoundException("Aucune région trouvée avec l'ID : " + dto.divisionId())
//        );
//
//        // On récupère le chemin actuel de l'image
//        String oldImagePath = existing.getImage();
//        // Par défaut, le nouveau chemin d'image = ancien chemin
//        String newImagePath = oldImagePath;
//
//        // On récupère l'image passée dans le DTO et on vérifie qu'elle n'est pas nulle
//        // Si elle n'est pas nulle, on l'enregistre (saveImage()) et on MAJ le nouveau chemin d'image.
//        // Si elle est nulle, le nouveau chemin d'image reste = ancien chemin
//        MultipartFile newImageFile = dto.image();
//        if (newImageFile != null && !newImageFile.isEmpty()) {
//            newImagePath = mediaService.saveImage(newImageFile, IMAGE_SUBDIRECTORY);
//        }
//
//        // On MAJ l'objet SubDivision courant
//        SubDivisionMapper.fromCreateDTO(dto, existing, division, newImagePath);
//        // Et on l'enregistre en BD
//        SubDivision updated = subDivisionRepository.save(existing);
//
//        // Si l'ancien chemin d'image != du nouveau (ça signifie que l'image a changé), alors on supprime l'ancienne image.
//        if (newImagePath != null && !newImagePath.equals(oldImagePath)) {
//            mediaService.deleteImage(oldImagePath);
//        }
//
//        // On log la MAJ
//        log.info("Arrondissement '{}' (ID: {}) mis à jour avec succès.", updated.getName(), id);
//        // On retourne l'arrondissement modifié et transformé en DTO
//        return SubDivisionMapper.toListDTO(updated);
//    }
//
//    public void delete(int id) {
//        SubDivision existing = subDivisionRepository.findById(id).orElseThrow(
//                ()-> new EntityNotFoundException("L'arrondissement n'existe pas")
//        );
//        deleteSubDivision(existing);
//    }
//
//    @Transactional
//    public void delete(String name) {
//        SubDivision existing = subDivisionRepository.findByName(name).orElseThrow(
//                ()-> new EntityNotFoundException("L'arrondissement n'existe pas")
//        );
//        deleteSubDivision(existing);
//    }
//
//    private void deleteSubDivision(SubDivision subDivision) {
//        if (!subDivision.getNeighbourhoodsList().isEmpty()) {
//            throw new DataIntegrityViolationException("Impossible de supprimer un arrondissement contenant des quartiers");
//        }
//        mediaService.deleteImage(subDivision.getImage());
//        subDivisionRepository.delete(subDivision);
//        log.info("Arrondissement '{}' (ID: {}) supprimé avec succès.", subDivision.getName(), subDivision.getId());
//    }
//
//}
