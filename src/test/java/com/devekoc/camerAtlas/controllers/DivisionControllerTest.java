package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.security.JwtFilter;
import com.devekoc.camerAtlas.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.devekoc.camerAtlas.dto.division.DivisionCreateDTO;
import com.devekoc.camerAtlas.dto.division.DivisionListDTO;
import com.devekoc.camerAtlas.dto.division.DivisionWithSubDivisionsDTO;
import com.devekoc.camerAtlas.services.DivisionService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(controllers = DivisionController.class)
@AutoConfigureMockMvc(addFilters = false) // Désactive totalement Spring Security
class DivisionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DivisionService divisionService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtFilter jwtFilter;

    private DivisionListDTO dtoResponse;

    @BeforeEach
    void setup() {
        dtoResponse = new DivisionListDTO(
                1,
                "Mfoundi",
                5000,
                1200000,
                "3.85,11.50",
                "Yaoundé",
                1,
                "Centre",
                null,
                null,
                null
        );
    }

    // ----------------------------------------------------------------------
    // CREATE
    // ----------------------------------------------------------------------
    @Test
    void create_shouldReturn201() throws Exception {
        DivisionCreateDTO dto = new DivisionCreateDTO(
                "Mfoundi",
                5000,
                1200000,
                "3.85,11.50",
                "Yaoundé",
                1,
                null
        );

        when(divisionService.create(any())).thenReturn(dtoResponse);

        mockMvc.perform(multipart("/divisions")
                        .file("file", "dummy".getBytes())  // si tu as un fichier
                        .param("name", dto.name())
                        .param("surface", dto.surface().toString())
                        .param("population", dto.population().toString())
                        .param("divisionalOffice", dto.divisionalOffice())
                        .param("gpsCoordinates", dto.gpsCoordinates())
                        .param("regionId", dto.regionId().toString())
                        .with(request -> { request.setMethod("POST"); return request; })
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Mfoundi"));

        verify(divisionService).create(any());
    }

    // ----------------------------------------------------------------------
    // CREATE SEVERAL
    // ----------------------------------------------------------------------
    @Test
    void createSeveral_shouldReturn201() throws Exception {
        List<DivisionListDTO> list = List.of(dtoResponse);

        DivisionCreateDTO dto = new DivisionCreateDTO(
                "Mfoundi",
                5000,
                1200000,
                "3.85,11.50",
                "Yaoundé",
                1,
                null
        );

        when(divisionService.createSeveral(any())).thenReturn(list);

        mockMvc.perform(post("/divisions/cascade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(dto))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].name").value("Mfoundi"));

        verify(divisionService).createSeveral(any());
    }

    // ----------------------------------------------------------------------
    // LIST ALL
    // ----------------------------------------------------------------------
    @Test
    void listAll_shouldReturn200() throws Exception {
        when(divisionService.listAll()).thenReturn(List.of(dtoResponse));

        mockMvc.perform(get("/divisions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Mfoundi"));
    }

    // ----------------------------------------------------------------------
    // FIND BY ID
    // ----------------------------------------------------------------------
    @Test
    void findById_shouldReturn200() throws Exception {
        when(divisionService.find(1)).thenReturn(dtoResponse);

        mockMvc.perform(get("/divisions/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void findById_shouldReturn404() throws Exception {
        when(divisionService.find(99)).thenReturn(null);

        mockMvc.perform(get("/divisions/id/99"))
                .andExpect(status().isNotFound());
    }

    // ----------------------------------------------------------------------
    // FIND BY NAME
    // ----------------------------------------------------------------------
    @Test
    void findByName_shouldReturn200() throws Exception {
        when(divisionService.find("Mfoundi")).thenReturn(dtoResponse);

        mockMvc.perform(get("/divisions/name/Mfoundi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Mfoundi"));
    }

    @Test
    void findByName_shouldReturn404() throws Exception {
        when(divisionService.find("Unknown")).thenReturn(null);

        mockMvc.perform(get("/divisions/name/Unknown"))
                .andExpect(status().isNotFound());
    }

    // ----------------------------------------------------------------------
    // FIND WITH SUBDIVISIONS
    // ----------------------------------------------------------------------
    @Test
    void findWithSubDivisions_shouldReturn200() throws Exception {
        DivisionWithSubDivisionsDTO response = new DivisionWithSubDivisionsDTO(
                1,
                "Mfoundi",
                5000,
                1200000,
                "3.85,11.50",
                "Yaoundé",
                1,
                "Centre",
                null,
                null,
                List.of(),
                List.of()
        );

        when(divisionService.findWithSubDivisions(1)).thenReturn(response);

        mockMvc.perform(get("/divisions/1/subDivisions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Mfoundi"));
    }

    @Test
    void findWithSubDivisions_shouldReturn404() throws Exception {
        when(divisionService.findWithSubDivisions(99)).thenReturn(null);

        mockMvc.perform(get("/divisions/99/subDivisions"))
                .andExpect(status().isNotFound());
    }

    // ----------------------------------------------------------------------
    // UPDATE
    // ----------------------------------------------------------------------
    @Test
    void update_shouldReturn200() throws Exception {
        DivisionCreateDTO dto = new DivisionCreateDTO(
                "Mfoundi New",
                6000,
                1500000,
                "3.90,11.60",
                "Yaoundé",
                1,
                null
        );

        when(divisionService.update(eq(1), any())).thenReturn(dtoResponse);

        mockMvc.perform(multipart("/divisions/id/1")
                        .file("file", "dummy".getBytes())
                        .param("name", dto.name())
                        .param("surface", dto.surface().toString())
                        .param("population", dto.population().toString())
                        .param("divisionalOffice", dto.divisionalOffice())
                        .param("gpsCoordinates", dto.gpsCoordinates())
                        .param("regionId", dto.regionId().toString())
                        .with(request -> { request.setMethod("PUT"); return request; })
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(divisionService).update(eq(1), any());
    }

    // ----------------------------------------------------------------------
    // DELETE BY ID
    // ----------------------------------------------------------------------
    @Test
    void deleteById_shouldReturn204() throws Exception {
        doNothing().when(divisionService).delete(1);

        mockMvc.perform(delete("/divisions/id/1"))
                .andExpect(status().isNoContent());

        verify(divisionService).delete(1);
    }

    // ----------------------------------------------------------------------
    // DELETE BY NAME
    // ----------------------------------------------------------------------
    @Test
    void deleteByName_shouldReturn204() throws Exception {
        doNothing().when(divisionService).delete("Mfoundi");

        mockMvc.perform(delete("/divisions/name/Mfoundi"))
                .andExpect(status().isNoContent());

        verify(divisionService).delete("Mfoundi");
    }
}
