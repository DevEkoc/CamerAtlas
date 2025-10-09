package com.devekoc.camerAtlas.entities;

import com.devekoc.camerAtlas.enumerations.TypeFrontiere;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "frontiere")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class Frontiere {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idFrontiere")
    private Integer id;

    @Column(name = "typeFrontiere")
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Le type de frontière ne peut pas être vide !")
    private TypeFrontiere type;

    @Column(name = "limite")
    @NotBlank(message = "Le nom de la limite ne peut pas être vide !")
    private String limite;

}
