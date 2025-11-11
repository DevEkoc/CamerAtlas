package com.devekoc.camerAtlas.entities;

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
    private String capital;

    @Column(name = "CodeMineralogique")
    @Size(min = 2, max = 2)
    private String mineralogicalCode;

    @OneToMany(mappedBy = "region")
    @JsonIgnore
    private List<Division> divisionsList = new ArrayList<>();

}
