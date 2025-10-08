package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.dto.DelimitationRequestDTO;
import com.devekoc.camerAtlas.entities.Circonscription;
import com.devekoc.camerAtlas.entities.Delimitation;
import com.devekoc.camerAtlas.entities.primaryKeys.DelimitationPK;
import com.devekoc.camerAtlas.services.DelimitationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "delimitation")
public class DelimitationController {
    private DelimitationService delimitationService;

    public DelimitationController(DelimitationService delimitationService) {
        this.delimitationService = delimitationService;
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void creer (@RequestBody DelimitationRequestDTO delimitation) {
        delimitationService.creer(delimitation);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<Delimitation> lister () {
        return delimitationService.lister();
    }

    @DeleteMapping(path = "{codeCirconscription}/{idFrontiere}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> supprimer(
            @PathVariable int codeCirconscription,
            @PathVariable int idFrontiere) {
        DelimitationPK id = new DelimitationPK(codeCirconscription, idFrontiere);
        delimitationService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
