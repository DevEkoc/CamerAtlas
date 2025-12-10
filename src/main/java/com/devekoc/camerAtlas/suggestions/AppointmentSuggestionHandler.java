package com.devekoc.camerAtlas.suggestions;

import com.devekoc.camerAtlas.dto.appointment.AppointmentCreateDTO;
import com.devekoc.camerAtlas.enumerations.TargetType;
import com.devekoc.camerAtlas.services.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentSuggestionHandler implements SuggestionHandler<AppointmentCreateDTO> {

    private final AppointmentService appointmentService;

    @Override
    public void create(AppointmentCreateDTO dto) {
        appointmentService.create(dto);
    }

    @Override
    public void update(int id, AppointmentCreateDTO dto) {
        appointmentService.update(id, dto);
    }

    @Override
    public void delete(int id) {
        appointmentService.delete(id);
    }

    @Override
    public Class<AppointmentCreateDTO> dtoType() {
        return AppointmentCreateDTO.class;
    }

    @Override
    public TargetType handledType() {
        return TargetType.APPOINTMENT;
    }
}

