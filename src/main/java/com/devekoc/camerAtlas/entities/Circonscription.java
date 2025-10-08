package com.devekoc.camerAtlas.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "circonscription")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter @Setter
@AllArgsConstructor
public abstract class Circonscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codeCirconscription")
    protected int id;

    @Column(name = "nomCirconscription", unique = true)
    @NotBlank(message = "Le nom ne doit pas être vide !")
    @Size(min = 1, max = 50)
    protected String nom;

    @Column
    @NotNull(message = "La superficie ne doit pas être vide !")
    protected Integer superficie;

    @Column
    @NotNull(message = "La population ne doit pas être vide !")
    protected Integer population;

    @Column(name = "coordonneesGPS")
    protected String coordonnees;

    public Circonscription() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getSuperficie() {
        return superficie;
    }

    public void setSuperficie(int superficie) {
        this.superficie = superficie;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public String getCoordonnees() {
        return coordonnees;
    }

    public void setCoordonnees(String coordonnees) {
        this.coordonnees = coordonnees;
    }
}
