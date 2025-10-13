package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.entities.Autorite;
import com.devekoc.camerAtlas.repositories.AutoriteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
class AutoriteControllerIT {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private AutoriteRepository autoriteRepository;

    private ObjectMapper objectMapper;

    private Autorite autorite1;
    private Autorite autorite2;

    @BeforeEach
    void setUp() {
        autoriteRepository.deleteAll();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        autorite1 = new Autorite();
        autorite1.setNom("Ngannou");
        autorite1.setPrenom("Francis");
        autorite1.setDateNaissance(LocalDate.of(1986, 9, 5));

        autorite2 = new Autorite();
        autorite2.setNom("Eto'o");
        autorite2.setPrenom("Samuel");
        autorite2.setDateNaissance(LocalDate.of(1981, 3, 10));
    }

    @Test
    void testCreerAutorite_shouldCreateAutoriteInDB() throws Exception {
        mvc.perform(post("/api/autorite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(autorite1)))
                .andExpect(status().isCreated());

        List<Autorite> autorites = autoriteRepository.findAll();
        assertThat(autorites).hasSize(1);
        assertThat(autorites.get(0).getNom()).isEqualTo("Ngannou");
    }

    @Test
    void testListerAutorites_shouldReturnListOfAutorites() throws Exception {
        autoriteRepository.save(autorite1);
        autoriteRepository.save(autorite2);

        mvc.perform(get("/api/autorite"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nom", is(autorite1.getNom())))
                .andExpect(jsonPath("$[1].nom", is(autorite2.getNom())));
    }

    @Test
    void testRechercherAutoriteById_shouldReturnAutorite() throws Exception {
        Autorite savedAutorite = autoriteRepository.save(autorite1);
        Integer autoriteId = savedAutorite.getId();

        mvc.perform(get("/api/autorite/{id}", autoriteId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(autoriteId)))
                .andExpect(jsonPath("$.nom", is(autorite1.getNom())))
                .andExpect(jsonPath("$.dateNaissance", is(autorite1.getDateNaissance().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))));
    }

    @Test
    void testRechercherAutoriteById_shouldReturnNotFound() throws Exception {
        mvc.perform(get("/api/autorite/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    void testModifierAutorite_shouldUpdateAutoriteInDB() throws Exception {
        Autorite savedAutorite = autoriteRepository.save(autorite1);
        Integer autoriteId = savedAutorite.getId();

        Autorite updatedInfo = new Autorite();
        updatedInfo.setId(autoriteId);
        updatedInfo.setNom("Le Lion Indomptable");
        updatedInfo.setPrenom("Francis");
        updatedInfo.setDateNaissance(LocalDate.of(1986, 9, 5));

        mvc.perform(put("/api/autorite/{id}", autoriteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedInfo)))
                .andExpect(status().isNoContent());

        Autorite updatedAutorite = autoriteRepository.findById(autoriteId).orElseThrow();
        assertThat(updatedAutorite.getNom()).isEqualTo("Le Lion Indomptable");
    }

    @Test
    void testModifierAutorite_shouldReturnNotFound() throws Exception {
        mvc.perform(put("/api/autorite/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(autorite1)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSupprimerAutorite_shouldDeleteAutoriteFromDB() throws Exception {
        Autorite savedAutorite = autoriteRepository.save(autorite1);
        Integer autoriteId = savedAutorite.getId();

        mvc.perform(delete("/api/autorite/{id}", autoriteId))
                .andExpect(status().isNoContent());

        assertThat(autoriteRepository.findById(autoriteId)).isEmpty();
    }

    @Test
    void testSupprimerAutorite_shouldReturnNotFound() throws Exception {
        mvc.perform(delete("/api/autorite/{id}", 999))
                .andExpect(status().isNotFound());
    }
}
