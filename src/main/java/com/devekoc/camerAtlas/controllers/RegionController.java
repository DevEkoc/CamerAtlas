package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.dto.region.RegionCreateDTO;
import com.devekoc.camerAtlas.dto.region.RegionListDTO;
import com.devekoc.camerAtlas.dto.region.RegionListWithDivisionsDTO;
import com.devekoc.camerAtlas.services.RegionService;
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

@Tag(name = "Régions", description = "Gestion des régions du Cameroun")
@RestController
@AllArgsConstructor
@RequestMapping(value = "regions")
public class RegionController {
    private final RegionService  regionService;

    @Operation(
            summary = "Crée une région",
            description = "Crée une nouvelle région à partir des informations fournies. Accepte un fichier image en multipart/form-data."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Région créée avec succès"),
                    @ApiResponse(responseCode = "400", description = "Données invalides (validation échouée)"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "409", description = "Le nom fourni existe déjà"),
                    @ApiResponse(responseCode = "500", description = "Erreur lors de l'enregistrement de l'image")
            }
    )
    @PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RegionListDTO> create(@RequestBody @Valid @ModelAttribute RegionCreateDTO dto) throws IOException {
        RegionListDTO created = regionService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Crée plusieurs régions en cascade",
            description = "Crée plusieurs régions en une seule requête. Chaque région est créée séquentiellement. Si une région échoue, les suivantes ne seront pas créées."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Régions créées avec succès"),
                    @ApiResponse(responseCode = "400", description = "Données invalides (validation échouée)"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "409", description = "Un nom fourni existe déjà"),
                    @ApiResponse(responseCode = "500", description = "Erreur lors de l'enregistrement d'une image")
            }
    )
    @PostMapping(path = "cascade", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<List<RegionListDTO>> createSeveral(@RequestBody @Valid List<RegionCreateDTO> dtos) throws IOException {
        List<RegionListDTO> created = regionService.createSeveral(dtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Liste toutes les régions",
            description = "Récupère la liste complète de toutes les régions avec leurs nominations actives et délimitations frontalières."
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
    public ResponseEntity <List<RegionListDTO>> listAll() {
        List<RegionListDTO> regions = regionService.listAll();
        return ResponseEntity.ok(regions);
    }

    @Operation(
            summary = "Recherche une région par ID",
            description = "Récupère les détails d'une région spécifique à partir de son identifiant."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Région trouvée"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "404", description = "Aucune région trouvée avec cet ID"),
                    @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
            }
    )
    @GetMapping(path = "id/{id}", produces =  APPLICATION_JSON_VALUE)
    public ResponseEntity <RegionListDTO> find(@PathVariable int id) {
        RegionListDTO region = regionService.find(id);
        return (region != null)
                ? ResponseEntity.ok(region)
                : ResponseEntity.notFound().build()
        ;
    }

    @Operation(
            summary = "Recherche une région par nom",
            description = "Récupère les détails d'une région spécifique à partir de son nom exact."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Région trouvée"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "404", description = "Aucune région trouvée avec ce nom"),
                    @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
            }
    )
    @GetMapping(path = "name/{name}", produces =  APPLICATION_JSON_VALUE)
    public ResponseEntity <RegionListDTO> find(@PathVariable String name) {
        RegionListDTO region = regionService.find(name);
        return region != null
                ? ResponseEntity.ok(region)
                : ResponseEntity.notFound().build()
        ;
    }

    @Operation(
            summary = "Récupère une région avec ses départements",
            description = "Récupère les détails d'une région incluant la liste complète de tous ses départements associés."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Région et départements trouvés"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "404", description = "Aucune région trouvée avec cet ID"),
                    @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
            }
    )
    @GetMapping(path = "{id}/divisions", produces =  APPLICATION_JSON_VALUE)
    public ResponseEntity <RegionListWithDivisionsDTO> findWithDivisions(@PathVariable int id) {
        RegionListWithDivisionsDTO region = regionService.findWithDivisions(id);
        return (region != null)
                ? ResponseEntity.ok(region)
                : ResponseEntity.notFound().build()
        ;
    }

    @Operation(
            summary = "Met à jour une région",
            description = "Modifie les informations d'une région existante. Si une nouvelle image est fournie, l'ancienne est supprimée. Si le nom change, vérifie l'unicité du nouveau nom."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Région mise à jour avec succès"),
                    @ApiResponse(responseCode = "400", description = "Données invalides (validation échouée)"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "404", description = "Aucune région trouvée avec l'ID fourni"),
                    @ApiResponse(responseCode = "409", description = "Le nouveau nom existe déjà"),
                    @ApiResponse(responseCode = "500", description = "Erreur lors de l'enregistrement de l'image")
            }
    )
    @PutMapping(path = "id/{id}", consumes = MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity <RegionListDTO> update(@PathVariable int id, @RequestBody @Valid @ModelAttribute RegionCreateDTO dto) throws IOException {
        RegionListDTO updated = regionService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Supprime une région par ID",
            description = "Supprime une région. La suppression échoue si la région contient encore des départements."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Région supprimée avec succès"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "404", description = "Aucune région trouvée avec cet ID"),
                    @ApiResponse(responseCode = "409", description = "Impossible de supprimer une région qui contient des départements"),
                    @ApiResponse(responseCode = "500", description = "Erreur lors de la suppression de l'image")
            }
    )
    @DeleteMapping(path = "id/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity <Void> delete(@PathVariable int id){
        regionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Supprime une région par nom",
            description = "Supprime une région en utilisant son nom. La suppression échoue si la région contient encore des départements."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Région supprimée avec succès"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "404", description = "Aucune région trouvée avec ce nom"),
                    @ApiResponse(responseCode = "409", description = "Impossible de supprimer une région qui contient des départements"),
                    @ApiResponse(responseCode = "500", description = "Erreur lors de la suppression de l'image")
            }
    )
    @DeleteMapping(path = "name/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity <Void> delete(@PathVariable String name){
        regionService.delete(name);
        return ResponseEntity.noContent().build();
    }
}
