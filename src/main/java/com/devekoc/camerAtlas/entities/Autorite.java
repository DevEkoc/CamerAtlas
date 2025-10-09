package com.devekoc.camerAtlas.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "autorite")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class Autorite {
    @Column(name = "idAutorite")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nomAutorite", nullable = false, length = 50)
    @NotBlank(message = "Le nom ne peut pas être vide !")
    private String nom;

    @Column(name = "prenomAutorite", length = 50)
    private String prenom;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @Column
    //Pour les dates on utilise @NotNull(message = "")
    @NotNull(message = "La date de naissance est obligatoire")
    @Past(message = "La date de naissance doit être dans le passé")
    private LocalDate dateNaissance;

}