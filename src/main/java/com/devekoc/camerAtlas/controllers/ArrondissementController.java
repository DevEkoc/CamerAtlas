package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.dto.arrondissement.ArrondissementCreateDTO;
import com.devekoc.camerAtlas.dto.arrondissement.ArrondissementListerDTO;
import com.devekoc.camerAtlas.services.ArrondissementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "arrondissement")
public class ArrondissementController {
    private final ArrondissementService  arrondissementService;

    public ArrondissementController(ArrondissementService arrondissementService) {
        this.arrondissementService = arrondissementService;
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ArrondissementListerDTO> creer (@RequestBody @Valid ArrondissementCreateDTO dto){
        ArrondissementListerDTO created = arrondissementService.creer(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping(produces =  APPLICATION_JSON_VALUE)
    public ResponseEntity <List<ArrondissementListerDTO>> lister () {
        List<ArrondissementListerDTO> arrondissements = arrondissementService.lister();
        return ResponseEntity.ok(arrondissements);
    }

    @GetMapping(path = "id/{id}", produces =  APPLICATION_JSON_VALUE)
    public ResponseEntity <ArrondissementListerDTO> rechercher (@PathVariable int id) {
        ArrondissementListerDTO arrondissement = arrondissementService.rechercher(id);
        // peut être remplacé par return arrondissement.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build())
        // mais je préfère celle-ci
        return (arrondissement != null)
                ? ResponseEntity.ok(arrondissement)
                : ResponseEntity.notFound().build()
        ;
    }

    @GetMapping(path = "nom/{nom}", produces =  APPLICATION_JSON_VALUE)
    public ResponseEntity <ArrondissementListerDTO> rechercher (@PathVariable String nom) {
        ArrondissementListerDTO arrondissement = arrondissementService.rechercher(nom);
        // peut-être remplacé par return arrondissement.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build())
        // mais je préfère celle-ci
        return (arrondissement != null)
                ? ResponseEntity.ok(arrondissement)
                : ResponseEntity.notFound().build()
        ;
    }

    @PutMapping(path = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity <ArrondissementListerDTO> modifier (
            @PathVariable int id,
            @RequestBody @Valid ArrondissementCreateDTO dto)
    {
        ArrondissementListerDTO updated = arrondissementService.modifier(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping(path = "id/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity <Void> supprimer (@PathVariable int id){
        arrondissementService.supprimer(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "nom/{nom}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity <Void> supprimer (@PathVariable String nom){
        arrondissementService.supprimer(nom);
        return ResponseEntity.noContent().build();
    }
}
