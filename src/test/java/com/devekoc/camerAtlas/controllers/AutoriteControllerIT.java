package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.entities.Autorite;
import com.devekoc.camerAtlas.repositories.AutoriteRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@DisplayName("Tests d'intégration pour AutoriteController")
class AutoriteControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AutoriteRepository autoriteRepository;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        autoriteRepository.deleteAll();
    }

    private Autorite creerAutoritePourTest(String nom, String prenom, LocalDate dateNaissance) {
        Autorite autorite = new Autorite(null, nom, prenom, dateNaissance);
        return autoriteRepository.save(autorite);
    }

    @Nested
    @DisplayName("Tests pour la création (POST /autorite)")
    class CreationTests {

        @Test
        @DisplayName("Succès : Créer une autorité avec des données valides")
        void creerAutorite_quandDonneesValides_doitRetourner201() throws Exception {
            Autorite autorite = new Autorite(null, "Mbarga", "Jean", LocalDate.of(1970, 1, 1));

            mockMvc.perform(post("/autorite")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(autorite)))
                    .andExpect(status().isCreated());
            var created = autoriteRepository.findAll().get(0);
            assertThat(created.getNom()).isEqualTo("Mbarga");
        }

        @Test
        @DisplayName("Échec : DTO avec nom vide")
        void creerAutorite_quandNomEstVide_doitRetourner400BadRequest() throws Exception {
            Autorite autorite = new Autorite(null, "", "Jean", LocalDate.of(1970, 1, 1));
            mockMvc.perform(post("/autorite")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(autorite)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("nom : Le nom ne peut pas être vide !"));
        }

        @Test
        @DisplayName("Échec : DTO avec date de naissance nulle")
        void creerAutorite_quandDateNaissanceEstNulle_doitRetourner400BadRequest() throws Exception {
            Autorite autorite = new Autorite(null, "Mbarga", "Jean", null);
            mockMvc.perform(post("/autorite")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(autorite)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("dateNaissance : La date de naissance est obligatoire"));
        }

        @Test
        @DisplayName("Échec : DTO avec date de naissance dans le futur")
        void creerAutorite_quandDateNaissanceEstDansLeFutur_doitRetourner400BadRequest() throws Exception {
            Autorite autorite = new Autorite(null, "Mbarga", "Jean", LocalDate.now().plusDays(1));
            mockMvc.perform(post("/autorite")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(autorite)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("dateNaissance : La date de naissance doit être dans le passé"));
        }
    }

    @Nested
    @DisplayName("Tests pour la recherche (GET)")
    class RechercheTests {

        @Test
        @DisplayName("GET /autorite - Succès : Lister les autorités")
        void listerAutorites_quandLaBaseContientDesAutorites_doitRetourner200EtLaListe() throws Exception {
            creerAutoritePourTest("Atangana", "Charles", LocalDate.of(1965, 5, 10));
            creerAutoritePourTest("Owona", "Marie", LocalDate.of(1980, 9, 22));

            mockMvc.perform(get("/autorite"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].nom").value("Atangana"))
                    .andExpect(jsonPath("$[1].nom").value("Owona"));
        }

        @Test
        @DisplayName("GET /autorite/{id} - Succès : Rechercher par un ID existant")
        void rechercherParId_quandIdExiste_doitRetourner200EtLAutorite() throws Exception {
            Autorite savedAutorite = creerAutoritePourTest("Mbarga", "Jean", LocalDate.of(1970, 1, 1));

            mockMvc.perform(get("/autorite/{id}", savedAutorite.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(savedAutorite.getId()))
                    .andExpect(jsonPath("$.nom").value("Mbarga"));
        }

        @Test
        @DisplayName("GET /autorite/{id} - Échec : Rechercher par un ID inexistant")
        void rechercherParId_quandIdInexistant_doitRetourner404NotFound() throws Exception {
            mockMvc.perform(get("/autorite/{id}", 999))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Aucune Autorité trouvée avec l'ID : 999"));
        }
    }

    @Nested
    @DisplayName("Tests pour la modification (PUT /autorite/{id})")
    class ModificationTests {

        @Test
        @DisplayName("Succès : Modifier une autorité avec des données valides")
        void modifierAutorite_quandDonneesValides_doitRetourner204() throws Exception {
            Autorite savedAutorite = creerAutoritePourTest("Ancien Nom", "Ancien Prenom", LocalDate.of(1960, 1, 1));
            Autorite autoriteModifiee = new Autorite(savedAutorite.getId(), "Nouveau Nom", "Nouveau Prenom", LocalDate.of(1961, 2, 2));

            mockMvc.perform(put("/autorite/{id}", savedAutorite.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(autoriteModifiee)))
                    .andExpect(status().isNoContent());

            Autorite autoriteVerif = autoriteRepository.findById(savedAutorite.getId()).orElseThrow();
            assertThat(autoriteVerif.getNom()).isEqualTo("Nouveau Nom");
            assertThat(autoriteVerif.getPrenom()).isEqualTo("Nouveau Prenom");
        }

        @Test
        @DisplayName("Échec : Modifier avec un ID inexistant")
        void modifierAutorite_quandIdInexistant_doitRetourner404NotFound() throws Exception {
            Autorite autorite = new Autorite(999, "Nom", "Prenom", LocalDate.of(1990, 1, 1));
            mockMvc.perform(put("/autorite/{id}", 999)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(autorite)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Aucune Autorité trouvée avec l'ID : 999"));
        }

        @Test
        @DisplayName("Échec : Modifier avec un nom vide")
        void modifierAutorite_quandNomEstVide_doitRetourner400BadRequest() throws Exception {
            Autorite savedAutorite = creerAutoritePourTest("Nom Valide", "Prenom", LocalDate.of(1990, 1, 1));
            Autorite autoriteModifiee = new Autorite(savedAutorite.getId(), "", "Nouveau Prenom", LocalDate.of(1991, 1, 1));

            mockMvc.perform(put("/autorite/{id}", savedAutorite.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(autoriteModifiee)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("nom : Le nom ne peut pas être vide !"));
        }
    }

    @Nested
    @DisplayName("Tests pour la suppression (DELETE)")
    class SuppressionTests {
        @Test
        @DisplayName("DELETE /autorite/{id} - Succès : Supprimer par un ID existant")
        void supprimerParId_quandIdExiste_doitRetourner204NoContent() throws Exception {
            Autorite savedAutorite = creerAutoritePourTest("A Supprimer", "Test", LocalDate.of(2000, 1, 1));
            mockMvc.perform(delete("/autorite/{id}", savedAutorite.getId()))
                    .andExpect(status().isNoContent());

            assertThat(autoriteRepository.findById(savedAutorite.getId())).isEmpty();
        }

        @Test
        @DisplayName("DELETE /autorite/{id} - Échec : Supprimer par un ID inexistant")
        void supprimerParId_quandIdInexistant_doitRetourner404NotFound() throws Exception {
            mockMvc.perform(delete("/autorite/{id}", 999))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Aucune Autorité trouvée avec l'ID : 999"));
        }
    }
}