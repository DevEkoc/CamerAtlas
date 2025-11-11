package com.devekoc.camerAtlas.mappers;

import com.devekoc.camerAtlas.dto.appointment.AppointmentCreateDTO;
import com.devekoc.camerAtlas.dto.appointment.AppointmentListDTO;
import com.devekoc.camerAtlas.entities.Appointment;
import com.devekoc.camerAtlas.entities.Authority;
import com.devekoc.camerAtlas.entities.Circonscription;

public class AppointmentMapper {

    public static Appointment fromCreateDTO (AppointmentCreateDTO dto, Appointment appointment, Authority authority, Circonscription circonscription) {
        appointment.setAuthority(authority);
        appointment.setCirconscription(circonscription);
        appointment.setFonction(dto.fonction());
        appointment.setStartDate(dto.startDate());
        appointment.setEndDate(dto.endDate());
        return appointment;
    }

    public static AppointmentListDTO toListDTO(Appointment appointment) {
        return new AppointmentListDTO(
                appointment.getId(),
                appointment.getAuthority().getId(),
                appointment.getCirconscription().getId(),
                appointment.getFonction(),
                appointment.getStartDate(),
                appointment.getEndDate()
        );
    }
}
