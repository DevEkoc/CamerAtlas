package com.devekoc.camerAtlas.entities;

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
@Table(name = "departement")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Departement extends Circonscription {
    @Column(name = "prefecture")
    private String prefecture;

    @ManyToOne
    @JoinColumn(name = "idRegion")
    private Region region;

    @OneToMany(mappedBy = "departement")
    @JsonIgnore
    private List<Arrondissement> listeArrondissements = new ArrayList<>();

    public static Departement fromCreateDTO(DepartementCreateDTO dto, Region region) {
        Departement departement = new Departement();
        departement.setNom(dto.nom());
        departement.setSuperficie(dto.superficie());
        departement.setPopulation(dto.population());
        departement.setCoordonnees(dto.coordonnees());
        departement.setPrefecture(dto.prefecture());
        departement.setRegion(region);

        return departement;
    }

    public void updateFromDTO(DepartementCreateDTO dto, Region region) {
        this.nom = dto.nom();
        this.superficie = dto.superficie();
        this.population = dto.population();
        this.coordonnees = dto.coordonnees();
        this.prefecture = dto.prefecture();
        this.region = region;
    }
}
