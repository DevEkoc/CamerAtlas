package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.dto.affectation.AffectationCreateDTO;
import com.devekoc.camerAtlas.entities.Affectation;
import com.devekoc.camerAtlas.entities.Autorite;
import com.devekoc.camerAtlas.entities.Circonscription;
import com.devekoc.camerAtlas.entities.Region;
import com.devekoc.camerAtlas.enumerations.Fonction;
import com.devekoc.camerAtlas.repositories.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@DisplayName("Tests d'intégration pour AffectationController")
class AffectationControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper; // Spring Boot auto-configures this

    @Autowired
    private AffectationRepository affectationRepository;
    @Autowired
    private AutoriteRepository autoriteRepository;
    @Autowired
    private CirconscriptionRepository circonscriptionRepository;
    @Autowired
    private RegionRepository regionRepository;
    @Autowired
    private DepartementRepository departementRepository;
    @Autowired
    private ArrondissementRepository arrondissementRepository;
    @Autowired
    private QuartierRepository quartierRepository;


    private Autorite autorite1;
    private Autorite autorite2;
    private Circonscription circonscription1;
    private Circonscription circonscription2;

    @BeforeEach
    void setUp() {
        // Register JavaTimeModule if not already registered by Spring
        objectMapper.registerModule(new JavaTimeModule());

        // Nettoyage complet de la base de données
        affectationRepository.deleteAll();
        quartierRepository.deleteAll();
        arrondissementRepository.deleteAll();
        departementRepository.deleteAll();
        regionRepository.deleteAll();
        autoriteRepository.deleteAll();
        circonscriptionRepository.deleteAll();


        // Création des données de test
        autorite1 = autoriteRepository.save(new Autorite(null, "Dupont", "Jean", LocalDate.of(1980, 1, 1)));
        autorite2 = autoriteRepository.save(new Autorite(null, "Durand", "Marie", LocalDate.of(1985, 5, 10)));

        Region region1 = new Region();
        region1.setNom("Région Est");
        region1.setChefLieu("Bertoua");
        region1.setPopulation(1000000);
        region1.setSuperficie(109002);
        circonscription1 = circonscriptionRepository.save(region1);

        Region region2 = new Region();
        region2.setNom("Région Ouest");
        region2.setChefLieu("Bafoussam");
        region2.setPopulation(2000000);
        region2.setSuperficie(13892);
        circonscription2 = circonscriptionRepository.save(region2);
    }

    @Nested
    @DisplayName("Tests pour la création (POST /affectation)")
    class CreationTests {

        @Test
        @DisplayName("Succès : Créer une affectation avec des données valides")
        void creer_quandDonneesValides_doitRetourner201() throws Exception {
            AffectationCreateDTO dto = new AffectationCreateDTO(
                    autorite1.getId(),
                    circonscription1.getId(),
                    Fonction.GOUVERNEUR,
                    LocalDate.now().minusMonths(1),
                    null
            );

            mockMvc.perform(post("/affectation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.idAffectation").exists())
                    .andExpect(jsonPath("$.idAutorite").value(autorite1.getId()))
                    .andExpect(jsonPath("$.idCirconscription").value(circonscription1.getId()))
                    .andExpect(jsonPath("$.fonction").value("GOUVERNEUR"));
        }

        @Test
        @DisplayName("Échec : ID autorité nul")
        void creer_quandIdAutoriteEstNul_doitRetourner400() throws Exception {
            AffectationCreateDTO dto = new AffectationCreateDTO(null, circonscription1.getId(), Fonction.GOUVERNEUR, LocalDate.now(), null);
            mockMvc.perform(post("/affectation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("idAutorite : L'ID de l'autorité est obligatoire"));
        }

        @Test
        @DisplayName("Échec : ID circonscription nul")
        void creer_quandIdCirconscriptionEstNul_doitRetourner400() throws Exception {
            AffectationCreateDTO dto = new AffectationCreateDTO(autorite1.getId(), null, Fonction.GOUVERNEUR, LocalDate.now(), null);
            mockMvc.perform(post("/affectation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("idCirconscription : La circonscription est obligatoire"));
        }

        @Test
        @DisplayName("Échec : Fonction nulle")
        void creer_quandFonctionEstNulle_doitRetourner400() throws Exception {
            AffectationCreateDTO dto = new AffectationCreateDTO(autorite1.getId(), circonscription1.getId(), null, LocalDate.now(), null);
            mockMvc.perform(post("/affectation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("fonction : La fonction est obligatoire"));
        }

        @Test
        @DisplayName("Échec : Date de début nulle")
        void creer_quandDateDebutEstNulle_doitRetourner400() throws Exception {
            AffectationCreateDTO dto = new AffectationCreateDTO(autorite1.getId(), circonscription1.getId(), Fonction.GOUVERNEUR, null, null);
            mockMvc.perform(post("/affectation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("dateDebut : La date de début est obligatoire !"));
        }

        @Test
        @DisplayName("Échec : Date de fin antérieure à la date de début")
        void creer_quandDateFinAvantDateDebut_doitRetourner400() throws Exception {
            AffectationCreateDTO dto = new AffectationCreateDTO(
                    autorite1.getId(),
                    circonscription1.getId(),
                    Fonction.GOUVERNEUR,
                    LocalDate.now(),
                    LocalDate.now().minusDays(1)
            );

            mockMvc.perform(post("/affectation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("La date de fin doit être après la date de début"));
        }

        @Test
        @DisplayName("Échec : Circonscription déjà occupée")
        void creer_quandCirconscriptionOccupee_doitRetourner409() throws Exception {
            // Arrange : une affectation active existante pour circonscription1
            affectationRepository.save(new Affectation(null, autorite1, circonscription1, Fonction.GOUVERNEUR, LocalDate.now().minusYears(1), null));

            // Act & Assert : essayer de créer une nouvelle affectation pour la même circonscription
            AffectationCreateDTO dto = new AffectationCreateDTO(autorite2.getId(), circonscription1.getId(), Fonction.GOUVERNEUR, LocalDate.now(), null);
            mockMvc.perform(post("/affectation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("Contrainte d’intégrité violée : La circonscription '" + circonscription1.getNom() + "' est déjà occupée par une autorité active."));
        }

        @Test
        @DisplayName("Échec : Autorité déjà en poste actif")
        void creer_quandAutoriteDejaActive_doitRetourner409() throws Exception {
            // Arrange : une affectation active existante pour autorite1
            affectationRepository.save(new Affectation(null, autorite1, circonscription1, Fonction.GOUVERNEUR, LocalDate.now().minusYears(1), null));

            // Act & Assert : essayer de créer une nouvelle affectation pour la même autorité sur une autre circonscription
            AffectationCreateDTO dto = new AffectationCreateDTO(autorite1.getId(), circonscription2.getId(), Fonction.GOUVERNEUR, LocalDate.now(), null);
            mockMvc.perform(post("/affectation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("Contrainte d’intégrité violée : L'autorité '" + autorite1.getNom() + " " + autorite1.getPrenom() + "' est déjà en poste actif ailleurs."));
        }
    }

    @Nested
    @DisplayName("Tests pour la récupération (GET /affectation)")
    class RecuperationTests {

        @Test
        @DisplayName("Succès : Lister les affectations quand la liste est vide")
        void lister_quandAucuneAffectation_doitRetourner200EtListeVide() throws Exception {
            mockMvc.perform(get("/affectation"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("Succès : Lister les affectations quand plusieurs existent")
        void lister_quandPlusieursAffectations_doitRetourner200EtListeComplete() throws Exception {
            affectationRepository.save(new Affectation(null, autorite1, circonscription1, Fonction.GOUVERNEUR, LocalDate.now(), null));
            affectationRepository.save(new Affectation(null, autorite2, circonscription2, Fonction.GOUVERNEUR, LocalDate.now(), null));

            mockMvc.perform(get("/affectation"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].idAutorite", is(autorite1.getId())))
                    .andExpect(jsonPath("$[1].idAutorite", is(autorite2.getId())));
        }
    }

    @Nested
    @DisplayName("Tests pour la modification (PUT /affectation/{id})")
    class ModificationTests {

        private Affectation affectationExistante;

        @BeforeEach
        void setUp() {
            affectationExistante = affectationRepository.save(new Affectation(null, autorite1, circonscription1, Fonction.GOUVERNEUR, LocalDate.now().minusYears(1), null));
        }

        @Test
        @DisplayName("Succès : Modifier une affectation avec des données valides")
        void modifier_quandDonneesValides_doitRetourner200() throws Exception {
            AffectationCreateDTO dto = new AffectationCreateDTO(
                    autorite2.getId(), // Changement d'autorité
                    circonscription2.getId(), // Changement de circonscription
                    Fonction.PREFET,
                    LocalDate.now(),
                    null
            );

            mockMvc.perform(put("/affectation/{id}", affectationExistante.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.idAutorite").value(autorite2.getId()))
                    .andExpect(jsonPath("$.idCirconscription").value(circonscription2.getId()))
                    .andExpect(jsonPath("$.fonction").value("PREFET"));
        }

        @Test
        @DisplayName("Échec : ID d'affectation non trouvé")
        void modifier_quandIdInexistant_doitRetourner404() throws Exception {
            AffectationCreateDTO dto = new AffectationCreateDTO(autorite1.getId(), circonscription1.getId(), Fonction.GOUVERNEUR, LocalDate.now(), null);
            int idInexistant = 9999;

            mockMvc.perform(put("/affectation/{id}", idInexistant)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Aucune Affectation trouvée avec l'ID : " + idInexistant));
        }

        @Test
        @DisplayName("Échec : ID autorité nul lors de la modification")
        void modifier_quandIdAutoriteNul_doitRetourner400() throws Exception {
            AffectationCreateDTO dto = new AffectationCreateDTO(null, circonscription1.getId(), Fonction.GOUVERNEUR, LocalDate.now(), null);

            mockMvc.perform(put("/affectation/{id}", affectationExistante.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("idAutorite : L'ID de l'autorité est obligatoire"));
        }

        @Test
        @DisplayName("Échec : Modification vers une circonscription déjà occupée")
        void modifier_quandCirconscriptionOccupee_doitRetourner409() throws Exception {
            // Arrange : une autre affectation active sur circonscription2
            affectationRepository.save(new Affectation(null, autorite2, circonscription2, Fonction.GOUVERNEUR, LocalDate.now().minusMonths(6), null));

            // Act & Assert : essayer de modifier affectationExistante pour pointer vers circonscription2
            AffectationCreateDTO dto = new AffectationCreateDTO(autorite1.getId(), circonscription2.getId(), Fonction.GOUVERNEUR, LocalDate.now(), null);
            mockMvc.perform(put("/affectation/{id}", affectationExistante.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("Contrainte d’intégrité violée : La circonscription '" + circonscription2.getNom()
                            + "' est déjà occupée par une autorité active."));
        }
    }

    @Nested
    @DisplayName("Tests pour la suppression (DELETE /affectation/{id})")
    class SuppressionTests {

        private Affectation affectationASupprimer;

        @BeforeEach
        void setUp() {
            affectationASupprimer = affectationRepository.save(new Affectation(null, autorite1, circonscription1, Fonction.GOUVERNEUR, LocalDate.now(), null));
        }

        @Test
        @DisplayName("Succès : Supprimer une affectation existante")
        void supprimer_quandIdExistant_doitRetourner204() throws Exception {
            mockMvc.perform(delete("/affectation/{id}", affectationASupprimer.getId()))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Échec : Supprimer une affectation avec un ID non trouvé")
        void supprimer_quandIdInexistant_doitRetourner404() throws Exception {
            int idInexistant = 9999;
            mockMvc.perform(delete("/affectation/{id}", idInexistant))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Aucune Affectation trouvée avec l'ID : " + idInexistant));
        }
    }
}
