package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.entities.Affectation;
import com.devekoc.camerAtlas.services.AffectationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
    public void creer (@RequestBody @Valid Affectation affectation){
        affectationService.creer(affectation);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<Affectation> lister () {
        return affectationService.lister();
    }

    @PutMapping(path = "{id}", consumes =  APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void modifier (@PathVariable int id, @RequestBody @Valid Affectation affectation) {
        affectationService.modifier(id, affectation);
    }

    @DeleteMapping(path = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer (@PathVariable int id) {
        affectationService.supprimer(id);
    }
}
