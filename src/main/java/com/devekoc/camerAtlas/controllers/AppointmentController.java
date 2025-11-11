package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.dto.appointment.AppointmentCreateDTO;
import com.devekoc.camerAtlas.dto.appointment.AppointmentListDTO;
import com.devekoc.camerAtlas.services.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "appointment")
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AppointmentListDTO> create(@RequestBody @Valid AppointmentCreateDTO dto){
        AppointmentListDTO created = appointmentService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping(path = "cascade", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<List<AppointmentListDTO>> createSeveral(@RequestBody @Valid List<AppointmentCreateDTO> dtos){
        List<AppointmentListDTO> created = appointmentService.createSeveral(dtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity <List<AppointmentListDTO>> listAll() {
        List<AppointmentListDTO> appointments = appointmentService.listAll();
        return ResponseEntity.ok(appointments);
    }

    @PutMapping(path = "{id}", consumes =  APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity <AppointmentListDTO> update(@PathVariable int id, @RequestBody @Valid AppointmentCreateDTO dto) {
        AppointmentListDTO updated = appointmentService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping(path = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity <Void> delete(@PathVariable int id) {
        appointmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
