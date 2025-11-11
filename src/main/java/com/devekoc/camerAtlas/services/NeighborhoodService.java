package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.dto.neighborhood.NeighborhoodCreateDTO;
import com.devekoc.camerAtlas.dto.neighborhood.NeighborhoodListDTO;
import com.devekoc.camerAtlas.entities.Neighborhood;
import com.devekoc.camerAtlas.entities.SubDivision;
import com.devekoc.camerAtlas.mappers.NeighborhoodMapper;
import com.devekoc.camerAtlas.repositories.NeighborhoodRepository;
import com.devekoc.camerAtlas.repositories.SubDivisionRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class NeighborhoodService {
    private final NeighborhoodRepository neighborhoodRepository;
    private final SubDivisionRepository subDivisionRepository;

    public NeighborhoodService(NeighborhoodRepository neighborhoodRepository, SubDivisionRepository subDivisionRepository) {
        this.neighborhoodRepository = neighborhoodRepository;
        this.subDivisionRepository = subDivisionRepository;
    }

    public NeighborhoodListDTO create(NeighborhoodCreateDTO dto) {
        if (neighborhoodRepository.existsByName(dto.name())) {
            throw new DataIntegrityViolationException(
                    "Un quartier avec le name '" + dto.name() + "' existe déjà"
            );
        }

        SubDivision subDivision = subDivisionRepository.findById(dto.subDivisionalOfficeId()).orElseThrow(
                ()-> new EntityNotFoundException("Aucun arrondissement trouvé avec l'ID : " + dto.subDivisionalOfficeId())
        );

        Neighborhood neighborhood = NeighborhoodMapper.fromCreateDTO(dto, new Neighborhood(), subDivision);
        Neighborhood saved = neighborhoodRepository.save(neighborhood);
        log.info("Quartier '{}' (ID : {}) créé avec succès. ", saved.getName(), saved.getId());
        return NeighborhoodMapper.toListDTO(saved);
    }

    public List<NeighborhoodListDTO> createSeveral(List<NeighborhoodCreateDTO> dtos) {
        List<NeighborhoodListDTO> neighborhoods = new ArrayList<>();
        for (NeighborhoodCreateDTO dto : dtos) {
            neighborhoods.add(create(dto));
        }

        return neighborhoods;
    }

    public List<NeighborhoodListDTO> listAll() {
        return neighborhoodRepository.findAll().stream()
                .map(NeighborhoodMapper::toListDTO)
                .toList();
    }

    public NeighborhoodListDTO find(int id) {
        Neighborhood neighborhood = neighborhoodRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Le quartier n'existe pas")
        );
        return NeighborhoodMapper.toListDTO(neighborhood);
    }

    public NeighborhoodListDTO find(String name) {
        Neighborhood neighborhood = neighborhoodRepository.findByName(name).orElseThrow(
                () -> new EntityNotFoundException("Le quartier n'existe pas")
        );
        return NeighborhoodMapper.toListDTO(neighborhood);
    }

    public NeighborhoodListDTO update(int id, NeighborhoodCreateDTO dto) {
        Neighborhood existing = neighborhoodRepository.findById(id).orElseThrow(
                ()-> new EntityNotFoundException("Aucun quartier trouvé avec l'ID : " + id)
        );

        if (neighborhoodRepository.existsByNameAndIdNot(dto.name(), id)) {
            throw new DataIntegrityViolationException(
                    "Un quartier avec le name '" + dto.name() + "' existe déjà"
            );
        }

        SubDivision subDivision = subDivisionRepository.findById(dto.subDivisionalOfficeId()).orElseThrow(
                ()-> new EntityNotFoundException("Aucun arrondissement " + dto.subDivisionalOfficeId())
        );

        NeighborhoodMapper.fromCreateDTO(dto, existing, subDivision);
        Neighborhood updated = neighborhoodRepository.save(existing);
        log.info("Quartier '{}' (ID : {}) mis à jour avec succès. ", updated.getName(), updated.getId());
        return NeighborhoodMapper.toListDTO(updated);
    }

    public void delete(int id) {
        if (!neighborhoodRepository.existsById(id)) {
            throw new EntityNotFoundException("Le quartier n'existe pas");        }
        neighborhoodRepository.deleteById(id);
    }

    @Transactional
    public void delete(String name) {
        if (!neighborhoodRepository.existsByName(name)) {
            throw new EntityNotFoundException("Le quartier n'existe pas");
        }
        neighborhoodRepository.deleteByName(name);
    }
}
