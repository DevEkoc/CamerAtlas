
package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.exceptions.ImageDeletionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class MediaService {

    private final Path baseUploadPath;

    public MediaService(@Value("${app.upload.dir:uploads}") String uploadDir) {
        this.baseUploadPath = Paths.get(uploadDir);
    }

    /**
     * Enregistre une image sur le disque dans un sous-dossier spécifique.
     * @param image Le fichier image à enregistrer.
     * @param subDirectory Le sous-dossier (ex: "regions", "departements").
     * @return Le chemin complet vers le fichier enregistré, ou null si l'image est vide.
     */
    public String saveImage(MultipartFile image, String subDirectory) throws IOException {
        if (image == null || image.isEmpty()) {
            return null;
        }

        verifyImageType(image);
        Path uploadPath = baseUploadPath.resolve(subDirectory);
        Files.createDirectories(uploadPath);

        String extension = StringUtils.getFilenameExtension(image.getOriginalFilename());
        // Utilise le name du sous-dossier et un UUID pour un name de fichier unique et descriptif
        String fileName = subDirectory.substring(0, 1).toUpperCase() + subDirectory.substring(1, subDirectory.length() - 1) + "_" + UUID.randomUUID() + "." + extension;

        Path finalPath = uploadPath.resolve(fileName);
        Files.copy(image.getInputStream(), finalPath, StandardCopyOption.REPLACE_EXISTING);

        log.debug("Image enregistrée avec succès sous : {}", finalPath);
        return finalPath.toString();
    }

    /**
     * Supprime un fichier image du système de fichiers de manière sécurisée.
     * Ne lève pas d'exception si le chemin est nul, vide ou si le fichier n'existe pas.
     */
    public void deleteImage(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) {
            return; // Rien à supprimer
        }

        try {
            Path path = Paths.get(imagePath);
            if (Files.exists(path)) {
                Files.delete(path);
                log.debug("Image '{}' supprimée du système de fichiers.", imagePath);
            } else {
                log.warn("Tentative de suppression d'une image non existante : '{}'", imagePath);
            }
        } catch (IOException e) {
            log.error("Erreur lors de la suppression de l'image '{}': {}", imagePath, e.getMessage(), e);
            throw new ImageDeletionException("Impossible de supprimer l'ancienne image : " + imagePath, e);
        }
    }

    /**
     * Vérifie si le fichier est bien une image supportée.
     */
    private void verifyImageType(MultipartFile image) {
        String extension = StringUtils.getFilenameExtension(image.getOriginalFilename());
        if (extension == null) {
            throw new IllegalArgumentException("Le fichier n'a pas d'extension.");
        }

        List<String> authorized = List.of("jpg", "jpeg", "png", "webp");
        if (!authorized.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException("Type d'image non supporté : " + extension);
        }
    }
}
