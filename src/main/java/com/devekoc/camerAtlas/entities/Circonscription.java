package com.devekoc.camerAtlas.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "circonscription")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public abstract class Circonscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codeCirconscription")
    protected Integer id;

    @Column(name = "nomCirconscription", unique = true)
    @NotBlank(message = "Le nom ne doit pas être vide !")
    @Size(min = 1, max = 50, message = "Le nom doit contenir entre 1 et 50 caractères")
    protected String nom;

    @Column
    @NotNull(message = "La superficie ne doit pas être vide !")
    @Positive(message = "La superficie doit être positive")
    protected Integer superficie;

    @Column
    @NotNull(message = "La population ne doit pas être vide !")
    @Positive(message = "La population doit être positive")
    protected Integer population;

    @Column(name = "coordonneesGPS")
    protected String coordonnees;

}
