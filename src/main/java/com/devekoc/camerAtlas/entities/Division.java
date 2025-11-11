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
@Table(name = "departement")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Division extends Circonscription {
    // Préfecture
    @Column(name = "prefecture")
    private String divisionalOffice;

    @ManyToOne
    @JoinColumn(name = "idRegion")
    private Region region;

    @OneToMany(mappedBy = "division")
    @JsonIgnore
    private List<SubDivision> subDivisionsList = new ArrayList<>();

}
