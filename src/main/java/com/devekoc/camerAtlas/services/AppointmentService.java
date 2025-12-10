package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.dto.appointment.AppointmentCreateDTO;
import com.devekoc.camerAtlas.dto.appointment.AppointmentListDTO;
import com.devekoc.camerAtlas.entities.Appointment;
import com.devekoc.camerAtlas.entities.Authority;
import com.devekoc.camerAtlas.entities.Circonscription;
import com.devekoc.camerAtlas.exceptions.PositionAlreadyFilledException;
import com.devekoc.camerAtlas.mappers.AppointmentMapper;
import com.devekoc.camerAtlas.repositories.AppointmentRepository;
import com.devekoc.camerAtlas.repositories.AuthorityRepository;
import com.devekoc.camerAtlas.repositories.CirconscriptionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final CirconscriptionRepository circonscriptionRepository;
    private final AuthorityRepository autoriteRepository;

    public AppointmentListDTO create(AppointmentCreateDTO dto) {
        Circonscription circonscription = circonscriptionRepository.findById(dto.circonscriptionId()).orElseThrow(
                ()-> new EntityNotFoundException("Aucune Circonscription trouvée pour l'ID : " + dto.circonscriptionId())
        );
        Authority authority = autoriteRepository.findById(dto.authorityId()).orElseThrow(
                ()-> new EntityNotFoundException("Aucune Autorité trouvée pour l'ID : " + dto.authorityId())
        );

        circonscriptionIsFree(null, circonscription);
        autoriteIsFree(null, authority);

        // Si les deux vérifications passent, on sauvegarde l'affectation.
        Appointment appointment = AppointmentMapper.fromCreateDTO(dto, new Appointment(), authority, circonscription);
        Appointment saved = appointmentRepository.save(appointment);
        return AppointmentMapper.toListDTO(saved);
    }

    public List<AppointmentListDTO> createSeveral(List<AppointmentCreateDTO> dtos) {
        List<AppointmentListDTO> appointments = new ArrayList<>();
        for (AppointmentCreateDTO dto : dtos) {
            appointments.add(create(dto));
        }
        return appointments;
    }

    public List<AppointmentListDTO> listAll() {
        return appointmentRepository.findAll().stream()
                .map(AppointmentMapper::toListDTO)
                .toList();
    }

    public AppointmentListDTO update(int id, AppointmentCreateDTO dto) {
        Appointment existing = appointmentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Aucune Affectation trouvée avec l'ID : " + id)
        );
        Circonscription circonscription = circonscriptionRepository.findById(dto.circonscriptionId()).orElseThrow(
                ()-> new EntityNotFoundException("Aucune Circonscription trouvée pour l'ID : " + dto.circonscriptionId())
        );
        Authority authority = autoriteRepository.findById(dto.authorityId()).orElseThrow(
                ()-> new EntityNotFoundException("Aucune Autorité trouvée pour l'ID : " + dto.authorityId())
        );

        circonscriptionIsFree(id, circonscription);
        autoriteIsFree(id, authority);
        AppointmentMapper.fromCreateDTO(dto, existing, authority, circonscription);
        Appointment updated = appointmentRepository.save(existing);
        return AppointmentMapper.toListDTO(existing);
    }

    public void delete(int id) {
        if (!appointmentRepository.existsById(id)) {
            throw new EntityNotFoundException("Aucune Affectation trouvée avec l'ID : " + id);
        }
        appointmentRepository.deleteById(id);
    }

    /**
     * Vérifie que la circonscription n'est pas déjà occupée par une appointment active.
     * @param idToExclude ID de l'appointment à exclure lors de la vérification (null pour création, ID pour modification)
     * @param circonscription La circonscription à vérifier
     * @throws PositionAlreadyFilledException si la circonscription est déjà occupée
     */
    public void circonscriptionIsFree (Integer idToExclude, Circonscription circonscription) {
        boolean occupe = (idToExclude == null)
                // idToExclude est null, donc il s'agit d'une création
                ? appointmentRepository.existsByCirconscriptionAndEndDateIsNull(circonscription)
                // idToExclude a une valeur, il s'agit d'une modification
                : appointmentRepository.existsByCirconscriptionAndEndDateIsNullAndIdNot(circonscription, idToExclude)
                ;

        if (occupe) {
            throw new PositionAlreadyFilledException(
                    "La circonscription '" + circonscription.getName()
                            + "' est déjà occupée par une autorité active."
            );
        }
    }

    /**
     * Vérifie que l'autorité n'est pas déjà en poste actif ailleurs.
     * @param idToExclude ID de l'appointment à exclure lors de la vérification (null pour création, ID pour modification)
     * @param autorite L'autorité à vérifier
     * @throws PositionAlreadyFilledException si l'autorité est déjà en poste actif
     */
    public void autoriteIsFree (Integer idToExclude, Authority autorite) {
        boolean occupe = (idToExclude == null)
                ? appointmentRepository.existsByAuthorityAndEndDateIsNull(autorite)
                : appointmentRepository.existsByAuthorityAndEndDateIsNullAndIdNot(autorite, idToExclude)
                ;
        // Règle métier 2 : Vérifier si l'autorité est déjà en poste ailleurs.
        if (occupe) {
            throw new PositionAlreadyFilledException(
                    "L'autorité '" + autorite.getName() + " " + autorite.getSurname() + "' est déjà en poste actif ailleurs."
            );
        }
    }
}
