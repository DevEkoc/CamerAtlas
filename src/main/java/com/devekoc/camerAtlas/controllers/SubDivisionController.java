//package com.devekoc.camerAtlas.controllers;
//
//import com.devekoc.camerAtlas.dto.division.DivisionCreateDTO;
//import com.devekoc.camerAtlas.dto.division.DivisionListDTO;
//import com.devekoc.camerAtlas.dto.subDivision.SubDivisionCreateDTO;
//import com.devekoc.camerAtlas.dto.subDivision.SubDivisionListDTO;
//import com.devekoc.camerAtlas.services.SubDivisionService;
//import jakarta.validation.Valid;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.IOException;
//import java.util.List;
//
//import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
//import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
//
//@RestController
//@RequestMapping(value = "subDivision")
//public class SubDivisionController {
//    private final SubDivisionService subDivisionService;
//
//    public SubDivisionController(SubDivisionService subDivisionService) {
//        this.subDivisionService = subDivisionService;
//    }
//
//    @PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
//    @ResponseStatus(HttpStatus.CREATED)
//    public ResponseEntity<SubDivisionListDTO> create(@RequestBody @Valid @ModelAttribute SubDivisionCreateDTO dto) throws IOException {
//        SubDivisionListDTO created = subDivisionService.create(dto);
//        return ResponseEntity.status(HttpStatus.CREATED).body(created);
//    }
//
//    @PostMapping(path = "cascade", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
//    @ResponseStatus(HttpStatus.CREATED)
//    public ResponseEntity<List<SubDivisionListDTO>> createSeveral(@RequestBody @Valid List<SubDivisionCreateDTO> dtos) throws IOException {
//        List<SubDivisionListDTO> created = subDivisionService.createSeveral(dtos);
//        return ResponseEntity.status(HttpStatus.CREATED).body(created);
//    }
//
//    @GetMapping(produces =  APPLICATION_JSON_VALUE)
//    public ResponseEntity <List<SubDivisionListDTO>> listAll() {
//        List<SubDivisionListDTO> subDivisions = subDivisionService.listAll();
//        return ResponseEntity.ok(subDivisions);
//    }
//
//    @GetMapping(path = "id/{id}", produces =  APPLICATION_JSON_VALUE)
//    public ResponseEntity <SubDivisionListDTO> find(@PathVariable int id) {
//        SubDivisionListDTO subDivision = subDivisionService.find(id);
//        return (subDivision != null)
//                ? ResponseEntity.ok(subDivision)
//                : ResponseEntity.notFound().build()
//        ;
//    }
//
//    @GetMapping(path = "name/{name}", produces =  APPLICATION_JSON_VALUE)
//    public ResponseEntity <SubDivisionListDTO> find(@PathVariable String name) {
//        SubDivisionListDTO subDivision = subDivisionService.find(name);
//        return (subDivision != null)
//                ? ResponseEntity.ok(subDivision)
//                : ResponseEntity.notFound().build()
//        ;
//    }
//
//    @PutMapping(path = "{id}", consumes = MULTIPART_FORM_DATA_VALUE)
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public ResponseEntity <SubDivisionListDTO> update(@PathVariable int id, @RequestBody @Valid @ModelAttribute SubDivisionCreateDTO dto) throws IOException {
//        SubDivisionListDTO updated = subDivisionService.update(id, dto);
//        return ResponseEntity.ok(updated);
//    }
//
//    @DeleteMapping(path = "id/{id}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public ResponseEntity <Void> delete(@PathVariable int id){
//        subDivisionService.delete(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    @DeleteMapping(path = "name/{name}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public ResponseEntity <Void> delete(@PathVariable String name){
//        subDivisionService.delete(name);
//        return ResponseEntity.noContent().build();
//    }
//}
