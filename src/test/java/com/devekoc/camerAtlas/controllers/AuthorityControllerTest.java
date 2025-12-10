package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.dto.authority.AuthorityCreateDTO;
import com.devekoc.camerAtlas.dto.authority.AuthorityListDTO;
import com.devekoc.camerAtlas.security.JwtFilter;
import com.devekoc.camerAtlas.services.AuthorityService;
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

@WebMvcTest(controllers = AuthorityController.class)
@AutoConfigureMockMvc(addFilters = false) // Désactive totalement Spring Security
class AuthorityControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    private AuthorityService authorityService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtFilter jwtFilter;

    AuthorityListDTO response;

    @BeforeEach
    void setup() {
        response = new AuthorityListDTO(
                1,
                "Paul",
                "Biya",
                LocalDate.of(1933, 2, 13)
        );
    }

    // --------------------------------------------------------------------
    // CREATE
    // --------------------------------------------------------------------
    @Test
    void create_shouldReturn201() throws Exception {

        AuthorityCreateDTO dto = new AuthorityCreateDTO(
                "Paul",
                "Biya",
                LocalDate.of(1933, 2, 13)
        );

        when(authorityService.create(any())).thenReturn(response);

        mockMvc.perform(post("/authorities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Paul"));

        verify(authorityService).create(any());
    }

    // --------------------------------------------------------------------
    // CREATE SEVERAL
    // --------------------------------------------------------------------
    @Test
    void createSeveral_shouldReturn201() throws Exception {
        AuthorityCreateDTO dto = new AuthorityCreateDTO(
                "Paul",
                "Biya",
                LocalDate.of(1933, 2, 13)
        );

        when(authorityService.createSeveral(any())).thenReturn(List.of(response));

        mockMvc.perform(post("/authorities/cascade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(dto))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].surname").value("Biya"));

        verify(authorityService).createSeveral(any());
    }

    // --------------------------------------------------------------------
    // LIST ALL
    // --------------------------------------------------------------------
    @Test
    void listAll_shouldReturn200() throws Exception {

        when(authorityService.listAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/authorities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Paul"));
    }

    // --------------------------------------------------------------------
    // FIND BY ID
    // --------------------------------------------------------------------
    @Test
    void findById_shouldReturn200() throws Exception {
        when(authorityService.find(1)).thenReturn(response);

        mockMvc.perform(get("/authorities/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.surname").value("Biya"));
    }

    @Test
    void findById_shouldReturn404() throws Exception {
        when(authorityService.find(99)).thenReturn(null);

        mockMvc.perform(get("/authorities/id/99"))
                .andExpect(status().isNotFound());
    }

    // --------------------------------------------------------------------
    // UPDATE
    // --------------------------------------------------------------------
    @Test
    void update_shouldReturn200() throws Exception {
        AuthorityCreateDTO dto = new AuthorityCreateDTO(
                "Paul",
                "Updated",
                LocalDate.of(1933, 2, 13)
        );

        when(authorityService.update(eq(1), any())).thenReturn(response);

        mockMvc.perform(put("/authorities/id/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(authorityService).update(eq(1), any());
    }

    // --------------------------------------------------------------------
    // DELETE
    // --------------------------------------------------------------------
    @Test
    void delete_shouldReturn204() throws Exception {
        doNothing().when(authorityService).delete(1);

        mockMvc.perform(delete("/authorities/id/1"))
                .andExpect(status().isNoContent());

        verify(authorityService).delete(1);
    }
}

