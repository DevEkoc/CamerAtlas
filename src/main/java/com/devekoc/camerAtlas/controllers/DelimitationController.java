package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.dto.delimitation.DelimitationCreateDTO;
import com.devekoc.camerAtlas.dto.delimitation.DelimitationListerDTO;
import com.devekoc.camerAtlas.services.DelimitationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "delimitation")
public class DelimitationController {
    private final DelimitationService delimitationService;

    public DelimitationController(DelimitationService delimitationService) {
        this.delimitationService = delimitationService;
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public DelimitationListerDTO creer (@RequestBody DelimitationCreateDTO delimitation) {
        return delimitationService.creer(delimitation);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<DelimitationListerDTO> lister () {
        return delimitationService.lister();
    }

    @DeleteMapping(path = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> supprimer(
            @PathVariable int id) {
        delimitationService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
