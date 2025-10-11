package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.dto.affectation.AffectationCreateDTO;
import com.devekoc.camerAtlas.dto.affectation.AffectationListerDTO;
import com.devekoc.camerAtlas.services.AffectationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "affectation")
public class AffectationController {
    private final AffectationService affectationService;

    public AffectationController(AffectationService affectationService) {
        this.affectationService = affectationService;
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AffectationListerDTO> creer (@RequestBody @Valid AffectationCreateDTO dto){
        AffectationListerDTO created = affectationService.creer(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity <List<AffectationListerDTO>> lister () {
        List<AffectationListerDTO> affectations = affectationService.lister();
        return ResponseEntity.ok(affectations);
    }

    @PutMapping(path = "{id}", consumes =  APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity <AffectationListerDTO> modifier (@PathVariable int id, @RequestBody @Valid AffectationCreateDTO dto) {
        AffectationListerDTO updated = affectationService.modifier(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping(path = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity <Void> supprimer (@PathVariable int id) {
        affectationService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
