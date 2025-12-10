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
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class NeighborhoodService {
    private final NeighborhoodRepository neighborhoodRepository;
    private final SubDivisionRepository subDivisionRepository;

    public NeighborhoodListDTO create(NeighborhoodCreateDTO dto) {
        validateUniqueName(dto.name());
        SubDivision subDivision = getSubDivisionOrThrow(dto.subDivisionalOfficeId());
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
        Neighborhood neighborhood = findByNeighborhoodId(id);
        return NeighborhoodMapper.toListDTO(neighborhood);
    }

    public NeighborhoodListDTO find(String name) {
        Neighborhood neighborhood = neighborhoodRepository.findByName(name).orElseThrow(
                () -> new EntityNotFoundException("Aucun quartier trouvé avec le name " + name)
        );
        return NeighborhoodMapper.toListDTO(neighborhood);
    }

    public NeighborhoodListDTO update(int id, NeighborhoodCreateDTO dto) {
        Neighborhood existing = findByNeighborhoodId(id);
        if (!existing.getName().equals(dto.name())) {
            validateUniqueName(dto.name());
        }
        SubDivision subDivision = getSubDivisionOrThrow(dto.subDivisionalOfficeId());

        NeighborhoodMapper.fromCreateDTO(dto, existing, subDivision);
        Neighborhood updated = neighborhoodRepository.save(existing);
        log.info("Quartier '{}' (ID : {}) mis à jour avec succès. ", updated.getName(), updated.getId());
        return NeighborhoodMapper.toListDTO(updated);
    }

    @Transactional
    public void delete(int id) {
        deleteNeighborhood(findByNeighborhoodId(id));
    }

    @Transactional
    public void delete(String name) {
        Neighborhood neighborhood = neighborhoodRepository.findByName(name).orElseThrow(
                ()-> new EntityNotFoundException("Aucun quartier trouvé avec le name " + name)
        );
        deleteNeighborhood(neighborhood);
    }
    private void validateUniqueName (String name) {
        if (neighborhoodRepository.existsByName(name)) {
            throw new DataIntegrityViolationException("Un quartier nommé '" + name + "' existe déjà");
        }
    }

    private SubDivision getSubDivisionOrThrow(int id) {
        return subDivisionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aucun arrondissement trouvé avec l'ID : " + id));
    }

    private Neighborhood findByNeighborhoodId(int id) {
        return neighborhoodRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aucun quartier trouvé avec l'ID : " + id));
    }

    private void deleteNeighborhood (Neighborhood neighborhood) {
        neighborhoodRepository.delete(neighborhood);
        log.info("Quartier '{}' (ID : {}) supprimé avec succès. ", neighborhood.getName(), neighborhood.getId());
    }
}
