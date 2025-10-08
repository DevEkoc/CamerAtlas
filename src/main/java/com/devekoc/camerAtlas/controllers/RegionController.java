package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.entities.Region;
import com.devekoc.camerAtlas.services.RegionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "region")
public class RegionController {
    private final RegionService  regionService;

    public RegionController(RegionService regionService) {
        this.regionService = regionService;
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Region> creer (@RequestBody Region region){
        Region regionCree = regionService.creer(region);
        return ResponseEntity.status(HttpStatus.CREATED).body(regionCree);
    }

    @GetMapping(produces =  APPLICATION_JSON_VALUE)
    public ResponseEntity <List<Region>> lister () {
        List<Region> regions = regionService.lister();
        return ResponseEntity.ok(regions);
    }

    @GetMapping(path = "id/{id}", produces =  APPLICATION_JSON_VALUE)
    public ResponseEntity <Region> rechercher (@PathVariable int id) {
        Region region = regionService.rechercher(id);
        // peut être remplacé par return region.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build())
        // mais je préfère celle-ci
        return (region != null)
                ? ResponseEntity.ok(region)
                : ResponseEntity.notFound().build()
        ;
    }

    @GetMapping(path = "nom/{nom}", produces =  APPLICATION_JSON_VALUE)
    public ResponseEntity <Region> rechercher (@PathVariable String nom) {
        Region region = regionService.rechercher(nom);
        // peut-être remplacé par return region.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build())
        // mais je préfère celle-ci
        return region != null
                ? ResponseEntity.ok(region)
                : ResponseEntity.notFound().build()
        ;
    }

    @PutMapping(path = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity <Region> modifier (@PathVariable int id, @RequestBody Region region){
        Region regionModifiee = regionService.modifier(id, region);
        return ResponseEntity.ok(regionModifiee);
    }

    @DeleteMapping(path = "id/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity <Void> supprimer (@PathVariable int id){
        regionService.supprimer(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "nom/{nom}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity <Void> supprimer (@PathVariable String nom){
        regionService.supprimer(nom);
        return ResponseEntity.noContent().build();
    }
}
