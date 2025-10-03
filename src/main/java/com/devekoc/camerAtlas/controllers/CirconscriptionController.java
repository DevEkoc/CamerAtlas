package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.entities.Administrateur;
import com.devekoc.camerAtlas.entities.Circonscription;
import com.devekoc.camerAtlas.services.AdministratorService;
import com.devekoc.camerAtlas.services.CirconscriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "circonscription")
public class CirconscriptionController {
    public CirconscriptionService circonscriptionService;

    public CirconscriptionController(CirconscriptionService circonscriptionService) {
        this.circonscriptionService = circonscriptionService;
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void creer(@RequestBody Circonscription circonscription) {
        circonscriptionService.creer(circonscription);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<Circonscription> listerCirconscriptions() {
        return circonscriptionService.listerCirconscriptions();
    }

    @GetMapping(path = "{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Circonscription> rechercher(@PathVariable int id) {
        Circonscription circonscription = circonscriptionService.rechercher(id);
        return ResponseEntity.ok(circonscription);
    }

    @PutMapping(path = "{id}", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void modifier (@PathVariable int id, @RequestBody Circonscription circonscription) {
        circonscriptionService.modifier(id, circonscription);
    }

    @DeleteMapping(path = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable int id) {
        circonscriptionService.supprimer(id);
    }
}
