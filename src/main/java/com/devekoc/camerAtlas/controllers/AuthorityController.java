package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.dto.authority.AuthorityCreateDTO;
import com.devekoc.camerAtlas.dto.authority.AuthorityListDTO;
import com.devekoc.camerAtlas.services.AuthorityService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@AllArgsConstructor
@RequestMapping(value = "authorities")
public class AuthorityController {
    private final AuthorityService authorityService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AuthorityListDTO> create(@RequestBody @Valid AuthorityCreateDTO autorite) {
        AuthorityListDTO created = authorityService.create(autorite);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping(path ="cascade", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<List<AuthorityListDTO>> createSeveral(@RequestBody @Valid List<AuthorityCreateDTO> autorites) {
        List<AuthorityListDTO> created = authorityService.createSeveral(autorites);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AuthorityListDTO>> listAll() {
        List<AuthorityListDTO> autorites = authorityService.listAll();
        return ResponseEntity.ok(autorites);
    }

    @GetMapping(path = "id/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthorityListDTO> find(@PathVariable int id) {
        AuthorityListDTO autorite = authorityService.find(id);
        return (autorite != null)
                ? ResponseEntity.ok(autorite)
                : ResponseEntity.notFound().build();
    }

    @PutMapping(path = "id/{id}", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<AuthorityListDTO> update(@PathVariable int id, @RequestBody @Valid AuthorityCreateDTO dto) {
        AuthorityListDTO updated = authorityService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping(path = "id/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable int id) {
        authorityService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
