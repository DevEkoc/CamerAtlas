package com.devekoc.camerAtlas.entities;

import com.devekoc.camerAtlas.dto.region.RegionCreateDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "region")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Region extends Circonscription {
    @Column(name = "chefLieu")
    private String chefLieu;

    @Column(name = "CodeMineralogique")
    @Size(min = 2, max = 2)
    private String codeMineralogique;

    @OneToMany(mappedBy = "region")
    @JsonIgnore
    private List<Departement> listeDepartements = new ArrayList<>();

    public static Region fromCreateDTO (RegionCreateDTO dto) {
        Region region = new Region();
        region.setNom(dto.nom());
        region.setPopulation(dto.population());
        region.setSuperficie(dto.superficie());
        region.setCoordonnees(dto.coordonnees());
        region.setChefLieu(dto.chefLieu());
        region.setCodeMineralogique(dto.codeMineralogique());
        return region;
    }

    public void updateFromDTO(RegionCreateDTO dto) {
        this.nom = dto.nom();
        this.population = dto.population();
        this.superficie = dto.superficie();
        this.coordonnees = dto.coordonnees();
        this.setCodeMineralogique(dto.codeMineralogique());
        this.chefLieu = dto.chefLieu();
    }
}
