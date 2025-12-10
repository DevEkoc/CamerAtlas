package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.dto.division.DivisionCreateDTO;
import com.devekoc.camerAtlas.dto.division.DivisionListDTO;
import com.devekoc.camerAtlas.dto.division.DivisionWithSubDivisionsDTO;
import com.devekoc.camerAtlas.services.DivisionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Tag(name = "Départements", description = "Gestion des départements du Cameroun")
@RestController
@AllArgsConstructor
@RequestMapping(value = "divisions")
public class DivisionController {
    private final DivisionService divisionService;

    @Operation(
            summary = "Crée un département",
            description = "Crée un nouveau département à partir des informations fournies. Requiert l'ID d'une région existante. Accepte un fichier image en multipart/form-data."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Département créé avec succès"),
                    @ApiResponse(responseCode = "400", description = "Données invalides (validation échouée)"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "404", description = "L'id fourni pour la Région n'existe pas"),
                    @ApiResponse(responseCode = "409", description = "Le nom fourni existe déjà"),
                    @ApiResponse(responseCode = "500", description = "Erreur lors de l'enregistrement de l'image")
            }
    )
    @PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<DivisionListDTO> create(@RequestBody @Valid @ModelAttribute DivisionCreateDTO dto) throws IOException {
        DivisionListDTO created = divisionService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Crée plusieurs départements en cascade",
            description = "Crée plusieurs départements en une seule requête. Chaque département est créé séquentiellement. Si un département échoue, les suivants ne seront pas créés."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Départements créés avec succès"),
                    @ApiResponse(responseCode = "400", description = "Données invalides (validation échouée)"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "404", description = "L'id fourni pour une Région n'existe pas"),
                    @ApiResponse(responseCode = "409", description = "Un nom fourni existe déjà"),
                    @ApiResponse(responseCode = "500", description = "Erreur lors de l'enregistrement d'une image")
            }
    )
    @PostMapping(path = "cascade", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<List<DivisionListDTO>> createSeveral(@RequestBody @Valid List<DivisionCreateDTO> dtos) throws IOException {
        List<DivisionListDTO> created = divisionService.createSeveral(dtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Liste tous les départements",
            description = "Récupère la liste complète de tous les départements avec leurs nominations actives et délimitations frontalières."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès (peut être vide)"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
            }
    )
    @GetMapping(produces =  APPLICATION_JSON_VALUE)
    public ResponseEntity <List<DivisionListDTO>> listAll() {
        List<DivisionListDTO> departments = divisionService.listAll();
        return ResponseEntity.ok(departments);
    }

    @Operation(
            summary = "Recherche un département par ID",
            description = "Récupère les détails d'un département spécifique à partir de son identifiant."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Département trouvé"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "404", description = "Aucun département trouvé avec cet ID"),
                    @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
            }
    )
    @GetMapping(path = "id/{id}", produces =  APPLICATION_JSON_VALUE)
    public ResponseEntity <DivisionListDTO> find(@PathVariable int id) {
        DivisionListDTO department = divisionService.find(id);
        return (department != null)
                ? ResponseEntity.ok(department)
                : ResponseEntity.notFound().build()
        ;
    }

    @Operation(
            summary = "Recherche un département par nom",
            description = "Récupère les détails d'un département spécifique à partir de son nom exact."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Département trouvé"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "404", description = "Aucun département trouvé avec ce nom"),
                    @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
            }
    )
    @GetMapping(path = "name/{name}", produces =  APPLICATION_JSON_VALUE)
    public ResponseEntity <DivisionListDTO> find(@PathVariable String name) {
        DivisionListDTO department = divisionService.find(name);
        return department != null
                ? ResponseEntity.ok(department)
                : ResponseEntity.notFound().build()
        ;
    }

    @Operation(
            summary = "Récupère un département avec ses arrondissements",
            description = "Récupère les détails d'un département incluant la liste complète de tous ses arrondissements associés."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Département et arrondissements trouvés"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "404", description = "Aucun département trouvé avec cet ID"),
                    @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
            }
    )
    @GetMapping(path = "{id}/subDivisions")
    public ResponseEntity<DivisionWithSubDivisionsDTO> findWithSubDivisions (@PathVariable int id) {
        DivisionWithSubDivisionsDTO division = divisionService.findWithSubDivisions(id);
        return division != null
                ? ResponseEntity.ok(division)
                : ResponseEntity.notFound().build()
        ;
    }

    @Operation(
            summary = "Met à jour un département",
            description = "Modifie les informations d'un département existant. Si une nouvelle image est fournie, l'ancienne est supprimée. Si le nom change, vérifie l'unicité du nouveau nom."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Département mis à jour avec succès"),
                    @ApiResponse(responseCode = "400", description = "Données invalides (validation échouée)"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "404", description = "Aucun département ou région trouvé avec l'ID fourni"),
                    @ApiResponse(responseCode = "409", description = "Le nouveau nom existe déjà"),
                    @ApiResponse(responseCode = "500", description = "Erreur lors de l'enregistrement de l'image")
            }
    )
    @PutMapping(path = "id/{id}", consumes = MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity <DivisionListDTO> update(@PathVariable int id, @RequestBody @Valid @ModelAttribute DivisionCreateDTO dto) throws IOException {
        DivisionListDTO updated = divisionService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Supprime un département par ID",
            description = "Supprime un département. La suppression échoue si le département contient encore des arrondissements."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Département supprimé avec succès"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "404", description = "Aucun département trouvé avec cet ID"),
                    @ApiResponse(responseCode = "409", description = "Impossible de supprimer un département contenant des arrondissements"),
                    @ApiResponse(responseCode = "500", description = "Erreur lors de la suppression de l'image")
            }
    )
    @DeleteMapping(path = "id/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity <Void> delete(@PathVariable int id){
        divisionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Supprime un département par nom",
            description = "Supprime un département en utilisant son nom. La suppression échoue si le département contient encore des arrondissements."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Département supprimé avec succès"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "404", description = "Aucun département trouvé avec ce nom"),
                    @ApiResponse(responseCode = "409", description = "Impossible de supprimer un département contenant des arrondissements"),
                    @ApiResponse(responseCode = "500", description = "Erreur lors de la suppression de l'image")
            }
    )
    @DeleteMapping(path = "name/{nom}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity <Void> delete(@PathVariable String nom){
        divisionService.delete(nom);
        return ResponseEntity.noContent().build();
    }
}
