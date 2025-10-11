package com.devekoc.camerAtlas.entities;

import com.devekoc.camerAtlas.dto.affectation.AffectationCreateDTO;
import com.devekoc.camerAtlas.dto.quartier.QuartierCreateDTO;
import com.devekoc.camerAtlas.enumerations.Fonction;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "affectation")
@Setter @Getter
@AllArgsConstructor @NoArgsConstructor
public class Affectation {
    @Id
    @Column(name = "idAffectation")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "idAutorite", nullable = false)
    @NotNull(message = "L'autorité est obligatoire")
    private Autorite autorite;

    @ManyToOne
    @JoinColumn(name = "codeCirconscription", nullable = false)
    @NotNull(message = "La circonscription est obligatoire")
    private Circonscription circonscription;

    @Column(name = "fonction", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "La fonction est obligatoire")
    private Fonction fonction;

    @Column(name = "dateDebut", nullable = false)
    @NotNull(message = "La date de début est obligatoire !")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate dateDebut;

    @Column(name = "dateFin")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate dateFin;

    public static Affectation fromCreateDTO (AffectationCreateDTO dto, Autorite autorite, Circonscription circonscription) {
        Affectation affectation = new Affectation();
        affectation.setAutorite(autorite);
        affectation.setCirconscription(circonscription);
        affectation.setFonction(dto.fonction());
        affectation.setDateDebut(dto.dateDebut());
        affectation.setDateFin(dto.dateFin());

        return affectation;
    }

    /**
     * Validation métier : la date de fin doit être après la date de début
     */
    @AssertTrue(message = "La date de fin doit être après la date de début")
    public boolean isDateFinValid() {
        return dateFin == null || dateDebut == null || dateFin.isAfter(dateDebut);
    }

    public void updateFromDTO(AffectationCreateDTO dto, Autorite autorite, Circonscription circonscription) {
        this.autorite = autorite;
        this.circonscription = circonscription;
        this.fonction = dto.fonction();
        this.dateDebut = dto.dateDebut();
        this.dateFin = dto.dateFin();
    }
}
