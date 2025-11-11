package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.dto.region.RegionCreateDTO;
import com.devekoc.camerAtlas.dto.region.RegionListDTO;
import com.devekoc.camerAtlas.dto.region.RegionListWithDivisionsDTO;
import com.devekoc.camerAtlas.services.RegionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping(value = "regions")
public class RegionController {
    private final RegionService  regionService;

    public RegionController(RegionService regionService) {
        this.regionService = regionService;
    }

    @PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RegionListDTO> create(@RequestBody @Valid @ModelAttribute RegionCreateDTO dto) throws IOException {
        RegionListDTO created = regionService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping(path = "cascade", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<List<RegionListDTO>> createSeveral(@RequestBody @Valid List<RegionCreateDTO> dtos) throws IOException {
        List<RegionListDTO> created = regionService.createSeveral(dtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping(produces =  APPLICATION_JSON_VALUE)
    public ResponseEntity <List<RegionListDTO>> listAll() {
        List<RegionListDTO> regions = regionService.listAll();
        return ResponseEntity.ok(regions);
    }

    @GetMapping(path = "id/{id}", produces =  APPLICATION_JSON_VALUE)
    public ResponseEntity <RegionListDTO> find(@PathVariable int id) {
        RegionListDTO region = regionService.find(id);
        return (region != null)
                ? ResponseEntity.ok(region)
                : ResponseEntity.notFound().build()
        ;
    }

    @GetMapping(path = "name/{name}", produces =  APPLICATION_JSON_VALUE)
    public ResponseEntity <RegionListDTO> find(@PathVariable String name) {
        RegionListDTO region = regionService.find(name);
        return region != null
                ? ResponseEntity.ok(region)
                : ResponseEntity.notFound().build()
        ;
    }

    @GetMapping(path = "id/{id}/divisions", produces =  APPLICATION_JSON_VALUE)
    public ResponseEntity <RegionListWithDivisionsDTO> findWithDivisions(@PathVariable int id) {
        RegionListWithDivisionsDTO region = regionService.findWithDivisions(id);
        return (region != null)
                ? ResponseEntity.ok(region)
                : ResponseEntity.notFound().build()
        ;
    }

    @PutMapping(path = "{id}", consumes = MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity <RegionListDTO> update(@PathVariable int id, @RequestBody @Valid @ModelAttribute RegionCreateDTO dto) throws IOException {
        RegionListDTO updated = regionService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping(path = "id/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity <Void> delete(@PathVariable int id){
        regionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "name/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity <Void> delete(@PathVariable String name){
        regionService.delete(name);
        return ResponseEntity.noContent().build();
    }
}
