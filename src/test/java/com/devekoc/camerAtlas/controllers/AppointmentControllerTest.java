package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.dto.appointment.AppointmentCreateDTO;
import com.devekoc.camerAtlas.dto.appointment.AppointmentListDTO;
import com.devekoc.camerAtlas.enumerations.Function;
import com.devekoc.camerAtlas.security.JwtFilter;
import com.devekoc.camerAtlas.services.AppointmentService;
import com.devekoc.camerAtlas.services.UserService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AppointmentController.class)
@AutoConfigureMockMvc(addFilters = false) // Désactive totalement Spring Security
class AppointmentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    private AppointmentService appointmentService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtFilter jwtFilter;

    AppointmentListDTO response;

    @BeforeEach
    void setup() {
        response = new AppointmentListDTO(
                1,
                10,
                20,
                Function.PREFET,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2026, 1, 1)
        );
    }

    // --------------------------------------------------------------------
    // CREATE
    // --------------------------------------------------------------------
    @Test
    void create_shouldReturn201() throws Exception {

        AppointmentCreateDTO dto = new AppointmentCreateDTO(
                10,
                20,
                Function.PREFET,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2026, 1, 1)
        );

        when(appointmentService.create(any())).thenReturn(response);

        mockMvc.perform(post("/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.function").value("PREFET"));

        verify(appointmentService).create(any());
    }

    // --------------------------------------------------------------------
    // CREATE SEVERAL
    // --------------------------------------------------------------------
    @Test
    void createSeveral_shouldReturn201() throws Exception {

        AppointmentCreateDTO dto = new AppointmentCreateDTO(
                10,
                20,
                Function.PREFET,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2026, 1, 1)
        );

        when(appointmentService.createSeveral(any())).thenReturn(List.of(response));

        mockMvc.perform(post("/appointments/cascade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(dto))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].appointmentId").value(1));

        verify(appointmentService).createSeveral(any());
    }

    // --------------------------------------------------------------------
    // LIST ALL
    // --------------------------------------------------------------------
    @Test
    void listAll_shouldReturn200() throws Exception {

        when(appointmentService.listAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/appointments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].authorityId").value(10));
    }

    // --------------------------------------------------------------------
    // UPDATE
    // --------------------------------------------------------------------
    @Test
    void update_shouldReturn200() throws Exception {

        AppointmentCreateDTO dto = new AppointmentCreateDTO(
                10,
                20,
                Function.SOUS_PREFET,
                LocalDate.of(2025, 2, 1),
                LocalDate.of(2026, 2, 1)
        );

        when(appointmentService.update(eq(1), any())).thenReturn(response);

        mockMvc.perform(put("/appointments/id/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appointmentId").value(1));

        verify(appointmentService).update(eq(1), any());
    }

    // --------------------------------------------------------------------
    // DELETE
    // --------------------------------------------------------------------
    @Test
    void delete_shouldReturn204() throws Exception {
        doNothing().when(appointmentService).delete(1);

        mockMvc.perform(delete("/appointments/id/1"))
                .andExpect(status().isNoContent());

        verify(appointmentService).delete(1);
    }
}

