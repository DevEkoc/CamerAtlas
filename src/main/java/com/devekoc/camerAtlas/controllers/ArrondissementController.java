package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.entities.Arrondissement;
import com.devekoc.camerAtlas.services.ArrondissementService;
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
    public ResponseEntity<Arrondissement> creer (@RequestBody Arrondissement arrondissement){
        Arrondissement arrondissementCree = arrondissementService.creer(arrondissement);
        return ResponseEntity.status(HttpStatus.CREATED).body(arrondissementCree);
    }

    @GetMapping(produces =  APPLICATION_JSON_VALUE)
    public ResponseEntity <List<Arrondissement>> lister () {
        List<Arrondissement> arrondissements = arrondissementService.lister();
        return ResponseEntity.ok(arrondissements);
    }

    @GetMapping(path = "id/{id}", produces =  APPLICATION_JSON_VALUE)
    public ResponseEntity <Arrondissement> rechercher (@PathVariable int id) {
        Arrondissement arrondissement = arrondissementService.rechercher(id);
        // peut être remplacé par return arrondissement.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build())
        // mais je préfère celle-ci
        return (arrondissement != null)
                ? ResponseEntity.ok(arrondissement)
                : ResponseEntity.notFound().build()
        ;
    }

    @GetMapping(path = "nom/{nom}", produces =  APPLICATION_JSON_VALUE)
    public ResponseEntity <Arrondissement> rechercher (@PathVariable String nom) {
        Arrondissement arrondissement = arrondissementService.rechercher(nom);
        // peut-être remplacé par return arrondissement.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build())
        // mais je préfère celle-ci
        return arrondissement != null
                ? ResponseEntity.ok(arrondissement)
                : ResponseEntity.notFound().build()
        ;
    }

    @PutMapping(path = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity <Arrondissement> modifier (@PathVariable int id, @RequestBody Arrondissement arrondissement){
        Arrondissement arrondissementModifie = arrondissementService.modifier(id, arrondissement);
        return ResponseEntity.ok(arrondissementModifie);
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
