package com.devekoc.camerAtlas.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "circonscription")
public class Circonscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codeCirconscription")
    private int id;

    @Column(name = "nomCirconscription")
    @NotBlank(message = "Le nom ne doit pas être vide !")
    @Size(min = 1, max = 50)
    private String nom;

    @Column
    @NotBlank(message = "La superficie ne doit pas être vide !")
    private int superficie;

    @Column
    @NotBlank(message = "La population ne doit pas être vide !")
    private int population;

    @Column(name = "coordonneesGPS")
    private String coordonnees;

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
