package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.dto.region.RegionCreateDTO;
import com.devekoc.camerAtlas.dto.suggestion.SuggestionCreateDTO;
import com.devekoc.camerAtlas.dto.suggestion.SuggestionListDTO;
import com.devekoc.camerAtlas.dto.user.*;
import com.devekoc.camerAtlas.entities.Suggestion;
import com.devekoc.camerAtlas.entities.User;
import com.devekoc.camerAtlas.enumerations.*;
import com.devekoc.camerAtlas.repositories.SuggestionRepository;
import com.devekoc.camerAtlas.suggestions.SuggestionHandler;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;


import java.io.IOException;
import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SuggestionServiceTest {

    @Mock SuggestionRepository suggestionRepository;
    @Mock ObjectMapper objectMapper;

    // Handler simulé pour REGION
    @Mock SuggestionHandler<RegionCreateDTO> regionHandler;

    @Mock Validator validator;

    SuggestionService suggestionService;

    @BeforeEach
    void setUp() {
        lenient().when(regionHandler.dtoType()).thenReturn(RegionCreateDTO.class);
        lenient().when(regionHandler.handledType()).thenReturn(TargetType.REGION);

        suggestionService = new SuggestionService(
                List.of(regionHandler), // on ne teste qu’un handler ici
                objectMapper,
                suggestionRepository,
                validator
        );

        // --- Mock SecurityContext ---
        User user = new User();
        user.setId(99);
        user.setName("Jean");

        Authentication auth = mock(Authentication.class);
        lenient().when(auth.getPrincipal()).thenReturn(user);

        SecurityContext sec = mock(SecurityContext.class);
        lenient().when(sec.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(sec);
    }

    // =========================================================================
    // CREATE
    // =========================================================================
    @Test
    void create_shouldSaveSuggestionWithUser() {
        SuggestionCreateDTO dto = new SuggestionCreateDTO(
                SuggestionType.CREATE,
                TargetType.REGION,
                0,
                "{\"name\":\"Centre\",\"surface\":10000}"
        );

        Suggestion saved = new Suggestion();
        saved.setId(1);
        saved.setType(dto.type());
        saved.setTargetType(dto.targetType());
        saved.setPayload(dto.payload());
        saved.setStatus(SuggestionStatus.PENDING);
        saved.setSubmittedBy((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        saved.setSubmittedAt(Instant.now());

        when(suggestionRepository.save(any())).thenReturn(saved);

        SuggestionListDTO result = suggestionService.create(dto);

        assertThat(result.id()).isEqualTo(1);
        verify(suggestionRepository).save(any(Suggestion.class));
    }

    // =========================================================================
    // APPROVE - CREATE
    // =========================================================================
    @Test
    void approve_shouldCallRegionHandlerCreate() throws Exception {
        Suggestion sug = new Suggestion();
        sug.setId(1);
        sug.setType(SuggestionType.CREATE);
        sug.setTargetType(TargetType.REGION);
        sug.setPayload("""
                {"name":"Centre","surface":10000,"population":4000000,"gpsCoordinates":"0,0","capital":"Yaoundé","mineralogicalCode":"CE"}
                """);
        sug.setStatus(SuggestionStatus.PENDING);

        RegionCreateDTO regionDTO = new RegionCreateDTO(
                "Centre",
                10000,
                4000000,
                "0,0",
                "Yaoundé",
                "CE",
                null
        );

        when(suggestionRepository.findById(1)).thenReturn(Optional.of(sug));
        when(objectMapper.readValue(anyString(), eq(RegionCreateDTO.class)))
                .thenReturn(regionDTO);

        suggestionService.approve(1);

        verify(regionHandler).create(regionDTO);
        assertThat(sug.getStatus()).isEqualTo(SuggestionStatus.APPROVED);
        verify(suggestionRepository).save(sug);
    }

    // =========================================================================
    // APPROVE - UPDATE
    // =========================================================================
    @Test
    void approve_shouldCallRegionHandlerUpdate() throws Exception {
        Suggestion sug = new Suggestion();
        sug.setId(1);
        sug.setType(SuggestionType.UPDATE);
        sug.setTargetType(TargetType.REGION);
        sug.setTargetId(10);
        sug.setPayload("{\"name\":\"Centre New\"}");
        sug.setStatus(SuggestionStatus.PENDING);

        RegionCreateDTO dto = new RegionCreateDTO(
                "Centre New",
                10000,
                4000000,
                null,
                "Yaoundé",
                "CE",
                null
        );

        when(suggestionRepository.findById(1)).thenReturn(Optional.of(sug));
        when(objectMapper.readValue(anyString(), eq(RegionCreateDTO.class)))
                .thenReturn(dto);

        suggestionService.approve(1);

        verify(regionHandler).update(10, dto);
        assertThat(sug.getStatus()).isEqualTo(SuggestionStatus.APPROVED);
    }

    // =========================================================================
    // APPROVE - DELETE
    // =========================================================================
    @Test
    void approve_shouldCallRegionHandlerDelete() {
        Suggestion sug = new Suggestion();
        sug.setId(1);
        sug.setType(SuggestionType.DELETE);
        sug.setTargetType(TargetType.REGION);
        sug.setTargetId(5);
        sug.setPayload("{}");
        sug.setStatus(SuggestionStatus.PENDING);

        when(suggestionRepository.findById(1)).thenReturn(Optional.of(sug));

        suggestionService.approve(1);

        verify(regionHandler).delete(5);
        assertThat(sug.getStatus()).isEqualTo(SuggestionStatus.APPROVED);
    }

    // =========================================================================
    // APPROVE - errors
    // =========================================================================
    @Test
    void approve_shouldThrowIfAlreadyProcessed() {
        Suggestion sug = new Suggestion();
        sug.setStatus(SuggestionStatus.APPROVED);

        when(suggestionRepository.findById(1)).thenReturn(Optional.of(sug));

        assertThatThrownBy(() -> suggestionService.approve(1))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void approve_shouldThrowIfHandlerMissing() {
        Suggestion sug = new Suggestion();
        sug.setType(SuggestionType.CREATE);
        sug.setTargetType(TargetType.DIVISION); // aucun handler fourni
        sug.setStatus(SuggestionStatus.PENDING);

        when(suggestionRepository.findById(1)).thenReturn(Optional.of(sug));

        assertThatThrownBy(() -> suggestionService.approve(1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Type cible non géré");
    }

    @Test
    void approve_shouldThrowOnJsonError() throws Exception {
        Suggestion sug = new Suggestion();
        sug.setType(SuggestionType.CREATE);
        sug.setTargetType(TargetType.REGION);
        sug.setPayload("{bad json}");
        sug.setStatus(SuggestionStatus.PENDING);

        when(suggestionRepository.findById(1)).thenReturn(Optional.of(sug));
        doThrow(new JsonParseException("Invalid JSON"))
                .when(objectMapper)
                .readValue(anyString(), eq(RegionCreateDTO.class));


        assertThatThrownBy(() -> suggestionService.approve(1))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("désérialisation");
    }

    // =========================================================================
    // REJECT
    // =========================================================================
    @Test
    void reject_shouldSetStatusRejected() {
        Suggestion sug = new Suggestion();
        sug.setId(1);
        sug.setStatus(SuggestionStatus.PENDING);

        when(suggestionRepository.findById(1)).thenReturn(Optional.of(sug));

        suggestionService.reject(1);

        assertThat(sug.getStatus()).isEqualTo(SuggestionStatus.REJECTED);
        verify(suggestionRepository).save(sug);
    }

    @Test
    void reject_shouldThrowIfAlreadyProcessed() {
        Suggestion sug = new Suggestion();
        sug.setStatus(SuggestionStatus.APPROVED);

        when(suggestionRepository.findById(1)).thenReturn(Optional.of(sug));

        assertThatThrownBy(() -> suggestionService.reject(1))
                .isInstanceOf(RuntimeException.class);
    }
}
