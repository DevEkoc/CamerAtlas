package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.dto.api.AuthorityDetailsDTO;
import com.devekoc.camerAtlas.dto.neighborhood.NeighborhoodListDTO;
import com.devekoc.camerAtlas.dto.subDivision.SubDivisionCreateDTO;
import com.devekoc.camerAtlas.dto.subDivision.SubDivisionListDTO;
import com.devekoc.camerAtlas.dto.subDivision.SubDivisionWithNeighborhoodsDTO;
import com.devekoc.camerAtlas.security.JwtFilter;
import com.devekoc.camerAtlas.services.SubDivisionService;
import com.devekoc.camerAtlas.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SubDivisionController.class)
@AutoConfigureMockMvc(addFilters = false) // Désactive totalement Spring Security
class SubDivisionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    SubDivisionService subDivisionService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtFilter jwtFilter;

    SubDivisionListDTO response;

    @BeforeEach
    void setup() {
        response = new SubDivisionListDTO(
                1,
                "Biyem-Assi",
                3000,
                800000,
                "3.88,11.52",
                "Sous-préfecture Biyem",
                10,
                "Mfoundi",
                "img.png",
                new AuthorityDetailsDTO(
                        "John",
                        "Doe",
                        LocalDate.of(1989, 12, 20),
                        "SOUS_PREFET",
                        LocalDate.of(2000, 12, 20),
                        null
                ),
                List.of()
        );
    }

    // ---------------------------------------------------------
    // CREATE
    // ---------------------------------------------------------
    @Test
    void create_shouldReturn201() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "test.png",
                "image/png",
                "bytes".getBytes()
        );

        when(subDivisionService.create(any())).thenReturn(response);

        mockMvc.perform(multipart("/subDivisions")
                        .file(file)
                        .param("name", "Biyem-Assi")
                        .param("surface", "3000")
                        .param("population", "800000")
                        .param("gpsCoordinates", "3.88,11.52")
                        .param("subDivisionalOffice", "Sous-préfecture Biyem")
                        .param("divisionId", "10")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Biyem-Assi"));

        verify(subDivisionService).create(any());
    }

    // ---------------------------------------------------------
    // CREATE SEVERAL
    // ---------------------------------------------------------
    @Test
    void createSeveral_shouldReturn201() throws Exception {
        SubDivisionCreateDTO dto = new SubDivisionCreateDTO(
                "Biyem-Assi",
                3000,
                800000,
                "3.88,11.52",
                "Sous-préfecture Biyem",
                10,
                null
        );

        when(subDivisionService.createSeveral(any())).thenReturn(List.of(response));

        mockMvc.perform(post("/subDivisions/cascade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(dto))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].name").value("Biyem-Assi"));

        verify(subDivisionService).createSeveral(any());
    }

    // ---------------------------------------------------------
    // LIST ALL
    // ---------------------------------------------------------
    @Test
    void listAll_shouldReturn200() throws Exception {
        when(subDivisionService.listAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/subDivisions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Biyem-Assi"));
    }

    // ---------------------------------------------------------
    // FIND BY ID
    // ---------------------------------------------------------
    @Test
    void findById_shouldReturn200() throws Exception {
        when(subDivisionService.find(1)).thenReturn(response);

        mockMvc.perform(get("/subDivisions/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void findById_shouldReturn404() throws Exception {
        when(subDivisionService.find(99)).thenReturn(null);

        mockMvc.perform(get("/subDivisions/id/99"))
                .andExpect(status().isNotFound());
    }

    // ---------------------------------------------------------
    // FIND BY NAME
    // ---------------------------------------------------------
    @Test
    void findByName_shouldReturn200() throws Exception {
        when(subDivisionService.find("Biyem-Assi")).thenReturn(response);

        mockMvc.perform(get("/subDivisions/name/Biyem-Assi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Biyem-Assi"));
    }

    @Test
    void findByName_shouldReturn404() throws Exception {
        when(subDivisionService.find("Unknown")).thenReturn(null);

        mockMvc.perform(get("/subDivisions/name/Unknown"))
                .andExpect(status().isNotFound());
    }

    // ---------------------------------------------------------
    // FIND WITH NEIGHBORHOODS
    // ---------------------------------------------------------
    @Test
    void findWithNeighborhoods_shouldReturn200() throws Exception {
        SubDivisionWithNeighborhoodsDTO dto = new SubDivisionWithNeighborhoodsDTO(
                response.id(),
                response.name(),
                response.surface(),
                response.population(),
                response.gpsCoordinates(),
                response.subDivisionalOffice(),
                response.divisionId(),
                response.divisionName(),
                response.imgUrl(),
                response.subDivisionalOfficer(),
                null,
                List.of(
                        new NeighborhoodListDTO(
                                1,
                                "Melen",
                                "12000",
                                1,
                                "Biyem-Assi"
                        )
                )
        );

        when(subDivisionService.findWithNeighborhoods(1)).thenReturn(dto);

        mockMvc.perform(get("/subDivisions/1/neighborhoods"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.neighborhoods[0].name").value("Melen"));
    }

    @Test
    void findWithNeighborhoods_shouldReturn404() throws Exception {
        when(subDivisionService.findWithNeighborhoods(99)).thenReturn(null);

        mockMvc.perform(get("/subDivisions/99/neighborhoods"))
                .andExpect(status().isNotFound());
    }

    // ---------------------------------------------------------
    // UPDATE
    // ---------------------------------------------------------
    @Test
    void update_shouldReturn200() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "test.png",
                "image/png",
                "bytes".getBytes()
        );

        SubDivisionCreateDTO dto = new SubDivisionCreateDTO(
                "Biyem-Assi New",
                3200,
                850000,
                "3.90,11.55",
                "Sous-préf. Biyem",
                10,
                null
        );

        when(subDivisionService.update(eq(1), any())).thenReturn(response);

        mockMvc.perform(multipart("/subDivisions/id/1")
                        .file(file)
                        .param("name", dto.name())
                        .param("surface", dto.surface().toString())
                        .param("population", dto.population().toString())
                        .param("gpsCoordinates", dto.gpsCoordinates())
                        .param("subDivisionalOffice", dto.subDivisionalOffice())
                        .param("divisionId", dto.divisionId().toString())
                        .with(request -> { request.setMethod("PUT"); return request; })
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(subDivisionService).update(eq(1), any());
    }

    // ---------------------------------------------------------
    // DELETE BY ID
    // ---------------------------------------------------------
    @Test
    void deleteById_shouldReturn204() throws Exception {
        doNothing().when(subDivisionService).delete(1);

        mockMvc.perform(delete("/subDivisions/id/1"))
                .andExpect(status().isNoContent());

        verify(subDivisionService).delete(1);
    }

    // ---------------------------------------------------------
    // DELETE BY NAME
    // ---------------------------------------------------------
    @Test
    void deleteByName_shouldReturn204() throws Exception {
        doNothing().when(subDivisionService).delete("Biyem-Assi");

        mockMvc.perform(delete("/subDivisions/name/Biyem-Assi"))
                .andExpect(status().isNoContent());

        verify(subDivisionService).delete("Biyem-Assi");
    }
}

