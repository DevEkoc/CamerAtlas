package com.devekoc.camerAtlas.repositories;

import com.devekoc.camerAtlas.entities.Appointment;
import com.devekoc.camerAtlas.entities.Authority;
import com.devekoc.camerAtlas.entities.Circonscription;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    /**
     * Vérifie s'il existe une affectation active (sans date de fin) pour une circonscription donnée.
     * @param circonscription La circonscription à vérifier.
     * @return true si une affectation active existe, false sinon.
     */
    boolean existsByCirconscriptionAndEndDateIsNull(Circonscription circonscription);

    boolean existsByCirconscriptionAndEndDateIsNullAndIdNot(Circonscription circonscription, int id);

    /**
     * Vérifie s'il existe une affectation active (sans date de fin) pour une autorité donnée.
     * @param authority L'autorité à vérifier.
     * @return true si une affectation active existe, false sinon.
     */
    boolean existsByAuthorityAndEndDateIsNull(Authority authority);

    boolean existsByAuthorityAndEndDateIsNullAndIdNot(Authority authority, int id);


    Optional <Appointment> findByCirconscription(@NotNull(message = "La circonscription est obligatoire") Circonscription circonscription);

    List<Appointment> findByCirconscription_IdInAndEndDateIsNull(List<Integer> circonscriptionIds);
}
