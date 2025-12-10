package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.dto.suggestion.SuggestionCreateDTO;
import com.devekoc.camerAtlas.dto.suggestion.SuggestionListDTO;
import com.devekoc.camerAtlas.services.SuggestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Suggestions", description = "Gestion des suggestions de création, modification ou suppression d'entités")
@RestController
@RequestMapping("suggestions")
@RequiredArgsConstructor
public class SuggestionController {

    private final SuggestionService suggestionService;

    @Operation(
            summary = "Crée une suggestion",
            description = "Soumet une nouvelle suggestion pour la création, la modification ou la suppression d'une entité. " +
                    "Le `payload` doit contenir un JSON correspondant au DTO de création de l'entité cible."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Suggestion soumise avec succès"),
                    @ApiResponse(responseCode = "400", description = "Données invalides (payload JSON incorrect, type cible non géré, ou violation de contraintes)"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action")
            }
    )
    @PostMapping
    public ResponseEntity<SuggestionListDTO> create(@RequestBody @Valid SuggestionCreateDTO dto) {
        return ResponseEntity.ok(suggestionService.create(dto));
    }

    @Operation(
            summary = "Liste toutes les suggestions",
            description = "Récupère la liste complète de toutes les suggestions, quel que soit leur statut."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès (peut être vide)"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
            }
    )
    @GetMapping
    public ResponseEntity<List<SuggestionListDTO>> listAll() {
        return ResponseEntity.ok(suggestionService.listAll());
    }

    @Operation(
            summary = "Recherche une suggestion par ID",
            description = "Récupère les détails d'une suggestion spécifique à partir de son identifiant."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Suggestion trouvée"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "404", description = "Aucune suggestion trouvée avec cet ID")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<SuggestionListDTO> find(@PathVariable int id) {
        return ResponseEntity.ok(suggestionService.find(id));
    }

    @Operation(
            summary = "Approuve une suggestion",
            description = "Approuve une suggestion en attente. L'action (création, modification, suppression) est alors appliquée à la base de données."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Suggestion approuvée et appliquée avec succès"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "404", description = "Aucune suggestion trouvée avec cet ID"),
                    @ApiResponse(responseCode = "409", description = "La suggestion a déjà été traitée, ou un conflit métier empêche son application (ex: nom dupliqué)")
            }
    )
    @PostMapping("/{id}/approve")
    public ResponseEntity<Void> approve(@PathVariable int id) {
        suggestionService.approve(id);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Rejette une suggestion",
            description = "Rejette une suggestion en attente. Aucune action n'est appliquée à la base de données."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Suggestion rejetée avec succès"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "404", description = "Aucune suggestion trouvée avec cet ID"),
                    @ApiResponse(responseCode = "409", description = "La suggestion a déjà été traitée")
            }
    )
    @PostMapping("/{id}/reject")
    public ResponseEntity<Void> reject(@PathVariable int id) {
        suggestionService.reject(id);
        return ResponseEntity.ok().build();
    }
}
