package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.dto.neighborhood.NeighborhoodCreateDTO;
import com.devekoc.camerAtlas.dto.neighborhood.NeighborhoodListDTO;
import com.devekoc.camerAtlas.services.NeighborhoodService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "neighborhood")
public class NeighborhoodController {
    private final NeighborhoodService neighborhoodService;

    public NeighborhoodController(NeighborhoodService neighborhoodService) {
        this.neighborhoodService = neighborhoodService;
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<NeighborhoodListDTO> create(@RequestBody @Valid NeighborhoodCreateDTO dto){
        NeighborhoodListDTO created = neighborhoodService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping(path = "cascade", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<List<NeighborhoodListDTO>> createSeveral(@RequestBody @Valid List<NeighborhoodCreateDTO> dtos) {
        List<NeighborhoodListDTO> created = neighborhoodService.createSeveral(dtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping(produces =  APPLICATION_JSON_VALUE)
    public ResponseEntity <List<NeighborhoodListDTO>> listAll() {
        List<NeighborhoodListDTO> neighborhoods = neighborhoodService.listAll();
        return ResponseEntity.ok(neighborhoods);
    }

    @GetMapping(path = "id/{id}", produces =  APPLICATION_JSON_VALUE)
    public ResponseEntity <NeighborhoodListDTO> find(@PathVariable int id) {
        NeighborhoodListDTO neighborhood = neighborhoodService.find(id);
        return (neighborhood != null)
                ? ResponseEntity.ok(neighborhood)
                : ResponseEntity.notFound().build()
        ;
    }

    @GetMapping(path = "name/{name}", produces =  APPLICATION_JSON_VALUE)
    public ResponseEntity <NeighborhoodListDTO> find(@PathVariable String name) {
        NeighborhoodListDTO neighborhood = neighborhoodService.find(name);
        return (neighborhood != null)
                ? ResponseEntity.ok(neighborhood)
                : ResponseEntity.notFound().build()
        ;
    }

    @PutMapping(path = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity <NeighborhoodListDTO> update(@PathVariable int id, @RequestBody @Valid NeighborhoodCreateDTO dto) {
        NeighborhoodListDTO updated = neighborhoodService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping(path = "id/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity <Void> delete(@PathVariable int id){
        neighborhoodService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "name/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity <Void> delete(@PathVariable String name){
        neighborhoodService.delete(name);
        return ResponseEntity.noContent().build();
    }
}
