package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.dto.delimitation.DelimitationCreateDTO;
import com.devekoc.camerAtlas.entities.Arrondissement;
import com.devekoc.camerAtlas.entities.Delimitation;
import com.devekoc.camerAtlas.entities.Departement;
import com.devekoc.camerAtlas.entities.Frontiere;
import com.devekoc.camerAtlas.entities.Region;
import com.devekoc.camerAtlas.enumerations.TypeFrontiere;
import com.devekoc.camerAtlas.repositories.ArrondissementRepository;
import com.devekoc.camerAtlas.repositories.DelimitationRepository;
import com.devekoc.camerAtlas.repositories.DepartementRepository;
import com.devekoc.camerAtlas.repositories.FrontiereRepository;
import com.devekoc.camerAtlas.repositories.RegionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class DelimitationControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DelimitationRepository delimitationRepository;
    @Autowired
    private FrontiereRepository frontiereRepository;
    @Autowired
    private RegionRepository regionRepository;
    @Autowired
    private DepartementRepository departementRepository;
    @Autowired
    private ArrondissementRepository arrondissementRepository;

    private Frontiere frontiereTest;
    private Arrondissement arrondissementTest;

    @BeforeEach
    void setUp() {
        // Nettoyage dans l'ordre inverse des dépendances
        delimitationRepository.deleteAll();
        arrondissementRepository.deleteAll();
        departementRepository.deleteAll();
        regionRepository.deleteAll();
        frontiereRepository.deleteAll();

        // Création des entités parentes nécessaires pour les tests
        frontiereTest = frontiereRepository.save(new Frontiere(null, TypeFrontiere.OUEST, "Fleuve Nkam"));

        Region reg = new Region();
        reg.setNom("Littoral"); reg.setCodeMineralogique("LT"); reg.setChefLieu("Douala"); reg.setSuperficie(123); reg.setPopulation(321);
        Region region = regionRepository.save(reg);

        Departement dpt = new Departement();
        dpt.setNom("Wouri"); dpt.setPrefecture("Douala"); dpt.setCoordonnees("coords"); dpt.setPopulation(123); dpt.setSuperficie(321);
        dpt.setRegion(region);
        Departement departement = departementRepository.save(dpt);

        Arrondissement arr = new Arrondissement();
        arr.setNom("Douala 1er"); arr.setSousPrefecture("Bonanjo"); arr.setDepartement(departement); arr.setPopulation(321); arr.setSuperficie(321);
        arr.setCoordonnees("coords");
        arrondissementTest = arrondissementRepository.save(arr);
    }

    @Test
    void doit_creer_une_delimitation_quand_les_donnees_sont_valides() throws Exception {
        DelimitationCreateDTO dto = new DelimitationCreateDTO(
                arrondissementTest.getId(),
                frontiereTest.getId(),
                arrondissementTest.getNom(),
                frontiereTest.getType().name()
        );

        mockMvc.perform(post("/delimitation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nomCirconscription", is(arrondissementTest.getNom())))
                .andExpect(jsonPath("$.limiteFrontiere", is(frontiereTest.getLimite())));

        assertThat(delimitationRepository.count()).isEqualTo(1);
    }

    @Test
    void doit_echouer_si_idFrontiere_est_nul() throws Exception {
        DelimitationCreateDTO dto = new DelimitationCreateDTO(arrondissementTest.getId(), null, "nom", "type");

        mockMvc.perform(post("/delimitation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("idFrontiere : L'ID de la frontière ne doit pas vide")));
    }

    @Test
    void doit_lister_les_delimitations_existantes() throws Exception {
        delimitationRepository.save(new Delimitation(frontiereTest, arrondissementTest));

        mockMvc.perform(get("/delimitation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath(
                        "$[0].nomCirconscription",
                        is(arrondissementTest.getNom())
                )
        );
    }

    @Test
    void doit_supprimer_une_delimitation_existante() throws Exception {
        Delimitation delimitation = delimitationRepository.save(new Delimitation(frontiereTest, arrondissementTest));

        mockMvc.perform(delete("/delimitation/{id}", delimitation.getId()))
                .andExpect(status().isNoContent());

        assertThat(delimitationRepository.findById(delimitation.getId())).isEmpty();
    }

    @Test
    void doit_retourner_not_found_si_id_a_supprimer_n_existe_pas() throws Exception {
        mockMvc.perform(delete("/delimitation/{id}", 999))
                .andExpect(status().isNotFound());
    }
}
