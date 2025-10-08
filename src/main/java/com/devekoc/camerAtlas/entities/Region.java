package com.devekoc.camerAtlas.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private List<Departement> listeDepartements;

}
