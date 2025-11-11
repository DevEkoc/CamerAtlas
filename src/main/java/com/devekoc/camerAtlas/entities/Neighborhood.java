package com.devekoc.camerAtlas.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "quartier")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class Neighborhood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idQuartier")
    private int id;

    @Column(name = "nomQuartier")
    private String name;

    @Column(name = "nomPopulaire")
    private String popularName;

    @ManyToOne
    @JoinColumn(name = "sousPrefecture")
    private SubDivision subDivisionalOffice;

}
