package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.dto.suggestion.SuggestionCreateDTO;
import com.devekoc.camerAtlas.dto.suggestion.SuggestionListDTO;
import com.devekoc.camerAtlas.services.SuggestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("suggestions")
@RequiredArgsConstructor
public class SuggestionController {

    private final SuggestionService suggestionService;

    @PostMapping
    public ResponseEntity<SuggestionListDTO> create(@RequestBody @Valid SuggestionCreateDTO dto) {
        return ResponseEntity.ok(suggestionService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<SuggestionListDTO>> listAll() {
        return ResponseEntity.ok(suggestionService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuggestionListDTO> find(@PathVariable int id) {
        return ResponseEntity.ok(suggestionService.find(id));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Void> approve(@PathVariable int id) {
        suggestionService.approve(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Void> reject(@PathVariable int id) {
        suggestionService.reject(id);
        return ResponseEntity.ok().build();
    }
}
