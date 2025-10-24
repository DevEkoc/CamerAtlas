package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.dto.quartier.QuartierCreateDTO;
import com.devekoc.camerAtlas.entities.Arrondissement;
import com.devekoc.camerAtlas.entities.Departement;
import com.devekoc.camerAtlas.entities.Quartier;
import com.devekoc.camerAtlas.entities.Region;
import com.devekoc.camerAtlas.repositories.ArrondissementRepository;
import com.devekoc.camerAtlas.repositories.DepartementRepository;
import com.devekoc.camerAtlas.repositories.QuartierRepository;
import com.devekoc.camerAtlas.repositories.RegionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@DisplayName("Tests d'intégration pour QuartierController")
class QuartierControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private QuartierRepository quartierRepository;
    @Autowired
    private ArrondissementRepository arrondissementRepository;
    @Autowired
    private DepartementRepository departementRepository;
    @Autowired
    private RegionRepository regionRepository;

    private Arrondissement arrondissement;

    @BeforeEach
    void setUp() {
        quartierRepository.deleteAll();
        arrondissementRepository.deleteAll();
        departementRepository.deleteAll();
        regionRepository.deleteAll();

        Region region = new Region();
        region.setNom("Centre");
        region.setChefLieu("Yaoundé");
        region.setPopulation(4000000);
        region.setSuperficie(68953);
        regionRepository.save(region);

        Departement departement = new Departement();
        departement.setNom("Mfoundi");
        departement.setPrefecture("Yaoundé");
        departement.setPopulation(2000000);
        departement.setSuperficie(297);
        departement.setRegion(region);
        departementRepository.save(departement);

        arrondissement = new Arrondissement();
        arrondissement.setNom("Yaoundé I");
        arrondissement.setSousPrefecture("Nlongkak");
        arrondissement.setPopulation(300000);
        arrondissement.setSuperficie(50);
        arrondissement.setDepartement(departement);
        arrondissementRepository.save(arrondissement);
    }

    private Quartier creerQuartierPourTest(String nom, String nomPopulaire, Arrondissement arr) {
        Quartier quartier = new Quartier();
        quartier.setNom(nom);
        quartier.setNomPopulaire(nomPopulaire);
        quartier.setSousPrefecture(arr);
        return quartierRepository.save(quartier);
    }

    @Nested
    @DisplayName("Tests pour la création (POST /quartier)")
    class CreationTests {

        @Test
        @DisplayName("Succès : Créer un quartier avec un DTO valide")
        void creerQuartier_quandLeDtoEstValide_doitRetourner201EtLeQuartierCree() throws Exception {
            var dto = new QuartierCreateDTO("Nlongkak", "Nlongkak", arrondissement.getId());

            mockMvc.perform(post("/quartier")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.nom").value("Nlongkak"))
                    .andExpect(jsonPath("$.idSousPrefecture").value(arrondissement.getId()));
        }

        @Test
        @DisplayName("Échec : Nom déjà existant")
        void creerQuartier_quandLeNomExisteDeja_doitRetourner409Conflict() throws Exception {
            creerQuartierPourTest("Nlongkak", "Nlongkak", arrondissement);
            var dto = new QuartierCreateDTO("Nlongkak", "Nlongkak", arrondissement.getId());

            mockMvc.perform(post("/quartier")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Échec : ID de sous-préfecture inexistant")
        void creerQuartier_quandIdSousPrefectureInexistant_doitRetourner404NotFound() throws Exception {
            var dto = new QuartierCreateDTO("Nlongkak", "Nlongkak", 999);

            mockMvc.perform(post("/quartier")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isNotFound());
        }

        // --- Tests de validation ---
        @Test
        @DisplayName("Échec : DTO avec nom vide")
        void creerQuartier_quandLeNomEstVide_doitRetourner400BadRequest() throws Exception {
            var dto = new QuartierCreateDTO("", "Nlongkak", arrondissement.getId());
            mockMvc.perform(post("/quartier").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Échec : DTO avec nom trop long")
        void creerQuartier_quandLeNomEstTropLong_doitRetourner400BadRequest() throws Exception {
            String nomTropLong = "a".repeat(51);
            var dto = new QuartierCreateDTO(nomTropLong, "Nlongkak", arrondissement.getId());
            mockMvc.perform(post("/quartier").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Échec : DTO avec nom populaire vide")
        void creerQuartier_quandLeNomPopulaireEstVide_doitRetourner400BadRequest() throws Exception {
            var dto = new QuartierCreateDTO("Nlongkak", "", arrondissement.getId());
            mockMvc.perform(post("/quartier").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Échec : DTO avec ID sous-préfecture nul")
        void creerQuartier_quandIdSousPrefectureEstNul_doitRetourner400BadRequest() throws Exception {
            var dto = new QuartierCreateDTO("Nlongkak", "Nlongkak", null);
            mockMvc.perform(post("/quartier").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Tests pour la recherche (GET)")
    class RechercheTests {

        @Test
        @DisplayName("GET /quartier - Succès : Lister les quartiers")
        void listerQuartiers_quandLaBaseContientDesQuartiers_doitRetourner200EtLaListe() throws Exception {
            creerQuartierPourTest("Nlongkak", "Nlongkak", arrondissement);
            creerQuartierPourTest("Bastos", "Bastos", arrondissement);

            mockMvc.perform(get("/quartier"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)));
        }

        @Test
        @DisplayName("GET /quartier/id/{id} - Succès : Rechercher par un ID existant")
        void rechercherParId_quandIdExiste_doitRetourner200EtLeQuartier() throws Exception {
            Quartier quartier = creerQuartierPourTest("Nlongkak", "Nlongkak", arrondissement);

            mockMvc.perform(get("/quartier/id/{id}", quartier.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nom").value("Nlongkak"));
        }

        @Test
        @DisplayName("GET /quartier/id/{id} - Échec : Rechercher par un ID inexistant")
        void rechercherParId_quandIdInexistant_doitRetourner404NotFound() throws Exception {
            mockMvc.perform(get("/quartier/id/{id}", 999))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Le quartier n'existe pas"));
        }

        @Test
        @DisplayName("GET /quartier/nom/{nom} - Succès : Rechercher par un nom existant")
        void rechercherParNom_quandNomExiste_doitRetourner200EtLeQuartier() throws Exception {
            Quartier quartier = creerQuartierPourTest("Nlongkak", "Nlongkak", arrondissement);

            mockMvc.perform(get("/quartier/nom/{nom}", quartier.getNom()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nom").value("Nlongkak"));
        }

        @Test
        @DisplayName("GET /quartier/nom/{nom} - Échec : Rechercher par un nom inexistant")
        void rechercherParNom_quandNomInexistant_doitRetourner404NotFound() throws Exception {
            mockMvc.perform(get("/quartier/nom/{nom}", "Inexistant"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Le quartier n'existe pas"));
        }
    }

    @Nested
    @DisplayName("Tests pour la modification (PUT /quartier/{id})")
    class ModificationTests {

        @Test
        @DisplayName("Succès : Modifier un quartier avec un DTO valide")
        void modifierQuartier_quandDtoValideEtIdExiste_doitRetourner200EtQuartierMiseAJour() throws Exception {
            Quartier quartier = creerQuartierPourTest("Ancien Nom", "Ancien Pop", arrondissement);
            var dto = new QuartierCreateDTO("Nouveau Nom", "Nouveau Pop", arrondissement.getId());

            mockMvc.perform(put("/quartier/{id}", quartier.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nom").value("Nouveau Nom"))
                    .andExpect(jsonPath("$.nomPopulaire").value("Nouveau Pop"));
        }

        @Test
        @DisplayName("Échec : Modifier un quartier avec un ID inexistant")
        void modifierQuartier_quandIdInexistant_doitRetourner404NotFound() throws Exception {
            var dto = new QuartierCreateDTO("Nouveau Nom", "Nouveau Pop", arrondissement.getId());
            mockMvc.perform(put("/quartier/{id}", 999).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Aucun Quartier trouvé avec l'ID : 999"));
        }

        @Test
        @DisplayName("Échec : Modifier pour un nom qui existe déjà")
        void modifierQuartier_quandNomExisteDeja_doitRetourner409Conflict() throws Exception {
            creerQuartierPourTest("Existant", "Existant Pop", arrondissement);
            Quartier aModifier = creerQuartierPourTest("A Modifier", "A Modifier Pop", arrondissement);
            var dto = new QuartierCreateDTO("Existant", "Nouveau Pop", arrondissement.getId());

            mockMvc.perform(put("/quartier/{id}", aModifier.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("Contrainte d’intégrité violée : Un quartier avec le nom '" + dto.nom() + "' existe déjà"));
        }
    }

    @Nested
    @DisplayName("Tests pour la suppression (DELETE)")
    class SuppressionTests {

        @Test
        @DisplayName("DELETE /quartier/id/{id} - Succès : Supprimer par un ID existant")
        void supprimerParId_quandIdExiste_doitRetourner204NoContent() throws Exception {
            Quartier quartier = creerQuartierPourTest("A Supprimer", "A Supprimer Pop", arrondissement);

            mockMvc.perform(delete("/quartier/id/{id}", quartier.getId()))
                    .andExpect(status().isNoContent());

            assertThat(quartierRepository.findById(quartier.getId())).isEmpty();
        }

        @Test
        @DisplayName("DELETE /quartier/id/{id} - Échec : Supprimer par un ID inexistant")
        void supprimerParId_quandIdInexistant_doitRetourner404NotFound() throws Exception {
            mockMvc.perform(delete("/quartier/id/{id}", 999))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Le quartier n'existe pas"));
        }

        @Test
        @DisplayName("DELETE /quartier/nom/{nom} - Succès : Supprimer par un nom existant")
        void supprimerParNom_quandNomExiste_doitRetourner204NoContent() throws Exception {
            Quartier quartier = creerQuartierPourTest("A Supprimer Par Nom", "A Supprimer Par Nom Pop", arrondissement);

            mockMvc.perform(delete("/quartier/nom/{nom}", quartier.getNom()))
                    .andExpect(status().isNoContent());

            assertThat(quartierRepository.findByNom(quartier.getNom())).isEmpty();
        }

        @Test
        @DisplayName("DELETE /quartier/nom/{nom} - Échec : Supprimer par un nom inexistant")
        void supprimerParNom_quandNomInexistant_doitRetourner404NotFound() throws Exception {
            mockMvc.perform(delete("/quartier/nom/{nom}", "Inexistant"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Le quartier n'existe pas"));
        }
    }
}