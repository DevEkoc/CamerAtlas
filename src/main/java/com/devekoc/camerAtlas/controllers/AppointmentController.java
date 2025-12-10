package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.dto.appointment.AppointmentCreateDTO;
import com.devekoc.camerAtlas.dto.appointment.AppointmentListDTO;
import com.devekoc.camerAtlas.services.AppointmentService;
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

@Tag(name = "Nominations", description = "Gestion des nominations (affectations) des autorités aux circonscriptions")
@RestController
@AllArgsConstructor
@RequestMapping(value = "appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;

    @Operation(
            summary = "Crée une nomination",
            description = "Crée une nouvelle nomination, liant une autorité à une circonscription. " +
                    "Échoue si l'autorité est déjà en poste ou si la circonscription est déjà occupée."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Nomination créée avec succès"),
                    @ApiResponse(responseCode = "400", description = "Données invalides (validation échouée)"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "404", description = "L'autorité ou la circonscription spécifiée n'existe pas"),
                    @ApiResponse(responseCode = "409", description = "Le poste est déjà occupé ou l'autorité est déjà affectée ailleurs")
            }
    )
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AppointmentListDTO> create(@RequestBody @Valid AppointmentCreateDTO dto){
        AppointmentListDTO created = appointmentService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Crée plusieurs nominations en cascade",
            description = "Crée plusieurs nominations en une seule requête."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Nominations créées avec succès"),
                    @ApiResponse(responseCode = "400", description = "Données invalides (validation échouée)"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "404", description = "Une autorité ou une circonscription spécifiée n'existe pas"),
                    @ApiResponse(responseCode = "409", description = "Un poste est déjà occupé ou une autorité est déjà affectée ailleurs")
            }
    )
    @PostMapping(path = "cascade", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<List<AppointmentListDTO>> createSeveral(@RequestBody @Valid List<AppointmentCreateDTO> dtos){
        List<AppointmentListDTO> created = appointmentService.createSeveral(dtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Liste toutes les nominations",
            description = "Récupère la liste complète de toutes les nominations, actives et passées."
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
    public ResponseEntity <List<AppointmentListDTO>> listAll() {
        List<AppointmentListDTO> appointments = appointmentService.listAll();
        return ResponseEntity.ok(appointments);
    }

    @Operation(
            summary = "Met à jour une nomination",
            description = "Modifie les informations d'une nomination existante. Les mêmes vérifications que pour la création s'appliquent."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Nomination mise à jour avec succès"),
                    @ApiResponse(responseCode = "400", description = "Données invalides (validation échouée)"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "404", description = "La nomination, l'autorité ou la circonscription spécifiée n'existe pas"),
                    @ApiResponse(responseCode = "409", description = "Le poste est déjà occupé ou l'autorité est déjà affectée ailleurs")
            }
    )
    @PutMapping(path = "id/{id}", consumes =  APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity <AppointmentListDTO> update(@PathVariable int id, @RequestBody @Valid AppointmentCreateDTO dto) {
        AppointmentListDTO updated = appointmentService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Supprime une nomination par ID",
            description = "Supprime une nomination de la base de données."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Nomination supprimée avec succès"),
                    @ApiResponse(responseCode = "401", description = "Accès non authorisé au public ! Veuillez vous connecter"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit ! Vous n'avez pas le droit d'effectuer cette action"),
                    @ApiResponse(responseCode = "404", description = "Aucune nomination trouvée avec cet ID")
            }
    )
    @DeleteMapping(path = "id/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity <Void> delete(@PathVariable int id) {
        appointmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
