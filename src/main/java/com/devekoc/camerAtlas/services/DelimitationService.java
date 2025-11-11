package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.dto.delimitation.DelimitationCreateDTO;
import com.devekoc.camerAtlas.dto.delimitation.DelimitationListDTO;
import com.devekoc.camerAtlas.entities.Circonscription;
import com.devekoc.camerAtlas.entities.Delimitation;
import com.devekoc.camerAtlas.mappers.DelimitationMapper;
import com.devekoc.camerAtlas.repositories.CirconscriptionRepository;
import com.devekoc.camerAtlas.repositories.DelimitationRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DelimitationService {
    private final DelimitationRepository delimitationRepository;
    private final CirconscriptionRepository circonscriptionRepository;

    public DelimitationService(DelimitationRepository delimitationRepository, CirconscriptionRepository circonscriptionRepository) {
        this.delimitationRepository = delimitationRepository;
        this.circonscriptionRepository = circonscriptionRepository;
    }

    public DelimitationListDTO create(DelimitationCreateDTO dto) {
        Circonscription circonscription = circonscriptionRepository.findById(dto.circonscriptionId())
                .orElseThrow(() -> new EntityNotFoundException("Aucune Circonscription trouvée avec l'ID : " + dto.circonscriptionId()));

        Delimitation delimitation = DelimitationMapper.fromCreateDTO(dto, new Delimitation(), circonscription);
        Delimitation saved = delimitationRepository.save(delimitation);
        return DelimitationMapper.toListDTO(saved);
    }

    public List<DelimitationListDTO> createSeveral(@Valid List<DelimitationCreateDTO> dtos) {
        List<DelimitationListDTO> delimitations = new ArrayList<>();
        for (DelimitationCreateDTO dto : dtos) {
            delimitations.add(create(dto));
        }

        return delimitations;
    }

    public List<DelimitationListDTO> listAll() {
        return delimitationRepository.findAll()
                .stream()
                .map(DelimitationMapper::toListDTO)
                .toList();
    }

    public void delete(int id) {
        if (!delimitationRepository.existsById(id)) {
            throw new EntityNotFoundException("Aucune Délimitation trouvée avec l'ID : " + id);
        }
        delimitationRepository.deleteById(id);
    }

}
