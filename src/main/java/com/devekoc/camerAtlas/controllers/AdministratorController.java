package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.dto.ErrorEntity;
import com.devekoc.camerAtlas.entities.Administrateur;
import com.devekoc.camerAtlas.services.AdministratorService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "administrateur")
public class AdministratorController {
    public AdministratorService administratorService;

    public AdministratorController(AdministratorService administratorService) {
        this.administratorService = administratorService;
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void creer(@RequestBody Administrateur administrateur) {
        administratorService.creer(administrateur);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<Administrateur> listerAdministrateurs() {
        return administratorService.listerAdministrateurs();
    }

    @GetMapping(path = "{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Administrateur> rechercher(@PathVariable int id) {
        Administrateur administrateur = administratorService.rechercher(id);
        return ResponseEntity.ok(administrateur);
    }

    @PutMapping(path = "{id}", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void modifier (@PathVariable int id, @RequestBody Administrateur administrateur) {
        administratorService.modifier(id, administrateur);
    }

    @DeleteMapping(path = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable int id) {
        administratorService.supprimer(id);
    }
}
