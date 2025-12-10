package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.dto.subDivision.SubDivisionCreateDTO;
import com.devekoc.camerAtlas.dto.subDivision.SubDivisionListDTO;
import com.devekoc.camerAtlas.dto.subDivision.SubDivisionWithNeighborhoodsDTO;
import com.devekoc.camerAtlas.services.SubDivisionService;
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

@Tag(name = "Arrondissements", description = "Gestion des arrondissements du Cameroun")
@RestController
@AllArgsConstructor
@RequestMapping(value = "subDivisions")
public class SubDivisionController {
    private final SubDivisionService subDivisionService;

    @Operation(
            summary = "Crée un arrondissement",
            description = "Crée un nouvel arrondissement à partir des informations fournies. Accepte un fichier image en multipart/form-data."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Arrondissement créé avec succès"),
                    @ApiResponse(responseCode = "400", description = "Données invalides (validation échouée)"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "404", description = "L'id fourni pour le Département n'existe pas"),
                    @ApiResponse(responseCode = "409", description = "Le nom fourni existe déjà"),
                    @ApiResponse(responseCode = "500", description = "Erreur lors de l'enregistrement de l'image")
            }
    )
    @PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<SubDivisionListDTO> create(@RequestBody @Valid @ModelAttribute SubDivisionCreateDTO dto) throws IOException {
        SubDivisionListDTO created = subDivisionService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Crée plusieurs arrondissements en cascade",
            description = "Crée plusieurs arrondissements en une seule requête. Chaque arrondissement est créé séquentiellement. Si un arrondissement échoue, les suivants ne seront pas créés."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Arrondissements créés avec succès"),
                    @ApiResponse(responseCode = "400", description = "Données invalides (validation échouée)"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "404", description = "L'id fourni pour un Département n'existe pas"),
                    @ApiResponse(responseCode = "409", description = "Un nom fourni existe déjà"),
                    @ApiResponse(responseCode = "500", description = "Erreur lors de l'enregistrement d'une image")
            }
    )
    @PostMapping(path = "cascade", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<List<SubDivisionListDTO>> createSeveral(@RequestBody @Valid List<SubDivisionCreateDTO> dtos) throws IOException {
        List<SubDivisionListDTO> created = subDivisionService.createSeveral(dtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Liste tous les arrondissements",
            description = "Récupère la liste complète de tous les arrondissements avec leurs nominations actives et délimitations frontalières."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès (peut être vide)"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
            }
    )
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SubDivisionListDTO>> listAll() {
        List<SubDivisionListDTO> subDivisions = subDivisionService.listAll();
        return ResponseEntity.ok(subDivisions);
    }

    @Operation(
            summary = "Recherche un arrondissement par ID",
            description = "Récupère les détails d'un arrondissement spécifique à partir de son identifiant."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Arrondissement trouvé"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "404", description = "Aucun arrondissement trouvé avec cet ID"),
                    @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
            }
    )
    @GetMapping(path = "id/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<SubDivisionListDTO> find(@PathVariable int id) {
        SubDivisionListDTO subDivision = subDivisionService.find(id);
        return (subDivision != null)
                ? ResponseEntity.ok(subDivision)
                : ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "Recherche un arrondissement par nom",
            description = "Récupère les détails d'un arrondissement spécifique à partir de son nom exact."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Arrondissement trouvé"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "404", description = "Aucun arrondissement trouvé avec ce nom"),
                    @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
            }
    )
    @GetMapping(path = "name/{name}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<SubDivisionListDTO> find(@PathVariable String name) {
        SubDivisionListDTO subDivision = subDivisionService.find(name);
        return (subDivision != null)
                ? ResponseEntity.ok(subDivision)
                : ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "Récupère un arrondissement avec ses quartiers",
            description = "Récupère les détails d'un arrondissement incluant la liste complète de tous ses quartiers associés."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Arrondissement et quartiers trouvés"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "404", description = "Aucun arrondissement trouvé avec cet ID"),
                    @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
            }
    )
    @GetMapping(path = "{id}/neighborhoods")
    public ResponseEntity<SubDivisionWithNeighborhoodsDTO> findWithNeighborhoods(@PathVariable int id) {
        SubDivisionWithNeighborhoodsDTO subDivisions = subDivisionService.findWithNeighborhoods(id);
        return subDivisions != null
                ? ResponseEntity.ok(subDivisions)
                : ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "Met à jour un arrondissement",
            description = "Modifie les informations d'un arrondissement existant. Si une nouvelle image est fournie, l'ancienne est supprimée. Si le nom change, vérifie l'unicité du nouveau nom."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Arrondissement mis à jour avec succès"),
                    @ApiResponse(responseCode = "400", description = "Données invalides (validation échouée)"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "404", description = "Aucun arrondissement ou département trouvé avec l'ID fourni"),
                    @ApiResponse(responseCode = "409", description = "Le nouveau nom existe déjà"),
                    @ApiResponse(responseCode = "500", description = "Erreur lors de l'enregistrement de l'image")
            }
    )
    @PutMapping(path = "id/{id}", consumes = MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<SubDivisionListDTO> update(@PathVariable int id, @RequestBody @Valid @ModelAttribute SubDivisionCreateDTO dto) throws IOException {
        SubDivisionListDTO updated = subDivisionService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Supprime un arrondissement par ID",
            description = "Supprime un arrondissement. La suppression échoue si l'arrondissement contient encore des quartiers."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Arrondissement supprimé avec succès"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "404", description = "Aucun arrondissement trouvé avec cet ID"),
                    @ApiResponse(responseCode = "409", description = "Impossible de supprimer un arrondissement contenant des quartiers"),
                    @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
            }
    )
    @DeleteMapping(path = "id/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable int id) {
        subDivisionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Supprime un arrondissement par nom",
            description = "Supprime un arrondissement en utilisant son nom. La suppression échoue si l'arrondissement contient encore des quartiers."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Arrondissement supprimé avec succès"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "404", description = "Aucun arrondissement trouvé avec ce nom"),
                    @ApiResponse(responseCode = "409", description = "Impossible de supprimer un arrondissement contenant des quartiers"),
                    @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
            }
    )
    @DeleteMapping(path = "name/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable String name) {
        subDivisionService.delete(name);
        return ResponseEntity.noContent().build();
    }
}