package com.devekoc.camerAtlas.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "quartier")
public class Quartier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idQuartier")
    private int id;

    @Column(name = "nomQuartier")
    private String nom;

    @Column(name = "nomPopulaire")
    private String nomPopulaire;

    @ManyToOne
    @JoinColumn(name = "sousPrefecture")
    private Arrondissement sousPrefecture;

    public Quartier() {
    }

    public Quartier(int id, String nom, String nomPopulaire, Arrondissement sousPrefecture) {
        this.id = id;
        this.nom = nom;
        this.nomPopulaire = nomPopulaire;
        this.sousPrefecture = sousPrefecture;
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

    public String getNomPopulaire() {
        return nomPopulaire;
    }

    public void setNomPopulaire(String nomPopulaire) {
        this.nomPopulaire = nomPopulaire;
    }

    public Arrondissement getSousPrefecture() {
        return sousPrefecture;
    }

    public void setSousPrefecture(Arrondissement sousPrefecture) {
        this.sousPrefecture = sousPrefecture;
    }
}
