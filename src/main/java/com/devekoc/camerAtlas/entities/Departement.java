package com.devekoc.camerAtlas.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private List<Arrondissement> listeArrondissements;

}
