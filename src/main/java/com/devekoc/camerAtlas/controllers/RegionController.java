package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.dto.region.RegionCreateDTO;
import com.devekoc.camerAtlas.dto.region.RegionListerDTO;
import com.devekoc.camerAtlas.entities.Region;
import com.devekoc.camerAtlas.services.RegionService;
import jakarta.validation.Valid;
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
    public ResponseEntity<RegionListerDTO> creer (@RequestBody @Valid RegionCreateDTO dto){
        RegionListerDTO created = regionService.creer(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping(produces =  APPLICATION_JSON_VALUE)
    public ResponseEntity <List<RegionListerDTO>> lister () {
        List<RegionListerDTO> regions = regionService.lister();
        return ResponseEntity.ok(regions);
    }

    @GetMapping(path = "id/{id}", produces =  APPLICATION_JSON_VALUE)
    public ResponseEntity <RegionListerDTO> rechercher (@PathVariable int id) {
        RegionListerDTO region = regionService.rechercher(id);
        // peut être remplacé par return region.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build())
        // mais je préfère celle-ci
        return (region != null)
                ? ResponseEntity.ok(region)
                : ResponseEntity.notFound().build()
        ;
    }

    @GetMapping(path = "nom/{nom}", produces =  APPLICATION_JSON_VALUE)
    public ResponseEntity <RegionListerDTO> rechercher (@PathVariable String nom) {
        RegionListerDTO region = regionService.rechercher(nom);
        // peut-être remplacé par return region.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build())
        // mais je préfère celle-ci
        return region != null
                ? ResponseEntity.ok(region)
                : ResponseEntity.notFound().build()
        ;
    }

    @PutMapping(path = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity <RegionListerDTO> modifier (@PathVariable int id, @RequestBody @Valid RegionCreateDTO dto){
        RegionListerDTO updated = regionService.modifier(id, dto);
        return ResponseEntity.ok(updated);
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
