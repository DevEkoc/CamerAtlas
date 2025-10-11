package com.devekoc.camerAtlas.entities;

import com.devekoc.camerAtlas.dto.arrondissement.ArrondissementCreateDTO;
import com.devekoc.camerAtlas.dto.quartier.QuartierCreateDTO;
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

    public static Quartier fromCreateDTO(QuartierCreateDTO dto, Arrondissement arrondissement) {
        Quartier quartier = new Quartier();
        quartier.setNom(dto.nom());
        quartier.setNomPopulaire(dto.nomPopulaire());
        quartier.setSousPrefecture(arrondissement);

        return quartier;
    }

    public void updateFromDTO(QuartierCreateDTO dto, Arrondissement arrondissement) {
        this.setNom(dto.nom());
        this.setNomPopulaire(dto.nomPopulaire());
        this.setSousPrefecture(arrondissement);
    }
}
