package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.dto.region.RegionCreateDTO;
import com.devekoc.camerAtlas.entities.Region;
import com.devekoc.camerAtlas.repositories.RegionRepository;
import com.devekoc.camerAtlas.repositories.DepartementRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
@org.springframework.test.context.ActiveProfiles("test")
class RegionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DepartementRepository departementRepository;

    @Autowired
    private RegionRepository regionRepository;

    @BeforeEach
    void nettoyerLaBase() {
        // On supprime les enfants avant les parents pour respecter les contraintes
        departementRepository.deleteAll();
        regionRepository.deleteAll();
    }

    @DisplayName("POST /region - Succès : Créer une région avec un DTO valide")
    @Test
    void creerRegion_quandLeDtoEstValide_doitRetourner201EtLaRegionCreee() throws Exception {
        RegionCreateDTO dto = new RegionCreateDTO("Est", 109002, 1000000, "coords", "Bertoua", "ES");

        mockMvc.perform(post("/region")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nom").value("Est"))
                .andExpect(jsonPath("$.chefLieu").value("Bertoua"));
    }

    @DisplayName("POST /region - Échec : DTO avec nom vide")
    @Test
    void creerRegion_quandLeNomEstVide_doitRetourner400BadRequest() throws Exception {
        RegionCreateDTO dto = new RegionCreateDTO("", 109002, 1000000, "coords", "Bertoua", "ES");

        mockMvc.perform(post("/region")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("POST /region - Échec : DTO avec superficie vide")
    @Test
    void creerRegion_quandLaSuperficieEstVide_doitRetourner400BadRequest() throws Exception {
        RegionCreateDTO dto = new RegionCreateDTO("Est", null, 1000000, "coords", "Bertoua", "ES");

        mockMvc.perform(post("/region")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("POST /region - Échec : DTO avec population vide")
    @Test
    void creerRegion_quandLaPopulationEstVide_doitRetourner400BadRequest() throws Exception {
        RegionCreateDTO dto = new RegionCreateDTO("Est", 109002, null, "coords", "Bertoua", "ES");

        mockMvc.perform(post("/region")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("POST /region - Échec : DTO avec chef lieu vide")
    @Test
    void creerRegion_quandLeChefLieuEstVide_doitRetourner400BadRequest() throws Exception {
        RegionCreateDTO dto = new RegionCreateDTO("Est", 109002, 203453, "coords", "", "ES");

        mockMvc.perform(post("/region")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("POST /region - Échec : DTO avec code minéralogique incorrect")
    @Test
    void creerRegion_quandLeCodeMineralogiqueEstIncorrect_doitRetourner400BadRequest() throws Exception {
        RegionCreateDTO dto = new RegionCreateDTO("Est", 109002, 203453, "coords", "Bertoua", "ETD");

        mockMvc.perform(post("/region")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("POST /region - Échec : Nom de région déjà existant")
    @Test
    void creerRegion_quandLeNomExisteDeja_doitRetourner409Conflict() throws Exception {
        // Arrange : Insérer une région dans la base de données de test
        Region regionExistante = new Region();
        regionExistante.setNom("Marne");
        regionExistante.setChefLieu("Yaoundé");
        regionExistante.setPopulation(12390);
        regionExistante.setSuperficie(333456);
        regionRepository.save(regionExistante);

        // Act & Assert
        RegionCreateDTO dto = new RegionCreateDTO("Marne", 100, 100, "c", "Y", "CE");
        mockMvc.perform(post("/region")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }
}
