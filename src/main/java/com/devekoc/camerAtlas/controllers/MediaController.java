package com.devekoc.camerAtlas.controllers;

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

@RestController
@RequestMapping("media")
public class MediaController {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    // Liste blanche des sous-répertoires autorisés
    private static final List<String> ALLOWED_SUBSEQUENT_DIRECTORIES = Arrays.asList("regions", "divisions", "subDivisions");

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