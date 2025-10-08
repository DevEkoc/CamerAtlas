package com.devekoc.camerAtlas.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @OneToMany(mappedBy = "arrondissement")
    @JsonIgnore
    private List<Quartier> listeQuartiers;

}
