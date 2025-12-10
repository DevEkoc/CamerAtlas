package com.devekoc.camerAtlas.entities;

import com.devekoc.camerAtlas.enumerations.Function;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "affectation")
@Setter @Getter
@AllArgsConstructor @NoArgsConstructor
//@RequiredArgsConstructor
public class Appointment {
    @Id
    @Column(name = "idAffectation")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "idAutorite", nullable = false)
    @NotNull(message = "L'autorité est obligatoire")
    private Authority authority;

    @ManyToOne
    @JoinColumn(name = "codeCirconscription", nullable = false)
    @NotNull(message = "La circonscription est obligatoire")
    private Circonscription circonscription;

    @Column(name = "fonction", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "La function est obligatoire")
    private Function function;

    @Column(name = "dateDebut", nullable = false)
    @NotNull(message = "La date de début est obligatoire !")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate startDate;

    @Column(name = "dateFin")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate endDate;
}
