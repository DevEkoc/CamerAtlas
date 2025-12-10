package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.dto.delimitation.DelimitationCreateDTO;
import com.devekoc.camerAtlas.dto.delimitation.DelimitationListDTO;
import com.devekoc.camerAtlas.services.DelimitationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@AllArgsConstructor
@RequestMapping(value = "delimitations")
public class DelimitationController {
    private final DelimitationService delimitationService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<DelimitationListDTO> create(@RequestBody @Valid DelimitationCreateDTO dto) {
        DelimitationListDTO created = delimitationService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping(path = "cascade", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<List<DelimitationListDTO>> createSeveral(@RequestBody @Valid List<DelimitationCreateDTO> dtos) {
        List<DelimitationListDTO> delimitations = delimitationService.createSeveral(dtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(delimitations);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DelimitationListDTO>> listAll() {
        List<DelimitationListDTO> delimitations = delimitationService.listAll();
        return ResponseEntity.ok(delimitations);
    }

    @DeleteMapping(path = "id/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable int id) {
        delimitationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
