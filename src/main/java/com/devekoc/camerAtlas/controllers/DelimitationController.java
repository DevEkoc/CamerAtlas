package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.dto.delimitation.DelimitationCreateDTO;
import com.devekoc.camerAtlas.dto.delimitation.DelimitationListDTO;
import com.devekoc.camerAtlas.services.DelimitationService;
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

@Tag(name = "Délimitations", description = "Gestion des délimitations (frontières) des circonscriptions")
@RestController
@AllArgsConstructor
@RequestMapping(value = "delimitations")
public class DelimitationController {
    private final DelimitationService delimitationService;

    @Operation(
            summary = "Crée une délimitation",
            description = "Crée une nouvelle délimitation (frontière) pour une circonscription."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Délimitation créée avec succès"),
                    @ApiResponse(responseCode = "400", description = "Données invalides (validation échouée)"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "404", description = "La circonscription spécifiée n'existe pas")
            }
    )
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<DelimitationListDTO> create(@RequestBody @Valid DelimitationCreateDTO dto) {
        DelimitationListDTO created = delimitationService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Crée plusieurs délimitations en cascade",
            description = "Crée plusieurs délimitations en une seule requête."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Délimitations créées avec succès"),
                    @ApiResponse(responseCode = "400", description = "Données invalides (validation échouée)"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "404", description = "Une des circonscriptions spécifiées n'existe pas")
            }
    )
    @PostMapping(path = "cascade", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<List<DelimitationListDTO>> createSeveral(@RequestBody @Valid List<DelimitationCreateDTO> dtos) {
        List<DelimitationListDTO> delimitations = delimitationService.createSeveral(dtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(delimitations);
    }

    @Operation(
            summary = "Liste toutes les délimitations",
            description = "Récupère la liste complète de toutes les délimitations."
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
    public ResponseEntity<List<DelimitationListDTO>> listAll() {
        List<DelimitationListDTO> delimitations = delimitationService.listAll();
        return ResponseEntity.ok(delimitations);
    }

    @Operation(
            summary = "Supprime une délimitation par ID",
            description = "Supprime une délimitation de la base de données."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Délimitation supprimée avec succès"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "404", description = "Aucune délimitation trouvée avec cet ID")
            }
    )
    @DeleteMapping(path = "id/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable int id) {
        delimitationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
