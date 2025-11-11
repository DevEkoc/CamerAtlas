package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.dto.division.DivisionCreateDTO;
import com.devekoc.camerAtlas.dto.division.DivisionListDTO;
import com.devekoc.camerAtlas.dto.division.DivisionWithSubDivisionsDTO;
import com.devekoc.camerAtlas.services.DivisionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping(value = "division")
public class DivisionController {
    private final DivisionService divisionService;

    public DivisionController(DivisionService divisionService) {
        this.divisionService = divisionService;
    }

    @PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<DivisionListDTO> create(@RequestBody @Valid @ModelAttribute DivisionCreateDTO dto) throws IOException {
        DivisionListDTO created = divisionService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping(path = "cascade", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<List<DivisionListDTO>> createSeveral(@RequestBody @Valid List<DivisionCreateDTO> dtos) throws IOException {
        List<DivisionListDTO> created = divisionService.createSeveral(dtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping(produces =  APPLICATION_JSON_VALUE)
    public ResponseEntity <List<DivisionListDTO>> listAll() {
        List<DivisionListDTO> departments = divisionService.listAll();
        return ResponseEntity.ok(departments);
    }

    @GetMapping(path = "id/{id}", produces =  APPLICATION_JSON_VALUE)
    public ResponseEntity <DivisionListDTO> find(@PathVariable int id) {
        DivisionListDTO department = divisionService.find(id);
        return (department != null)
                ? ResponseEntity.ok(department)
                : ResponseEntity.notFound().build()
        ;
    }

    @GetMapping(path = "name/{name}", produces =  APPLICATION_JSON_VALUE)
    public ResponseEntity <DivisionListDTO> find(@PathVariable String name) {
        DivisionListDTO department = divisionService.find(name);
        return department != null
                ? ResponseEntity.ok(department)
                : ResponseEntity.notFound().build()
        ;
    }

    @GetMapping(path = "id/{id}/subDivisions")
    public ResponseEntity<DivisionWithSubDivisionsDTO> findWithSubDivisions (@PathVariable int id) {
        DivisionWithSubDivisionsDTO division = divisionService.findWithSubDivisions(id);
        return division != null
                ? ResponseEntity.ok(division)
                : ResponseEntity.notFound().build()
        ;
    }

    @PutMapping(path = "{id}", consumes = MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity <DivisionListDTO> update(@PathVariable int id, @RequestBody @Valid @ModelAttribute DivisionCreateDTO dto) throws IOException {
        DivisionListDTO updated = divisionService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping(path = "id/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity <Void> delete(@PathVariable int id){
        divisionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "name/{nom}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity <Void> delete(@PathVariable String nom){
        divisionService.delete(nom);
        return ResponseEntity.noContent().build();
    }
}
