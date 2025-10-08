package com.devekoc.camerAtlas.entities;

import com.devekoc.camerAtlas.enumerations.Fonction;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@Entity
@Table(name = "affectation")
public class Affectation {
    @Id
    @Column(name = "idAffectation")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JoinColumn(name = "idAutorite")
    @ManyToOne
    private Autorite autorite;

    @JoinColumn(name = "codeCirconscription")
    @ManyToOne
    private Circonscription circonscription;

    @Column(name = "fonction")
    @Enumerated(EnumType.STRING)
    private Fonction fonction;

    @Column(name = "dateDebut")
    @NotNull(message = "La date de début est obligatoire !")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate dateDebut;

    @Column(name = "dateFin")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate dateFin;

    public Affectation() {
    }

    public Affectation(int id, Autorite autorite, Circonscription circonscription, Fonction fonction, LocalDate dateDebut, LocalDate dateFin) {
        this.id = id;
        this.autorite = autorite;
        this.circonscription = circonscription;
        this.fonction = fonction;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

}
