package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.entities.Departement;
import com.devekoc.camerAtlas.services.DepartementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "departement")
public class DepartementController {
    private final DepartementService  departementService;

    public DepartementController(DepartementService departementService) {
        this.departementService = departementService;
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Departement> creer (@RequestBody Departement departement){
        Departement departementCree = departementService.creer(departement);
        return ResponseEntity.status(HttpStatus.CREATED).body(departementCree);
    }

    @GetMapping(produces =  APPLICATION_JSON_VALUE)
    public ResponseEntity <List<Departement>> lister () {
        List<Departement> departements = departementService.lister();
        return ResponseEntity.ok(departements);
    }

    @GetMapping(path = "id/{id}", produces =  APPLICATION_JSON_VALUE)
    public ResponseEntity <Departement> rechercher (@PathVariable int id) {
        Departement departement = departementService.rechercher(id);
        // peut être remplacé par return departement.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build())
        // mais je préfère celle-ci
        return (departement != null)
                ? ResponseEntity.ok(departement)
                : ResponseEntity.notFound().build()
        ;
    }

    @GetMapping(path = "nom/{nom}", produces =  APPLICATION_JSON_VALUE)
    public ResponseEntity <Departement> rechercher (@PathVariable String nom) {
        Departement departement = departementService.rechercher(nom);
        // peut-être remplacé par return departement.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build())
        // mais je préfère celle-ci
        return departement != null
                ? ResponseEntity.ok(departement)
                : ResponseEntity.notFound().build()
        ;
    }

    @PutMapping(path = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity <Departement> modifier (@PathVariable int id, @RequestBody Departement departement){
        Departement departementModifie = departementService.modifier(id, departement);
        return ResponseEntity.ok(departementModifie);
    }

    @DeleteMapping(path = "id/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity <Void> supprimer (@PathVariable int id){
        departementService.supprimer(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "nom/{nom}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity <Void> supprimer (@PathVariable String nom){
        departementService.supprimer(nom);
        return ResponseEntity.noContent().build();
    }
}
