package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.dto.delimitation.DelimitationCreateDTO;
import com.devekoc.camerAtlas.dto.delimitation.DelimitationListDTO;
import com.devekoc.camerAtlas.enumerations.BorderType;
import com.devekoc.camerAtlas.security.JwtFilter;
import com.devekoc.camerAtlas.services.DelimitationService;
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

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DelimitationController.class)
@AutoConfigureMockMvc(addFilters = false) // Désactive totalement Spring Security
class DelimitationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    private DelimitationService delimitationService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtFilter jwtFilter;

    DelimitationListDTO response;

    @BeforeEach
    void setup() {
        response = new DelimitationListDTO(
                1,
                10,
                "Biyem-Assi",
                BorderType.NORD,
                "Frontière Nord"
        );
    }

    // --------------------------------------------------------------------
    // CREATE
    // --------------------------------------------------------------------
    @Test
    void create_shouldReturn201() throws Exception {

        DelimitationCreateDTO dto = new DelimitationCreateDTO(
                10,
                BorderType.NORD,
                "Frontière Nord"
        );

        when(delimitationService.create(any())).thenReturn(response);

        mockMvc.perform(post("/delimitations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.delimitationId").value(1));

        verify(delimitationService).create(any());
    }

    // --------------------------------------------------------------------
    // CREATE SEVERAL
    // --------------------------------------------------------------------
    @Test
    void createSeveral_shouldReturn201() throws Exception {

        DelimitationCreateDTO dto = new DelimitationCreateDTO(
                10,
                BorderType.SUD,
                "Frontière Sud"
        );

        when(delimitationService.createSeveral(any()))
                .thenReturn(List.of(response));

        mockMvc.perform(post("/delimitations/cascade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(dto))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].delimitationId").value(1));

        verify(delimitationService).createSeveral(any());
    }

    // --------------------------------------------------------------------
    // LIST ALL
    // --------------------------------------------------------------------
    @Test
    void listAll_shouldReturn200() throws Exception {

        when(delimitationService.listAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/delimitations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].borderType").value("NORD"));
    }

    // --------------------------------------------------------------------
    // DELETE
    // --------------------------------------------------------------------
    @Test
    void delete_shouldReturn204() throws Exception {

        doNothing().when(delimitationService).delete(1);

        mockMvc.perform(delete("/delimitations/id/1"))
                .andExpect(status().isNoContent());

        verify(delimitationService).delete(1);
    }
}
