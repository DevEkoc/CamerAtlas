package com.devekoc.camerAtlas.entities.primaryKeys;

import java.io.Serializable;
import java.util.Objects;

public class DelimitationPK implements Serializable {
    private int codeCirconscription;
    private int idFrontiere;

    public DelimitationPK() {
    }

    public DelimitationPK(int codeCirconscription, int idFrontiere) {
        this.codeCirconscription = codeCirconscription;
        this.idFrontiere = idFrontiere;
    }

    public int getIdFrontiere() {
        return idFrontiere;
    }

    public void setIdFrontiere(int idFrontiere) {
        this.idFrontiere = idFrontiere;
    }

    public int getCodeCirconscription() {
        return codeCirconscription;
    }

    public void setCodeCirconscription(int codeCirconscription) {
        this.codeCirconscription = codeCirconscription;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DelimitationPK that = (DelimitationPK) o;
        return idFrontiere == that.idFrontiere && codeCirconscription == that.codeCirconscription;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idFrontiere, codeCirconscription);
    }
}
