package com.devekoc.camerAtlas.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Tag(name = "Médias", description = "Gestion et consultation des fichiers médias (images) associés aux entités")
@RestController
@RequestMapping("media")
public class MediaController {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    // Liste blanche des sous-répertoires autorisés
    private static final List<String> ALLOWED_SUBSEQUENT_DIRECTORIES = Arrays.asList("regions", "divisions", "subDivisions");

    @Operation(
            summary = "Récupère un fichier média",
            description = "Permet d'accéder aux fichiers images stockés sur le serveur, en spécifiant un sous-répertoire et un nom de fichier. Seuls les sous-répertoires autorisés sont accessibles."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Fichier média récupéré avec succès"),
                    @ApiResponse(responseCode = "400", description = "Requête invalide (par exemple, tentative de path traversal ou URL mal formée)"),
                    @ApiResponse(responseCode = "404", description = "Sous-répertoire non autorisé ou fichier non trouvé")
            }
    )
    @GetMapping("/{subDirectory}/{fileName:.+}")
    public ResponseEntity<Resource> getImage(
            @PathVariable String subDirectory,
            @PathVariable String fileName) {
        try {
            // Vérifier que le sous-répertoire est autorisé pour éviter le path traversal
            if (!ALLOWED_SUBSEQUENT_DIRECTORIES.contains(subDirectory)) {
                return ResponseEntity.notFound().build();
            }

            // Construire le chemin vers le fichier dans le sous-répertoire spécifié
            Path targetImagesDir = Paths.get(uploadDir, subDirectory);
            Path filePath = targetImagesDir.resolve(fileName).normalize();

            // Vérification de sécurité supplémentaire : s'assurer que le fichier est bien sous le répertoire cible
            if (!filePath.startsWith(targetImagesDir)) {
                return ResponseEntity.badRequest().build();
            }

            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = null;
                try {
                    contentType = Files.probeContentType(filePath); // Détermine le type MIME
                } catch (IOException e) {
                    // Log the error, but continue
                }
                if (contentType == null) {
                    contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE; // Type par défaut si non détecté
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}