package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.dto.suggestion.SuggestionCreateDTO;
import com.devekoc.camerAtlas.dto.suggestion.SuggestionListDTO;
import com.devekoc.camerAtlas.entities.*;
import com.devekoc.camerAtlas.enumerations.SuggestionStatus;
import com.devekoc.camerAtlas.enumerations.TargetType;
import com.devekoc.camerAtlas.mappers.SuggestionMapper;
import com.devekoc.camerAtlas.repositories.*;
import com.devekoc.camerAtlas.suggestions.SuggestionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SuggestionService {
    private final SuggestionRepository suggestionRepository;
    private final ObjectMapper objectMapper;
    private final Map<TargetType, SuggestionHandler<?>> handlers;
    private final Validator validator;

    public SuggestionService(List<SuggestionHandler<?>> handlerList, ObjectMapper objectMapper, SuggestionRepository suggestionRepository, Validator validator) {
        this.objectMapper = objectMapper;
        this.handlers = handlerList.stream()
                .collect(Collectors.toMap(
                        SuggestionHandler::handledType,
                        h -> h
                ));
        this.suggestionRepository = suggestionRepository;
        this.validator = validator;
    }

    public SuggestionListDTO create(SuggestionCreateDTO dto) {
        // 1. Résolution du handler
        SuggestionHandler<?> handler = handlers.get(dto.targetType());
        if (handler == null)
            throw new IllegalArgumentException("Type cible non géré : " + dto.targetType());

        // 2. Désérialisation du payload
        Object payloadObject;
        try {
            payloadObject = objectMapper.readValue(dto.payload(), handler.dtoType());
        } catch (IOException e) {
            throw new RuntimeException("Payload JSON invalide", e);
        }

        // 3. Validation du DTO provenant du payload
        var violations = validator.validate(payloadObject);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("Payload invalide\n" + violations, violations);
        }

        Suggestion suggestion = new Suggestion();
        suggestion.setType(dto.type());
        suggestion.setTargetType(dto.targetType());
        suggestion.setTargetId(dto.targetId());
        suggestion.setPayload(dto.payload());
        suggestion.setStatus(SuggestionStatus.PENDING);
        suggestion.setSubmittedAt(Instant.now());
        suggestion.setSubmittedBy(getAuthenticatedUser());

        Suggestion saved = suggestionRepository.save(suggestion);
        return SuggestionMapper.toDTO(saved);
    }

    public List<SuggestionListDTO> listAll() {
        return suggestionRepository.findAll()
                .stream()
                .map(SuggestionMapper::toDTO)
                .toList();
    }

    public SuggestionListDTO find(int id) {
        return SuggestionMapper.toDTO(
                suggestionRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Suggestion introuvable"))
        );
    }

    @Transactional
    public void approve(int id) {
        Suggestion suggestion = suggestionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Suggestion introuvable"));

        if (suggestion.getStatus() != SuggestionStatus.PENDING)
            throw new IllegalStateException("Suggestion déjà traitée");

        SuggestionHandler<?> handler = handlers.get(suggestion.getTargetType());
        if (handler == null)
            throw new IllegalArgumentException("Type cible non géré");

        applySuggestion(suggestion, handler);

        suggestion.setStatus(SuggestionStatus.APPROVED);
        suggestionRepository.save(suggestion);
    }

    private <T> void applySuggestion(Suggestion s, SuggestionHandler<T> handler) {
        try {
            T dto = objectMapper.readValue(s.getPayload(), handler.dtoType());

            switch (s.getType()) {
                case CREATE -> handler.create(dto);
                case UPDATE -> handler.update(s.getTargetId(), dto);
                case DELETE -> handler.delete(s.getTargetId());
            }
        } catch (IOException e) {
            throw new RuntimeException("Erreur durant la désérialisation JSON", e);
        }
    }

    public void reject(int id) {
        Suggestion suggestion = suggestionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Suggestion introuvable"));

        if (suggestion.getStatus() != SuggestionStatus.PENDING)
            throw new RuntimeException("Suggestion déjà traitée");

        suggestion.setStatus(SuggestionStatus.REJECTED);
        suggestionRepository.save(suggestion);
    }

    private User getAuthenticatedUser() {
        // récupère l’utilisateur connecté
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (User) auth.getPrincipal();
    }
}
