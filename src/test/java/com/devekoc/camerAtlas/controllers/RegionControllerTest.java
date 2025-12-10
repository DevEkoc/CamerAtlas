package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.dto.region.RegionCreateDTO;
import com.devekoc.camerAtlas.dto.region.RegionListDTO;
import com.devekoc.camerAtlas.dto.region.RegionListWithDivisionsDTO;
import com.devekoc.camerAtlas.security.JwtFilter;
import com.devekoc.camerAtlas.services.RegionService;
import com.devekoc.camerAtlas.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.http.MediaType;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RegionController.class)
@AutoConfigureMockMvc(addFilters = false) // Désactive totalement Spring Security
class RegionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RegionService regionService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtFilter jwtFilter;

    // -----------------------------------------------------
    // CREATE
    // -----------------------------------------------------

    @Test
    @DisplayName("POST /regions → doit créer une région et retourner 201")
    void create_shouldReturn201() throws Exception {

        RegionCreateDTO dto = new RegionCreateDTO(
                "Centre", 10000, 4000000, "0,0", "Yaoundé", "CE", null
        );

        RegionListDTO returned = new RegionListDTO(
                1, "Centre", 10000, 4000000, "0,0", "Yaoundé", "CE", null, null, null
        );

        when(regionService.create(any())).thenReturn(returned);

        mockMvc.perform(multipart("/regions")
                        .file("image", "fake".getBytes())
                        .param("name", dto.name())
                        .param("surface", dto.surface().toString())
                        .param("population", dto.population().toString())
                        .param("gpsCoordinates", dto.gpsCoordinates())
                        .param("capital", dto.capital())
                        .param("mineralogicalCode", dto.mineralogicalCode())
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Centre"));

        verify(regionService).create(any());
    }

    // -----------------------------------------------------
    // CREATE SEVERAL
    // -----------------------------------------------------

    @Test
    @DisplayName("POST /regions/cascade → doit créer plusieurs régions")
    void createSeveral_shouldReturn201() throws Exception {
        List<RegionCreateDTO> dtos = List.of(
                new RegionCreateDTO("Centre", 10000, 4000000, "0,0", "Yaoundé", "CE", null),
                new RegionCreateDTO("Littoral", 5000, 3000000, "1,1", "Douala", "LT", null)
        );

        List<RegionListDTO> returned = List.of(
                new RegionListDTO(1, "Centre", 10000, 4000000, "0,0", "Yaoundé", "CE", null, null, null),
                new RegionListDTO(2, "Littoral", 5000, 3000000, "1,1", "Douala", "LT", null, null, null)
        );

        when(regionService.createSeveral(any())).thenReturn(returned);

        mockMvc.perform(post("/regions/cascade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtos)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(regionService).createSeveral(any());
    }

    // -----------------------------------------------------
    // LIST ALL
    // -----------------------------------------------------

    @Test
    @DisplayName("GET /regions → doit lister toutes les régions")
    void listAll_shouldReturn200() throws Exception {
        List<RegionListDTO> list = List.of(
                new RegionListDTO(1, "Centre", 10000, 4000000, "0,0", "Yaoundé", "CE", null, null, null)
        );

        when(regionService.listAll()).thenReturn(list);

        mockMvc.perform(get("/regions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Centre"));

        verify(regionService).listAll();
    }

    // -----------------------------------------------------
    // FIND BY ID
    // -----------------------------------------------------

    @Test
    @DisplayName("GET /regions/{id} → doit retourner 200 si trouvé")
    void findById_shouldReturn200() throws Exception {
        RegionListDTO dto = new RegionListDTO(1, "Centre", 10000, 4000000, "0,0", "Yaoundé", "CE", null, null, null);
        when(regionService.find(1)).thenReturn(dto);

        mockMvc.perform(get("/regions/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Centre"));
    }

    @Test
    @DisplayName("GET /regions/{id} → doit retourner 404 si introuvable")
    void findById_shouldReturn404() throws Exception {
        when(regionService.find(1)).thenReturn(null);

        mockMvc.perform(get("/regions/id/1"))
                .andExpect(status().isNotFound());
    }

    // -----------------------------------------------------
    // FIND BY NAME
    // -----------------------------------------------------

    @Test
    @DisplayName("GET /regions/{name} → doit retourner 200 si trouvé")
    void findByName_shouldReturn200() throws Exception {
        RegionListDTO dto = new RegionListDTO(1, "Centre", 10000, 4000000, "0,0", "Yaoundé", "CE", null, null, null);
        when(regionService.find("Centre")).thenReturn(dto);

        mockMvc.perform(get("/regions/name/Centre"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Centre"));
    }

    @Test
    @DisplayName("GET /regions/{name} → doit retourner 404 si introuvable")
    void findByName_shouldReturn404() throws Exception {
        when(regionService.find("Ghost")).thenReturn(null);

        mockMvc.perform(get("/regions/name/Ghost"))
                .andExpect(status().isNotFound());
    }

    // -----------------------------------------------------
    // FIND WITH DIVISIONS
    // -----------------------------------------------------

    @Test
    @DisplayName("GET /regions/{id}/divisions → doit retourner 200")
    void findWithDivisions_shouldReturn200() throws Exception {

        RegionListWithDivisionsDTO dto =
                new RegionListWithDivisionsDTO(1, "Centre", 10000, 4000000, "0,0", "Yaoundé", "CE", null, null, null, List.of());

        when(regionService.findWithDivisions(1)).thenReturn(dto);

        mockMvc.perform(get("/regions/1/divisions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Centre"));

        verify(regionService).findWithDivisions(1);
    }

    // -----------------------------------------------------
    // UPDATE
    // -----------------------------------------------------

    @Test
    @DisplayName("PUT /regions/{id} → doit mettre à jour la région")
    void update_shouldReturn200() throws Exception {

        RegionCreateDTO dto = new RegionCreateDTO(
                "Centre", 10000, 4000000, "0,0", "Yaoundé", "CE", null
        );

        RegionListDTO updated = new RegionListDTO(
                1, "Centre", 10000, 4000000, "0,0", "Yaoundé", "CE", null, null, null
        );

        when(regionService.update(eq(1), any())).thenReturn(updated);

        mockMvc.perform(multipart("/regions/id/1")
                        .file("image", "mock".getBytes())
                        .param("name", dto.name())
                        .param("surface", dto.surface().toString())
                        .param("population", dto.population().toString())
                        .param("gpsCoordinates", dto.gpsCoordinates())
                        .param("capital", dto.capital())
                        .param("mineralogicalCode", dto.mineralogicalCode())
                        .with(request -> { request.setMethod("PUT"); return request; })
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(regionService).update(eq(1), any());
    }

    // -----------------------------------------------------
    // DELETE
    // -----------------------------------------------------

    @Test
    @DisplayName("DELETE /regions/{id} → doit supprimer la région")
    void deleteById_shouldReturn204() throws Exception {

        mockMvc.perform(delete("/regions/id/1"))
                .andExpect(status().isNoContent());

        verify(regionService).delete(1);
    }

    @Test
    @DisplayName("DELETE /regions/{name} → doit supprimer par nom")
    void deleteByName_shouldReturn204() throws Exception {

        mockMvc.perform(delete("/regions/name/Centre"))
                .andExpect(status().isNoContent());

        verify(regionService).delete("Centre");
    }
}

