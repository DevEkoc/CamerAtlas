package com.devekoc.camerAtlas.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MediaServiceTest {

    @TempDir
    Path tempDir; // Répertoire temporaire fourni par JUnit

    private MediaService mediaService;

    @BeforeEach
    void setUp() {
        mediaService = new MediaService(tempDir.toString());
    }

    // -------------------------------------------------------
    // saveImage
    // -------------------------------------------------------

    @Test
    void saveImage_shouldSaveFileSuccessfully() throws Exception {
        // Mock d’une image PNG
        MultipartFile image = mock(MultipartFile.class);
        when(image.isEmpty()).thenReturn(false);
        when(image.getOriginalFilename()).thenReturn("test.png");

        byte[] content = "fake image data".getBytes();
        when(image.getInputStream()).thenReturn(new ByteArrayInputStream(content));

        String savedPath = mediaService.saveImage(image, "regions");

        // Vérifications
        assertThat(savedPath).isNotNull();
        Path finalPath = Paths.get(savedPath);

        assertThat(Files.exists(finalPath)).isTrue();
        assertThat(Files.readAllBytes(finalPath)).isEqualTo(content);

        // Vérifie que le fichier est bien dans /regions/
        assertThat(finalPath.getParent().getFileName().toString()).isEqualTo("regions");
    }

    @Test
    void saveImage_shouldReturnNullWhenFileIsEmpty() throws Exception {
        MultipartFile image = mock(MultipartFile.class);
        when(image.isEmpty()).thenReturn(true);

        String result = mediaService.saveImage(image, "regions");

        assertThat(result).isNull();
    }

    @Test
    void saveImage_shouldThrowExceptionWhenUnsupportedExtension() {
        MultipartFile image = mock(MultipartFile.class);
        when(image.isEmpty()).thenReturn(false);
        when(image.getOriginalFilename()).thenReturn("file.exe"); // extension interdite

        assertThatThrownBy(() -> mediaService.saveImage(image, "regions"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Type d'image non supporté");
    }

    // -------------------------------------------------------
    // deleteImage
    // -------------------------------------------------------

    @Test
    void deleteImage_shouldDeleteFileSuccessfully() throws Exception {
        Path file = tempDir.resolve("toDelete.png");
        Files.write(file, "data".getBytes());

        assertThat(Files.exists(file)).isTrue();

        mediaService.deleteImage(file.toString());

        assertThat(Files.exists(file)).isFalse();
    }

    @Test
    void deleteImage_shouldIgnoreNullOrBlank() {
        // Pas d’exception
        mediaService.deleteImage(null);
        mediaService.deleteImage("");
        mediaService.deleteImage("  ");
    }

    @Test
    void deleteImage_shouldNotFailWhenFileDoesNotExist() {
        // Pas d’exception malgré fichier inexistant
        mediaService.deleteImage(tempDir.resolve("missing.png").toString());
    }
}

