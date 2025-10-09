package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.entities.Frontiere;
import com.devekoc.camerAtlas.enumerations.TypeFrontiere;
import com.devekoc.camerAtlas.services.FrontiereService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "frontiere")
public class FrontiereController {
    private final FrontiereService frontiereService;

    public FrontiereController(FrontiereService frontiereService) {
        this.frontiereService = frontiereService;
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void creer (@RequestBody @Valid Frontiere frontiere) {
        frontiereService.creer(frontiere);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<Frontiere> lister () {
        return frontiereService.lister();
    }

    @GetMapping(value = "{type}", produces = APPLICATION_JSON_VALUE)
    public @ResponseBody List<Frontiere> rechercherParType (@RequestParam TypeFrontiere type) {
        return frontiereService.rechercherParType(type);
    }

}
