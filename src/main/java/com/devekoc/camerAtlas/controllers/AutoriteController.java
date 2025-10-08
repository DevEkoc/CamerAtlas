package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.entities.Autorite;
import com.devekoc.camerAtlas.services.AutoriteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "autorite")
public class AutoriteController {
    public AutoriteService autoriteService;

    public AutoriteController(AutoriteService autoriteService) {
        this.autoriteService = autoriteService;
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void creer(@RequestBody Autorite autorite) {
        autoriteService.creer(autorite);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<Autorite> lister() {
        return autoriteService.lister();
    }

    @GetMapping(path = "{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Autorite> rechercher(@PathVariable int id) {
        Autorite autorite = autoriteService.rechercher(id);
        return ResponseEntity.ok(autorite);
    }

    @PutMapping(path = "{id}", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void modifier (@PathVariable int id, @RequestBody Autorite autorite) {
        autoriteService.modifier(id, autorite);
    }

    @DeleteMapping(path = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable int id) {
        autoriteService.supprimer(id);
    }
}
