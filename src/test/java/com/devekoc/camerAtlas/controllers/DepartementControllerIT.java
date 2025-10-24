package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.dto.departement.DepartementCreateDTO;
import com.devekoc.camerAtlas.entities.Arrondissement;
import com.devekoc.camerAtlas.entities.Departement;
import com.devekoc.camerAtlas.entities.Region;
import com.devekoc.camerAtlas.repositories.ArrondissementRepository;
import com.devekoc.camerAtlas.repositories.DepartementRepository;
import com.devekoc.camerAtlas.repositories.RegionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
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
@DisplayName("Tests d'intégration pour DepartementController")
public class DepartementControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private DepartementRepository departementRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private ArrondissementRepository arrondissementRepository;

    private Region regionEst;
    private Region regionOuest;

    @BeforeEach
    void nettoyerEtInitialiserLaBase() {
        arrondissementRepository.deleteAll();
        departementRepository.deleteAll();
        regionRepository.deleteAll();

        regionEst = new Region();
        regionEst.setNom("Est");
        regionEst.setSuperficie(109002);
        regionEst.setPopulation(835600);
        regionEst.setChefLieu("Bertoua");
        regionEst.setCodeMineralogique("ES");
        regionRepository.save(regionEst);

        regionOuest = new Region();
        regionOuest.setNom("Ouest");
        regionOuest.setSuperficie(13892);
        regionOuest.setPopulation(1921590);
        regionOuest.setChefLieu("Bafoussam");
        regionOuest.setCodeMineralogique("OU");
        regionRepository.save(regionOuest);
    }

    private Departement creerDepartementPourTest(String nom, Region region) {
        Departement departement = new Departement();
        departement.setNom(nom);
        departement.setPrefecture("Prefecture de " + nom);
        departement.setPopulation(50000);
        departement.setSuperficie(5000);
        departement.setRegion(region);
        return departementRepository.save(departement);
    }

    @Nested
    @DisplayName("Tests pour la création (POST /departement)")
    class CreationTests {

        @Test
        @DisplayName("Succès : Créer un département avec un DTO valide")
        void creerDepartement_quandLeDtoEstValide_doitRetourner201EtLeDepartementCree() throws Exception {
            DepartementCreateDTO dto = new DepartementCreateDTO("Lom-et-Djerem", 26345, 228700, "coords", "Bertoua", regionEst.getId());

            mockMvc.perform(post("/departement")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.nom").value("Lom-et-Djerem"))
                    .andExpect(jsonPath("$.nomRegion").value(regionEst.getNom()));
        }

        @Test
        @DisplayName("Échec : Nom de département déjà existant")
        void creerDepartement_quandLeNomExisteDeja_doitRetourner409Conflict() throws Exception {
            creerDepartementPourTest("Kadey", regionEst);
            DepartementCreateDTO dto = new DepartementCreateDTO("Kadey", 15884, 192900, "coords", "Batouri", regionEst.getId());

            mockMvc.perform(post("/departement")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Échec : ID de région inexistant")
        void creerDepartement_quandIdRegionInexistant_doitRetourner404NotFound() throws Exception {
            DepartementCreateDTO dto = new DepartementCreateDTO("Lom-et-Djerem", 26345, 228700, "coords", "Bertoua", 999);

            mockMvc.perform(post("/departement")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Échec : DTO avec nom vide")
        void creerDepartement_quandLeNomEstVide_doitRetourner400BadRequest() throws Exception {
            DepartementCreateDTO dto = new DepartementCreateDTO("", 26345, 228700, "coords", "Bertoua", regionEst.getId());
            mockMvc.perform(post("/departement").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("nom : Le nom ne doit pas être vide !"));
        }

        @Test
        @DisplayName("Échec : DTO avec superficie négative")
        void creerDepartement_quandLaSuperficieEstNegative_doitRetourner400BadRequest() throws Exception {
            DepartementCreateDTO dto = new DepartementCreateDTO("Lom-et-Djerem", -100, 228700, "coords", "Bertoua", regionEst.getId());
            mockMvc.perform(post("/departement").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("superficie : La superficie doit être positive"));
        }

        @Test
        @DisplayName("Échec : DTO avec population nulle")
        void creerDepartement_quandLaPopulationEstNulle_doitRetourner400BadRequest() throws Exception {
            DepartementCreateDTO dto = new DepartementCreateDTO("Lom-et-Djerem", 26345, null, "coords", "Bertoua", regionEst.getId());
            mockMvc.perform(post("/departement").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("population : La population ne doit pas être vide !"));
        }

        @Test
        @DisplayName("Échec : DTO avec préfecture vide")
        void creerDepartement_quandLaPrefectureEstVide_doitRetourner400BadRequest() throws Exception {
            DepartementCreateDTO dto = new DepartementCreateDTO("Lom-et-Djerem", 26345, 228700, "coords", "", regionEst.getId());
            mockMvc.perform(post("/departement").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("prefecture : La préfecture ne doit pas être vide !"));
        }

        @Test
        @DisplayName("Échec : DTO avec ID région nul")
        void creerDepartement_quandIdRegionEstNul_doitRetourner400BadRequest() throws Exception {
            DepartementCreateDTO dto = new DepartementCreateDTO("Lom-et-Djerem", 26345, 228700, "coords", "Bertoua", null);
            mockMvc.perform(post("/departement").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("idRegion : L'ID de la région ne doit pas être vide !"));
        }
    }

    @Nested
    @DisplayName("Tests pour la recherche (GET)")
    class RechercheTests {

        @Test
        @DisplayName("GET /departement - Succès : Lister les départements")
        void listerDepartements_quandLaBaseContientDesDepartements_doitRetourner200EtLaListe() throws Exception {
            creerDepartementPourTest("Lom-et-Djerem", regionEst);
            creerDepartementPourTest("Mifi", regionOuest);

            mockMvc.perform(get("/departement"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].nom").value("Lom-et-Djerem"))
                    .andExpect(jsonPath("$[1].nom").value("Mifi"));
        }

        @Test
        @DisplayName("GET /departement/id/{id} - Succès : Rechercher par un ID existant")
        void rechercherParId_quandIdExiste_doitRetourner200EtLeDepartement() throws Exception {
            Departement departement = creerDepartementPourTest("Lom-et-Djerem", regionEst);

            mockMvc.perform(get("/departement/id/{id}", departement.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(departement.getId()))
                    .andExpect(jsonPath("$.nom").value("Lom-et-Djerem"));
        }

        @Test
        @DisplayName("GET /departement/id/{id} - Échec : Rechercher par un ID inexistant")
        void rechercherParId_quandIdInexistant_doitRetourner404NotFound() throws Exception {
            mockMvc.perform(get("/departement/id/{id}", 999))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Le département n'existe pas"));
        }

        @Test
        @DisplayName("GET /departement/nom/{nom} - Succès : Rechercher par un nom existant")
        void rechercherParNom_quandNomExiste_doitRetourner200EtLeDepartement() throws Exception {
            Departement departement = creerDepartementPourTest("Lom-et-Djerem", regionEst);

            mockMvc.perform(get("/departement/nom/{nom}", departement.getNom()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(departement.getId()))
                    .andExpect(jsonPath("$.nom").value("Lom-et-Djerem"));
        }

        @Test
        @DisplayName("GET /departement/nom/{nom} - Échec : Rechercher par un nom inexistant")
        void rechercherParNom_quandNomInexistant_doitRetourner404NotFound() throws Exception {
            mockMvc.perform(get("/departement/id/{id}", 999))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Le département n'existe pas"));
        }
    }

    @Nested
    @DisplayName("Tests pour la modification (PUT /departement/{id})")
    class ModificationTests {

        @Test
        @DisplayName("Succès : Modifier un département avec un DTO valide")
        void modifierDepartement_quandDtoValideEtIdExiste_doitRetourner200EtDepartementMiseAJour() throws Exception {
            Departement departement = creerDepartementPourTest("Ancien Nom", regionEst);
            DepartementCreateDTO dto = new DepartementCreateDTO("Nouveau Nom", 123, 456, "new coords", "Nouvelle Pref", regionOuest.getId());

            mockMvc.perform(put("/departement/{id}", departement.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nom").value("Nouveau Nom"))
                    .andExpect(jsonPath("$.nomRegion").value(regionOuest.getNom()));
        }

        @Test
        @DisplayName("Échec : Modifier un département avec un ID inexistant")
        void modifierDepartement_quandIdInexistant_doitRetourner404NotFound() throws Exception {
            DepartementCreateDTO dto = new DepartementCreateDTO("Nouveau Nom", 123, 456, "coords", "Pref", regionEst.getId());
            mockMvc.perform(put("/departement/{id}", 999).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Aucun Département trouvé avec l'ID : 999"));
        }

        @Test
        @DisplayName("Échec : Modifier pour un nom qui existe déjà")
        void modifierDepartement_quandNomExisteDeja_doitRetourner409Conflict() throws Exception {
            Departement departement1 = creerDepartementPourTest("Departement Un", regionEst);
            Departement departement2 = creerDepartementPourTest("Departement Deux", regionEst);
            DepartementCreateDTO dto = new DepartementCreateDTO(departement2.getNom(), 123, 456, "c", "P", regionEst.getId());

            mockMvc.perform(put("/departement/{id}", departement1.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("Contrainte d’intégrité violée : Un département avec le nom '" + dto.nom() + "' existe déjà"));
        }
    }

    @Nested
    @DisplayName("Tests pour la suppression (DELETE)")
    class SuppressionTests {

        @Test
        @DisplayName("DELETE /departement/id/{id} - Succès : Supprimer par un ID existant")
        void supprimerParId_quandIdExiste_doitRetourner204NoContent() throws Exception {
            Departement departement = creerDepartementPourTest("A Supprimer", regionEst);

            mockMvc.perform(delete("/departement/id/{id}", departement.getId()))
                    .andExpect(status().isNoContent());

            assertThat(departementRepository.findById(departement.getId())).isEmpty();
        }

        @Test
        @DisplayName("DELETE /departement/id/{id} - Échec : Supprimer par un ID inexistant")
        void supprimerParId_quandIdInexistant_doitRetourner404NotFound() throws Exception {
            mockMvc.perform(delete("/departement/id/{id}", 999))
                    .andExpect(status().isNotFound())
                    .andExpect((jsonPath("$.message").value("Le département n'existe pas")));
        }

        @Test
        @DisplayName("DELETE /departement/nom/{nom} - Succès : Supprimer par un nom existant")
        void supprimerParNom_quandNomExiste_doitRetourner204NoContent() throws Exception {
            Departement departement = creerDepartementPourTest("A Supprimer Par Nom", regionEst);

            mockMvc.perform(delete("/departement/nom/{nom}", departement.getNom()))
                    .andExpect(status().isNoContent());

            assertThat(departementRepository.findByNom(departement.getNom())).isEmpty();
        }

        @Test
        @DisplayName("DELETE /departement/id/{id} - Échec : Supprimer par un nom inexistant")
        void supprimerParNom_quandNomInexistant_doitRetourner404NotFound() throws Exception {
            mockMvc.perform(delete("/departement/nom/{nom}", "Inexistant"))
                    .andExpect(status().isNotFound())
                    .andExpect((jsonPath("$.message").value("Le département n'existe pas")));
        }

        @Test
        @DisplayName("DELETE /departement/id/{id} - Échec : Supprimer un département qui a des arrondissements")
        // Ce test ne fonctionne pas comme prévu en raison de la façon dont MockMVC et JUnit gèrent le flush dans la BD
        // Un DataIntegrityViolation devrait être levée pour signifier qu'un département contenant des arrondissements ne peut pas être supprimé.
        void supprimerParId_quandDepartementContientArrondissements_doitRetourner409Conflict() throws Exception {
            Departement departement = creerDepartementPourTest("Avec Arrondissements", regionEst);
            Arrondissement arrondissement = new Arrondissement();
            arrondissement.setNom("Arrondissement Test");
            arrondissement.setSousPrefecture("Test");
            arrondissement.setDepartement(departement);
            arrondissement.setPopulation(100);
            arrondissement.setSuperficie(100);
            arrondissementRepository.save(arrondissement);

            entityManager.flush();
            entityManager.clear();

            mockMvc.perform(delete("/departement/id/{id}", departement.getId()))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("Contrainte d’intégrité violée : Impossible de supprimer un département contenant des arrondissements"))
            ;
        }
    }
}