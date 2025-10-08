package com.devekoc.camerAtlas.entities;

import com.devekoc.camerAtlas.enumerations.TypeFrontiere;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "frontiere")
public class Frontiere {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idFrontiere")
    private int id;

    @Column(name = "typeFrontiere")
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Le type de frontière ne peut pas être vide !")
    private TypeFrontiere type;

    @Column(name = "limite")
    @NotBlank(message = "Le nom de la limite ne peut pas être vide !")
    private String limite;

    public Frontiere() {
    }

    public Frontiere(int id, TypeFrontiere type, String limite) {
        this.id = id;
        this.type = type;
        this.limite = limite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TypeFrontiere getType() {
        return type;
    }

    public void setType(TypeFrontiere type) {
        this.type = type;
    }

    public String getLimite() {
        return limite;
    }

    public void setLimite(String limite) {
        this.limite = limite;
    }
}
