package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.dto.arrondissement.ArrondissementCreateDTO;
import com.devekoc.camerAtlas.entities.Arrondissement;
import com.devekoc.camerAtlas.entities.Departement;
import com.devekoc.camerAtlas.entities.Quartier;
import com.devekoc.camerAtlas.entities.Region;
import com.devekoc.camerAtlas.repositories.ArrondissementRepository;
import com.devekoc.camerAtlas.repositories.DepartementRepository;
import com.devekoc.camerAtlas.repositories.QuartierRepository;
import com.devekoc.camerAtlas.repositories.RegionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
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
@DisplayName("Tests d'intégration pour ArrondissementController")
class ArrondissementControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private ArrondissementRepository arrondissementRepository;
    @Autowired
    private DepartementRepository departementRepository;
    @Autowired
    private RegionRepository regionRepository;
    @Autowired
    private QuartierRepository quartierRepository;

    private Region region;
    private Departement departement;

    @BeforeEach
    void setUp() {
        quartierRepository.deleteAll();
        arrondissementRepository.deleteAll();
        departementRepository.deleteAll();
        regionRepository.deleteAll();

        region = new Region();
        region.setNom("Centre");
        region.setChefLieu("Yaoundé");
        region.setPopulation(4000000);
        region.setSuperficie(68953);
        regionRepository.save(region);

        departement = new Departement();
        departement.setNom("Mfoundi");
        departement.setPrefecture("Yaoundé");
        departement.setPopulation(2000000);
        departement.setSuperficie(297);
        departement.setRegion(region);
        departementRepository.save(departement);
    }

    private Arrondissement creerArrondissementPourTest(String nom, Departement dept) {
        Arrondissement arrondissement = new Arrondissement();
        arrondissement.setNom(nom);
        arrondissement.setSousPrefecture("Sous-préfecture de " + nom);
        arrondissement.setPopulation(50000);
        arrondissement.setSuperficie(50);
        arrondissement.setDepartement(dept);
        return arrondissementRepository.save(arrondissement);
    }

    @Nested
    @DisplayName("Tests pour la création (POST /arrondissement)")
    class CreationTests {

        @Test
        @DisplayName("Succès : Créer un arrondissement avec un DTO valide")
        void creerArrondissement_quandLeDtoEstValide_doitRetourner201EtLArrondissementCree() throws Exception {
            var dto = new ArrondissementCreateDTO("Yaoundé I", 50, 300000, "coords", "Nlongkak", departement.getId());

            mockMvc.perform(post("/arrondissement")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.nom").value("Yaoundé I"))
                    .andExpect(jsonPath("$.idDepartement").value(departement.getId()));
        }

        @Test
        @DisplayName("Échec : Nom déjà existant")
        void creerArrondissement_quandLeNomExisteDeja_doitRetourner409Conflict() throws Exception {
            creerArrondissementPourTest("Yaoundé I", departement);
            var dto = new ArrondissementCreateDTO("Yaoundé I", 50, 300000, "coords", "Nlongkak", departement.getId());

            mockMvc.perform(post("/arrondissement")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("Contrainte d’intégrité violée : Un arrondissement avec le nom '" + dto.nom() + "' existe déjà"));
        }

        @Test
        @DisplayName("Échec : ID de département inexistant")
        void creerArrondissement_quandIdDepartementInexistant_doitRetourner404NotFound() throws Exception {
            var dto = new ArrondissementCreateDTO("Yaoundé I", 50, 300000, "coords", "Nlongkak", 999);

            mockMvc.perform(post("/arrondissement")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Aucun département trouvé avec l'ID : " + dto.idDepartement()));
        }

        // --- Tests de validation ---
        @Test
        @DisplayName("Échec : DTO avec nom vide")
        void creerArrondissement_quandLeNomEstVide_doitRetourner400BadRequest() throws Exception {
            var dto = new ArrondissementCreateDTO("", 50, 300000, "coords", "Nlongkak", departement.getId());
            mockMvc.perform(post("/arrondissement").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("nom : Le nom ne doit pas être vide !"));
        }

        @Test
        @DisplayName("Échec : DTO avec nom trop long")
        void creerArrondissement_quandLeNomEstTropLong_doitRetourner400BadRequest() throws Exception {
            String nomTropLong = "a".repeat(51);
            var dto = new ArrondissementCreateDTO(nomTropLong, 50, 300000, "coords", "Nlongkak", departement.getId());
            mockMvc.perform(post("/arrondissement").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("nom : Le nom doit contenir au maximum 50 caractères"));
        }

        @Test
        @DisplayName("Échec : DTO avec superficie négative")
        void creerArrondissement_quandLaSuperficieEstNegative_doitRetourner400BadRequest() throws Exception {
            var dto = new ArrondissementCreateDTO("Yaoundé I", -100, 300000, "coords", "Nlongkak", departement.getId());
            mockMvc.perform(post("/arrondissement").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("superficie : La superficie doit être positive"));
        }

        @Test
        @DisplayName("Échec : DTO avec population nulle")
        void creerArrondissement_quandLaPopulationEstNulle_doitRetourner400BadRequest() throws Exception {
            var dto = new ArrondissementCreateDTO("Yaoundé I", 50, null, "coords", "Nlongkak", departement.getId());
            mockMvc.perform(post("/arrondissement").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("population : La population ne doit pas être vide !"));
        }

        @Test
        @DisplayName("Échec : DTO avec sous-préfecture vide")
        void creerArrondissement_quandLaSousPrefectureEstVide_doitRetourner400BadRequest() throws Exception {
            var dto = new ArrondissementCreateDTO("Yaoundé I", 50, 300000, "coords", "", departement.getId());
            mockMvc.perform(post("/arrondissement").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("sousPrefecture : La Sous-préfecture ne doit pas être vide !"));
        }

        @Test
        @DisplayName("Échec : DTO avec ID département nul")
        void creerArrondissement_quandIdDepartementEstNul_doitRetourner400BadRequest() throws Exception {
            var dto = new ArrondissementCreateDTO("Yaoundé I", 50, 300000, "coords", "Nlongkak", null);
            mockMvc.perform(post("/arrondissement").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("idDepartement : L'ID du département ne doit pas être vide !"));
        }
    }

    @Nested
    @DisplayName("Tests pour la recherche (GET)")
    class RechercheTests {

        @Test
        @DisplayName("GET /arrondissement - Succès : Lister les arrondissements")
        void listerArrondissements_quandLaBaseContientDesArrondissements_doitRetourner200EtLaListe() throws Exception {
            creerArrondissementPourTest("Yaoundé I", departement);
            creerArrondissementPourTest("Yaoundé II", departement);

            mockMvc.perform(get("/arrondissement"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].nom").value("Yaoundé I"))
                    .andExpect(jsonPath("$[1].nom").value("Yaoundé II"));
        }

        @Test
        @DisplayName("GET /arrondissement/id/{id} - Succès : Rechercher par un ID existant")
        void rechercherParId_quandIdExiste_doitRetourner200EtLArrondissement() throws Exception {
            Arrondissement arrondissement = creerArrondissementPourTest("Yaoundé I", departement);

            mockMvc.perform(get("/arrondissement/id/{id}", arrondissement.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(arrondissement.getId()))
                    .andExpect(jsonPath("$.nom").value("Yaoundé I"));
        }

        @Test
        @DisplayName("GET /arrondissement/id/{id} - Échec : Rechercher par un ID inexistant")
        void rechercherParId_quandIdInexistant_doitRetourner404NotFound() throws Exception {
            mockMvc.perform(get("/arrondissement/id/{id}", 999))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("L'arrondissement n'existe pas"));
        }

        @Test
        @DisplayName("GET /arrondissement/nom/{nom} - Succès : Rechercher par un nom existant")
        void rechercherParNom_quandNomExiste_doitRetourner200EtLArrondissement() throws Exception {
            Arrondissement arrondissement = creerArrondissementPourTest("Yaoundé I", departement);

            mockMvc.perform(get("/arrondissement/nom/{nom}", arrondissement.getNom()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(arrondissement.getId()))
                    .andExpect(jsonPath("$.nom").value("Yaoundé I"));
        }
    }

    @Nested
    @DisplayName("Tests pour la modification (PUT /arrondissement/{id})")
    class ModificationTests {

        @Test
        @DisplayName("Succès : Modifier un arrondissement avec un DTO valide")
        void modifierArrondissement_quandDtoValideEtIdExiste_doitRetourner200EtArrondissementMiseAJour() throws Exception {
            Arrondissement arrondissement = creerArrondissementPourTest("Ancien Nom", departement);
            var dto = new ArrondissementCreateDTO("Nouveau Nom", 123, 456, "new coords", "Nouvelle Sous-Pref", departement.getId());

            mockMvc.perform(put("/arrondissement/{id}", arrondissement.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nom").value("Nouveau Nom"))
                    .andExpect(jsonPath("$.idDepartement").value(departement.getId()));
        }

        @Test
        @DisplayName("Échec : Modifier un arrondissement avec un ID inexistant")
        void modifierArrondissement_quandIdInexistant_doitRetourner404NotFound() throws Exception {
            var dto = new ArrondissementCreateDTO("Nouveau Nom", 123, 456, "coords", "Pref", departement.getId());
            mockMvc.perform(put("/arrondissement/{id}", 999).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Échec : Modifier pour un nom qui existe déjà")
        void modifierArrondissement_quandNomExisteDeja_doitRetourner409Conflict() throws Exception {
            creerArrondissementPourTest("Existant", departement);
            Arrondissement aModifier = creerArrondissementPourTest("A Modifier", departement);
            var dto = new ArrondissementCreateDTO("Existant", 123, 456, "c", "P", departement.getId());

            mockMvc.perform(put("/arrondissement/{id}", aModifier.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("Tests pour la suppression (DELETE)")
    class SuppressionTests {

        @Test
        @DisplayName("DELETE /arrondissement/id/{id} - Succès : Supprimer par un ID existant")
        void supprimerParId_quandIdExiste_doitRetourner204NoContent() throws Exception {
            Arrondissement arrondissement = creerArrondissementPourTest("A Supprimer", departement);

            mockMvc.perform(delete("/arrondissement/id/{id}", arrondissement.getId()))
                    .andExpect(status().isNoContent());

            assertThat(arrondissementRepository.findById(arrondissement.getId())).isEmpty();
        }

        @Test
        @DisplayName("DELETE /arrondissement/id/{id} - Échec : Supprimer par un ID inexistant")
        void supprimerParId_quandIdInexistant_doitRetourner404NotFound() throws Exception {
            mockMvc.perform(delete("/arrondissement/id/{id}", 999))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("DELETE /arrondissement/nom/{nom} - Succès : Supprimer par un nom existant")
        void supprimerParNom_quandNomExiste_doitRetourner204NoContent() throws Exception {
            Arrondissement arrondissement = creerArrondissementPourTest("A Supprimer Par Nom", departement);

            mockMvc.perform(delete("/arrondissement/nom/{nom}", arrondissement.getNom()))
                    .andExpect(status().isNoContent());

            assertThat(arrondissementRepository.findByNom(arrondissement.getNom())).isEmpty();
        }

        @Test
        @DisplayName("DELETE /arrondissement/id/{id} - Échec : Supprimer un arrondissement qui a des quartiers")
        void supprimerParId_quandArrondissementContientQuartiers_doitRetourner409Conflict() throws Exception {
            Arrondissement arrondissement = creerArrondissementPourTest("Avec Quartiers", departement);
            Quartier quartier = new Quartier();
            quartier.setNom("Quartier Test");
            quartier.setNomPopulaire("Quartier Test Populaire");
            quartier.setSousPrefecture(arrondissement);
            quartierRepository.save(quartier);

            entityManager.flush();
            entityManager.clear();

            mockMvc.perform(delete("/arrondissement/id/{id}", arrondissement.getId()))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("Contrainte d’intégrité violée : Impossible de supprimer un arrondissement contenant des quartiers"));
        }
    }
}