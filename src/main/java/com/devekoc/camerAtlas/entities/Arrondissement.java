package com.devekoc.camerAtlas.entities;

import com.devekoc.camerAtlas.dto.arrondissement.ArrondissementCreateDTO;
import com.devekoc.camerAtlas.dto.departement.DepartementCreateDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "arrondissement")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Arrondissement extends Circonscription {
    @Column(name = "sousPrefecture")
    private String sousPrefecture;

    @ManyToOne
    @JoinColumn(name = "idDepartement")
    private Departement departement;

    @OneToMany(mappedBy = "sousPrefecture")
    @JsonIgnore
    private List<Quartier> listeQuartiers = new ArrayList<>();

    public static Arrondissement fromCreateDTO(ArrondissementCreateDTO dto, Departement departement) {
        Arrondissement arrondissement = new Arrondissement();
        arrondissement.setNom(dto.nom());
        arrondissement.setSuperficie(dto.superficie());
        arrondissement.setPopulation(dto.population());
        arrondissement.setCoordonnees(dto.coordonnees());
        arrondissement.setSousPrefecture(dto.sousPrefecture());
        arrondissement.setDepartement(departement);

        return arrondissement;
    }

    public void updateFromDTO(ArrondissementCreateDTO dto, Departement departement) {
        this.nom = dto.nom();
        this.superficie = dto.superficie();
        this.population = dto.population();
        this.coordonnees = dto.coordonnees();
        this.sousPrefecture = dto.sousPrefecture();
        this.departement = departement;
    }
}
