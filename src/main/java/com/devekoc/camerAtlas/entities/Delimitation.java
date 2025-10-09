package com.devekoc.camerAtlas.entities;

import com.devekoc.camerAtlas.entities.primaryKeys.DelimitationPK;
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

}
