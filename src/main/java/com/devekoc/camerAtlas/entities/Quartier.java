package com.devekoc.camerAtlas.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "quartier")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
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

}
