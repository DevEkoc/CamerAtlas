package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.dto.neighborhood.NeighborhoodCreateDTO;
import com.devekoc.camerAtlas.dto.neighborhood.NeighborhoodListDTO;
import com.devekoc.camerAtlas.services.NeighborhoodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "Quartiers", description = "Gestion des quartiers du Cameroun")
@RestController
@AllArgsConstructor
@RequestMapping(value = "neighborhoods")
public class NeighborhoodController {
    private final NeighborhoodService neighborhoodService;

    @Operation(
            summary = "Crée un quartier",
            description = "Crée un nouveau quartier à partir des informations fournies. Requiert l'ID d'un arrondissement existant."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Quartier créé avec succès"),
                    @ApiResponse(responseCode = "400", description = "Données invalides (validation échouée)"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "404", description = "L'id fourni pour l'Arrondissement n'existe pas"),
                    @ApiResponse(responseCode = "409", description = "Le nom de quartier fourni existe déjà")
            }
    )
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<NeighborhoodListDTO> create(@RequestBody @Valid NeighborhoodCreateDTO dto){
        NeighborhoodListDTO created = neighborhoodService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Crée plusieurs quartiers en cascade",
            description = "Crée plusieurs quartiers en une seule requête. Chaque quartier est créé séquentiellement. Si un quartier échoue, les suivants ne seront pas créés."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Quartiers créés avec succès"),
                    @ApiResponse(responseCode = "400", description = "Données invalides (validation échouée)"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "404", description = "L'id fourni pour un Arrondissement n'existe pas"),
                    @ApiResponse(responseCode = "409", description = "Un nom de quartier fourni existe déjà")
            }
    )
    @PostMapping(path = "cascade", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<List<NeighborhoodListDTO>> createSeveral(@RequestBody @Valid List<NeighborhoodCreateDTO> dtos) {
        List<NeighborhoodListDTO> created = neighborhoodService.createSeveral(dtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Liste tous les quartiers",
            description = "Récupère la liste complète de tous les quartiers."
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
    public ResponseEntity <List<NeighborhoodListDTO>> listAll() {
        List<NeighborhoodListDTO> neighborhoods = neighborhoodService.listAll();
        return ResponseEntity.ok(neighborhoods);
    }

    @Operation(
            summary = "Recherche un quartier par ID",
            description = "Récupère les détails d'un quartier spécifique à partir de son identifiant."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Quartier trouvé"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "404", description = "Aucun quartier trouvé avec cet ID")
            }
    )
    @GetMapping(path = "id/{id}", produces =  APPLICATION_JSON_VALUE)
    public ResponseEntity <NeighborhoodListDTO> find(@PathVariable int id) {
        NeighborhoodListDTO neighborhood = neighborhoodService.find(id);
        return (neighborhood != null)
                ? ResponseEntity.ok(neighborhood)
                : ResponseEntity.notFound().build()
        ;
    }

    @Operation(
            summary = "Recherche un quartier par nom",
            description = "Récupère les détails d'un quartier spécifique à partir de son nom exact."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Quartier trouvé"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "404", description = "Aucun quartier trouvé avec ce nom")
            }
    )
    @GetMapping(path = "name/{name}", produces =  APPLICATION_JSON_VALUE)
    public ResponseEntity <NeighborhoodListDTO> find(@PathVariable String name) {
        NeighborhoodListDTO neighborhood = neighborhoodService.find(name);
        return (neighborhood != null)
                ? ResponseEntity.ok(neighborhood)
                : ResponseEntity.notFound().build()
        ;
    }

    @Operation(
            summary = "Met à jour un quartier",
            description = "Modifie les informations d'un quartier existant. Si le nom change, vérifie l'unicité du nouveau nom."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Quartier mis à jour avec succès"),
                    @ApiResponse(responseCode = "400", description = "Données invalides (validation échouée)"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "404", description = "Aucun quartier ou arrondissement trouvé avec l'ID fourni"),
                    @ApiResponse(responseCode = "409", description = "Le nouveau nom existe déjà")
            }
    )
    @PutMapping(path = "id/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity <NeighborhoodListDTO> update(@PathVariable int id, @RequestBody @Valid NeighborhoodCreateDTO dto) {
        NeighborhoodListDTO updated = neighborhoodService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Supprime un quartier par ID",
            description = "Supprime un quartier de la base de données."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Quartier supprimé avec succès"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "404", description = "Aucun quartier trouvé avec cet ID")
            }
    )
    @DeleteMapping(path = "id/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity <Void> delete(@PathVariable int id){
        neighborhoodService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Supprime un quartier par nom",
            description = "Supprime un quartier de la base de données en utilisant son nom."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Quartier supprimé avec succès"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "404", description = "Aucun quartier trouvé avec ce nom")
            }
    )
    @DeleteMapping(path = "name/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity <Void> delete(@PathVariable String name){
        neighborhoodService.delete(name);
        return ResponseEntity.noContent().build();
    }
}
