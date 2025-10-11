package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.dto.quartier.QuartierCreateDTO;
import com.devekoc.camerAtlas.dto.quartier.QuartierListerDTO;
import com.devekoc.camerAtlas.services.QuartierService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "quartier")
public class QuartierController {
    private final QuartierService  quartierService;

    public QuartierController(QuartierService quartierService) {
        this.quartierService = quartierService;
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<QuartierListerDTO> creer (@RequestBody @Valid QuartierCreateDTO dto){
        QuartierListerDTO created = quartierService.creer(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping(produces =  APPLICATION_JSON_VALUE)
    public ResponseEntity <List<QuartierListerDTO>> lister () {
        List<QuartierListerDTO> quartiers = quartierService.lister();
        return ResponseEntity.ok(quartiers);
    }

    @GetMapping(path = "id/{id}", produces =  APPLICATION_JSON_VALUE)
    public ResponseEntity <QuartierListerDTO> rechercher (@PathVariable int id) {
        QuartierListerDTO quartier = quartierService.rechercher(id);
        // peut être remplacé par return quartier.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build())
        // mais je préfère celle-ci
        return (quartier != null)
                ? ResponseEntity.ok(quartier)
                : ResponseEntity.notFound().build()
        ;
    }

    @GetMapping(path = "nom/{nom}", produces =  APPLICATION_JSON_VALUE)
    public ResponseEntity <QuartierListerDTO> rechercher (@PathVariable String nom) {
        QuartierListerDTO quartier = quartierService.rechercher(nom);
        // peut-être remplacé par return quartier.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build())
        // mais je préfère celle-ci
        return (quartier != null)
                ? ResponseEntity.ok(quartier)
                : ResponseEntity.notFound().build()
        ;
    }

    @PutMapping(path = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity <QuartierListerDTO> modifier (
            @PathVariable int id,
            @RequestBody @Valid QuartierCreateDTO dto)
    {
        QuartierListerDTO updated = quartierService.modifier(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping(path = "id/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity <Void> supprimer (@PathVariable int id){
        quartierService.supprimer(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "nom/{nom}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity <Void> supprimer (@PathVariable String nom){
        quartierService.supprimer(nom);
        return ResponseEntity.noContent().build();
    }
}
