package com.devekoc.camerAtlas.entities;

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
public class SubDivision extends Circonscription {
    // Sous-préfecture
    @Column(name = "sousPrefecture")
    private String subDivisionalOffice;

    @ManyToOne
    @JoinColumn(name = "idDepartement")
    private Division division;

    @OneToMany(mappedBy = "subDivisionalOffice")
    @JsonIgnore
    private List<Neighborhood> neighbourhoodsList = new ArrayList<>();

}
