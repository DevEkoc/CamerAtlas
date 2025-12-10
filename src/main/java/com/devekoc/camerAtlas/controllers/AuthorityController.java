package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.dto.authority.AuthorityCreateDTO;
import com.devekoc.camerAtlas.dto.authority.AuthorityListDTO;
import com.devekoc.camerAtlas.services.AuthorityService;
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

@Tag(name = "Autorités", description = "Gestion des autorités (personnes physiques)")
@RestController
@AllArgsConstructor
@RequestMapping(value = "authorities")
public class AuthorityController {
    private final AuthorityService authorityService;

    @Operation(
            summary = "Crée une autorité",
            description = "Crée une nouvelle personne (autorité) dans la base de données."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Autorité créée avec succès"),
                    @ApiResponse(responseCode = "400", description = "Données invalides (validation échouée)"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action")
            }
    )
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AuthorityListDTO> create(@RequestBody @Valid AuthorityCreateDTO autorite) {
        AuthorityListDTO created = authorityService.create(autorite);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Crée plusieurs autorités en cascade",
            description = "Crée plusieurs personnes (autorités) en une seule requête."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Autorités créées avec succès"),
                    @ApiResponse(responseCode = "400", description = "Données invalides (validation échouée)"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action")
            }
    )
    @PostMapping(path ="cascade", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<List<AuthorityListDTO>> createSeveral(@RequestBody @Valid List<AuthorityCreateDTO> autorites) {
        List<AuthorityListDTO> created = authorityService.createSeveral(autorites);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Liste toutes les autorités",
            description = "Récupère la liste complète de toutes les personnes (autorités)."
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
    public ResponseEntity<List<AuthorityListDTO>> listAll() {
        List<AuthorityListDTO> autorites = authorityService.listAll();
        return ResponseEntity.ok(autorites);
    }

    @Operation(
            summary = "Recherche une autorité par ID",
            description = "Récupère les détails d'une autorité spécifique à partir de son identifiant."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Autorité trouvée"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "404", description = "Aucune autorité trouvée avec cet ID")
            }
    )
    @GetMapping(path = "id/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthorityListDTO> find(@PathVariable int id) {
        AuthorityListDTO autorite = authorityService.find(id);
        return (autorite != null)
                ? ResponseEntity.ok(autorite)
                : ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "Met à jour une autorité",
            description = "Modifie les informations d'une autorité existante."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Autorité mise à jour avec succès"),
                    @ApiResponse(responseCode = "400", description = "Données invalides (validation échouée)"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "404", description = "Aucune autorité trouvée avec l'ID fourni")
            }
    )
    @PutMapping(path = "id/{id}", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<AuthorityListDTO> update(@PathVariable int id, @RequestBody @Valid AuthorityCreateDTO dto) {
        AuthorityListDTO updated = authorityService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Supprime une autorité par ID",
            description = "Supprime une autorité de la base de données. Attention : ne vérifie pas si l'autorité est actuellement nommée à un poste."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Autorité supprimée avec succès"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "404", description = "Aucune autorité trouvée avec cet ID")
            }
    )
    @DeleteMapping(path = "id/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable int id) {
        authorityService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
