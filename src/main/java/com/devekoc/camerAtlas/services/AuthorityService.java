package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.dto.authority.AuthorityCreateDTO;
import com.devekoc.camerAtlas.dto.authority.AuthorityListDTO;
import com.devekoc.camerAtlas.entities.Authority;
import com.devekoc.camerAtlas.mappers.AuthorityMapper;
import com.devekoc.camerAtlas.repositories.AuthorityRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class AuthorityService {
    private final AuthorityRepository authorityRepository;

    public AuthorityService(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    public AuthorityListDTO create(AuthorityCreateDTO dto) {
        Authority authority = AuthorityMapper.fromCreateDTO(dto, new Authority());
        Authority saved = authorityRepository.save(authority);
        return AuthorityMapper.toListDTO(saved);
    }

    public List<AuthorityListDTO> createSeveral(List<AuthorityCreateDTO> dtos) {
        List<AuthorityListDTO> autorities = new ArrayList<>();
        for (AuthorityCreateDTO dto : dtos) {
            autorities.add(create(dto));
        }
        return autorities;
    }

    public List<AuthorityListDTO> listAll() {
        return authorityRepository.findAll()
                .stream()
                .map(AuthorityMapper::toListDTO)
                .toList();
    }

    public AuthorityListDTO find(int id) {
        Authority found = authorityRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Aucune Autorité trouvée avec l'ID : " + id)
        );
        return AuthorityMapper.toListDTO(found);
    }

    public AuthorityListDTO update(int id, AuthorityCreateDTO dto) {
        Authority authority = authorityRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Aucune Autorité trouvée avec l'ID : " + id)
        );
        Authority updated =  AuthorityMapper.fromCreateDTO(dto, authority);
        Authority saved = authorityRepository.save(updated);
        return AuthorityMapper.toListDTO(saved);
    }

    public void supprimer(int id) {
        Authority found =  authorityRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Aucune Autorité trouvée avec l'ID : " + id)
        );
        authorityRepository.delete(found);
    }
}
