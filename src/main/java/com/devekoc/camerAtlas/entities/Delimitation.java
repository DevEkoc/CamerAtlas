package com.devekoc.camerAtlas.entities;

import com.devekoc.camerAtlas.enumerations.BorderType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "delimitation")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class Delimitation {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "idDelimitation")
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "codeCirconscription")
    private Circonscription circonscription;

    @Column(name = "typeFrontiere")
    @Enumerated(EnumType.STRING)
    private BorderType borderType;

    @Column(name = "frontiere")
    private String borderName;


}
