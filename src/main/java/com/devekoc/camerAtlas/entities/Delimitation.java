package com.devekoc.camerAtlas.entities;

import com.devekoc.camerAtlas.entities.primaryKeys.DelimitationPK;
import jakarta.persistence.*;

@Entity
@Table(name = "delimitation")
public class Delimitation {
    @EmbeddedId
    private DelimitationPK id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("codeCirconscription")
    @JoinColumn(name = "codeCirconscription")
    private Circonscription circonscription;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("idFrontiere")
    @JoinColumn(name = "idFrontiere")
    private Frontiere frontiere;

    public Delimitation() {
    }

    /**
     * Constructeur de convenance pour créer une délimitation à partir des entités associées.
     * L'ID composite est créé automatiquement par JPA grâce à @MapsId.
     * @param frontiere L'entité Frontiere managée.
     * @param circonscription L'entité Circonscription managée.
     */
    public Delimitation(Frontiere frontiere, Circonscription circonscription) {
        this.frontiere = frontiere;
        this.circonscription = circonscription;
        this.id = new DelimitationPK(frontiere.getId(), circonscription.getId());
    }

    public DelimitationPK getId() {
        return id;
    }

    public void setId(DelimitationPK delimitationPK) {
        this.id = delimitationPK;
    }

    public Frontiere getFrontiere() {
        return frontiere;
    }

    public void setFrontiere(Frontiere frontiere) {
        this.frontiere = frontiere;
    }

    public Circonscription getCirconscription() {
        return circonscription;
    }

    public void setCirconscription(Circonscription circonscription) {
        this.circonscription = circonscription;
    }
}
