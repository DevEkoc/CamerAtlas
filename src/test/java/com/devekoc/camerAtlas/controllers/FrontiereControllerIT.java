package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.entities.Frontiere;
import com.devekoc.camerAtlas.enumerations.TypeFrontiere;
import com.devekoc.camerAtlas.repositories.FrontiereRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class FrontiereControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FrontiereRepository frontiereRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void nettoyerLaBase() {
        frontiereRepository.deleteAll();
    }

    @Test
    void doit_creer_une_frontiere_valide() throws Exception {
        Frontiere frontiere = new Frontiere(null, TypeFrontiere.NORD, "Frontière avec le Tchad");

        mockMvc.perform(post("/frontiere")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(frontiere)))
                .andExpect(status().isCreated());

        assertThat(frontiereRepository.count()).isEqualTo(1);
        assertThat(frontiereRepository.findAll().get(0).getLimite()).isEqualTo("Frontière avec le Tchad");
    }

    @Test
    void doit_echouer_si_type_est_nul() throws Exception {
        Frontiere frontiere = new Frontiere(null, null, "Limite sans type");

        mockMvc.perform(post("/frontiere")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(frontiere)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("type : Le type de frontière ne peut pas être vide !"));
    }

    @Test
    void doit_echouer_si_limite_est_vide() throws Exception {
        Frontiere frontiere = new Frontiere(null, TypeFrontiere.SUD, " ");

        mockMvc.perform(post("/frontiere")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(frontiere)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("limite : Le nom de la limite ne peut pas être vide !"));
    }

    @Test
    void doit_lister_les_frontieres_existantes() throws Exception {
        frontiereRepository.save(new Frontiere(null, TypeFrontiere.EST, "Frontière Est"));
        frontiereRepository.save(new Frontiere(null, TypeFrontiere.OUEST, "Frontière Ouest"));

        mockMvc.perform(get("/frontiere"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].type").value(TypeFrontiere.EST.name()));
    }

    @Test
    void doit_rechercher_des_frontieres_par_type() throws Exception {
        frontiereRepository.save(new Frontiere(null, TypeFrontiere.SUD, "Frontière Sud 1"));
        frontiereRepository.save(new Frontiere(null, TypeFrontiere.NORD, "Frontière Nord"));
        frontiereRepository.save(new Frontiere(null, TypeFrontiere.SUD, "Frontière Sud 2"));

        mockMvc.perform(get("/frontiere").param("type", "SUD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].type", is("SUD")))
                .andExpect(jsonPath("$[1].type", is("SUD")));
    }
}
