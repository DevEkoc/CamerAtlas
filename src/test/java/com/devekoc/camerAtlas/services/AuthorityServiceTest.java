package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.dto.authority.AuthorityCreateDTO;
import com.devekoc.camerAtlas.dto.authority.AuthorityListDTO;
import com.devekoc.camerAtlas.entities.Authority;
import com.devekoc.camerAtlas.repositories.AuthorityRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;


import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorityServiceTest {

    @Mock
    private AuthorityRepository authorityRepository;

    @InjectMocks
    private AuthorityService service;

    Authority authority;

    @BeforeEach
    void init() {
        authority = new Authority();
        authority.setId(1);
        authority.setName("Paul");
        authority.setSurname("Biya");
        authority.setDateOfBirth(LocalDate.of(1933, 2, 13));
    }

    @Test
    void create_shouldCreateAuthoritySuccessfully() {
        AuthorityCreateDTO dto = new AuthorityCreateDTO(
                "Paul",
                "Biya",
                LocalDate.of(1933, 2, 13)
        );

        when(authorityRepository.save(any())).thenReturn(authority);
        AuthorityListDTO result = service.create(dto);

        assertThat(result.name()).isEqualTo("Paul");
        assertThat(result.surname()).isEqualTo("Biya");
        assertThat(result.dateOfBirth()).isEqualTo(LocalDate.of(1933, 2, 13));
        verify(authorityRepository).save(any());
    }

    @Test
    void createSeveral_shouldCreateAllAuthorities() {
        AuthorityCreateDTO dto = new AuthorityCreateDTO("Paul", "Biya", LocalDate.of(1933, 2, 13));
        when(authorityRepository.save(any())).thenReturn(authority);
        List<AuthorityListDTO> list = service.createSeveral(List.of(dto, dto));
        assertThat(list).hasSize(2);
        verify(authorityRepository, times(2)).save(any());
    }

    @Test
    void listAll_shouldReturnAllAuthorities() {
        when(authorityRepository.findAll()).thenReturn(List.of(authority));
        List<AuthorityListDTO> list = service.listAll();
        assertThat(list).hasSize(1);
        assertThat(list.getFirst().name()).isEqualTo("Paul");
    }

    @Test
    void find_shouldReturnAuthority() {
        when(authorityRepository.findById(1)).thenReturn(Optional.of(authority));
        AuthorityListDTO dto = service.find(1);
        assertThat(dto.name()).isEqualTo("Paul");
    }

    @Test
    void find_shouldThrowException_whenAuthorityNotFound() {
        when(authorityRepository.findById(1)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.find(1))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Aucune Autorité trouvée");
    }

    @Test
    void update_shouldUpdateAuthoritySuccessfully() {
        AuthorityCreateDTO dto = new AuthorityCreateDTO(
                "John",
                "Doe",
                LocalDate.of(1980, 1, 1)
        );

        when(authorityRepository.findById(1)).thenReturn(Optional.of(authority));
        when(authorityRepository.save(any())).thenReturn(authority);
        AuthorityListDTO result = service.update(1, dto);
        verify(authorityRepository).save(any());
        assertThat(result.name()).isEqualTo("John");
        assertThat(result.surname()).isEqualTo("Doe");
        assertThat(result.dateOfBirth()).isEqualTo(LocalDate.of(1980, 1, 1));
    }


    @Test
    void update_shouldThrowException_whenNotFound() {
        AuthorityCreateDTO dto = new AuthorityCreateDTO("John", "Doe", LocalDate.now());
        when(authorityRepository.findById(1)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.update(1, dto))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void delete_shouldDeleteSuccessfully() {
        when(authorityRepository.findById(1)).thenReturn(Optional.of(authority));
        service.delete(1);
        verify(authorityRepository).delete(authority);
    }

    @Test
    void delete_shouldThrowException_whenNotFound() {
        when(authorityRepository.findById(1)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.delete(1))
                .isInstanceOf(EntityNotFoundException.class);
    }


}

