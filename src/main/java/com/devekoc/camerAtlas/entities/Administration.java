package com.devekoc.camerAtlas.entities;

import com.devekoc.camerAtlas.enumerations.Fonction;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

@Entity
public class Administration {
    @Id
    @Column(name = "idAdministration")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JoinColumn(name = "idAdministrateur")
    @OneToOne(cascade = CascadeType.ALL)
    private Administrateur administrateur;

    @JoinColumn(name = "codeCirconscription")
    @OneToOne(cascade = CascadeType.ALL)
    private Circonscription circonscription;

    @Column(name = "fonction")
    private Fonction fonction;

    @Column(name = "dateDebut")
    @NotBlank
    private LocalDate dateDebut;

    @Column(name = "dateFin")
    private LocalDate dateFin;

    public Administration() {
    }

    public Administration(int id, Administrateur administrateur, Circonscription circonscription, Fonction fonction, LocalDate dateDebut, LocalDate dateFin) {
        this.id = id;
        this.administrateur = administrateur;
        this.circonscription = circonscription;
        this.fonction = fonction;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Administrateur getAdministrateur() {
        return administrateur;
    }

    public void setAdministrateur(Administrateur administrateur) {
        this.administrateur = administrateur;
    }

    public Circonscription getCirconscription() {
        return circonscription;
    }

    public void setCirconscription(Circonscription circonscription) {
        this.circonscription = circonscription;
    }

    public Fonction getFonction() {
        return fonction;
    }

    public void setFonction(Fonction fonction) {
        this.fonction = fonction;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }
}
