package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.dto.appointment.AppointmentCreateDTO;
import com.devekoc.camerAtlas.dto.appointment.AppointmentListDTO;
import com.devekoc.camerAtlas.entities.Appointment;
import com.devekoc.camerAtlas.entities.Authority;
import com.devekoc.camerAtlas.entities.Circonscription;
import com.devekoc.camerAtlas.entities.Region;
import com.devekoc.camerAtlas.enumerations.Function;
import com.devekoc.camerAtlas.exceptions.PositionAlreadyFilledException;
import com.devekoc.camerAtlas.repositories.AppointmentRepository;
import com.devekoc.camerAtlas.repositories.AuthorityRepository;
import com.devekoc.camerAtlas.repositories.CirconscriptionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private CirconscriptionRepository circonscriptionRepository;

    @Mock
    private AuthorityRepository authorityRepository;

    @InjectMocks
    private AppointmentService service;

    Circonscription circonscription;
    Authority authority;
    Appointment appointment;

    @BeforeEach
    void init() {
        circonscription = new Region();
        circonscription.setId(10);
        circonscription.setName("Mfoundi");

        authority = new Authority();
        authority.setId(5);
        authority.setName("Paul");
        authority.setSurname("Biya");

        appointment = new Appointment();
        appointment.setId(1);
        appointment.setAuthority(authority);
        appointment.setCirconscription(circonscription);
        appointment.setStartDate(LocalDate.of(2024,1,1));
        appointment.setFunction(Function.GOUVERNEUR);
    }

    @Test
    void create_shouldCreateAppointmentSuccessfully() {

        AppointmentCreateDTO dto = new AppointmentCreateDTO(
                5, 10, Function.GOUVERNEUR,
                LocalDate.of(2024,1,1),
                null
        );

        when(circonscriptionRepository.findById(10)).thenReturn(Optional.of(circonscription));
        when(authorityRepository.findById(5)).thenReturn(Optional.of(authority));
        when(appointmentRepository.existsByCirconscriptionAndEndDateIsNull(circonscription))
                .thenReturn(false);
        when(appointmentRepository.existsByAuthorityAndEndDateIsNull(authority))
                .thenReturn(false);
        when(appointmentRepository.save(any())).thenReturn(appointment);

        AppointmentListDTO result = service.create(dto);

        assertThat(result.appointmentId()).isEqualTo(1);
        assertThat(result.circonscriptionId()).isEqualTo(10);
        assertThat(result.authorityId()).isEqualTo(5);

        verify(appointmentRepository).save(any());
    }

    @Test
    void create_shouldThrow_whenCirconscriptionAlreadyOccupied() {

        AppointmentCreateDTO dto = new AppointmentCreateDTO(
                5, 10, Function.GOUVERNEUR,
                LocalDate.now(), null
        );

        when(circonscriptionRepository.findById(10)).thenReturn(Optional.of(circonscription));
        when(authorityRepository.findById(5)).thenReturn(Optional.of(authority));

        when(appointmentRepository.existsByCirconscriptionAndEndDateIsNull(circonscription))
                .thenReturn(true);

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(PositionAlreadyFilledException.class)
                .hasMessageContaining("La circonscription");
    }

    @Test
    void create_shouldThrow_whenAuthorityAlreadyInPost() {

        AppointmentCreateDTO dto = new AppointmentCreateDTO(
                5, 10, Function.GOUVERNEUR,
                LocalDate.now(), null
        );

        when(circonscriptionRepository.findById(10)).thenReturn(Optional.of(circonscription));
        when(authorityRepository.findById(5)).thenReturn(Optional.of(authority));

        when(appointmentRepository.existsByCirconscriptionAndEndDateIsNull(circonscription))
                .thenReturn(false);
        when(appointmentRepository.existsByAuthorityAndEndDateIsNull(authority))
                .thenReturn(true);

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(PositionAlreadyFilledException.class)
                .hasMessageContaining("L'autorité");
    }

    @Test
    void listAll_shouldReturnAllAppointments() {
        when(appointmentRepository.findAll()).thenReturn(List.of(appointment));
        List<AppointmentListDTO> result = service.listAll();
        assertThat(result).hasSize(1);
    }

    @Test
    void update_shouldUpdateSuccessfully() {
        AppointmentCreateDTO dto = new AppointmentCreateDTO(
                5, 10, Function.PREFET,
                LocalDate.of(2023,1,1),
                null
        );

        when(appointmentRepository.findById(1)).thenReturn(Optional.of(appointment));
        when(circonscriptionRepository.findById(10)).thenReturn(Optional.of(circonscription));
        when(authorityRepository.findById(5)).thenReturn(Optional.of(authority));

        when(appointmentRepository.existsByCirconscriptionAndEndDateIsNullAndIdNot(circonscription, 1))
                .thenReturn(false);
        when(appointmentRepository.existsByAuthorityAndEndDateIsNullAndIdNot(authority, 1))
                .thenReturn(false);

        when(appointmentRepository.save(any())).thenReturn(appointment);

        AppointmentListDTO result = service.update(1, dto);

        assertThat(result.appointmentId()).isEqualTo(1);
        verify(appointmentRepository).save(any());
    }

    @Test
    void update_shouldThrow_whenAppointmentNotFound() {

        AppointmentCreateDTO dto = new AppointmentCreateDTO(
                5, 10, Function.GOUVERNEUR,
                LocalDate.now(), null
        );

        when(appointmentRepository.findById(1)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.update(1, dto))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void delete_shouldDeleteSuccessfully() {
        when(appointmentRepository.existsById(1)).thenReturn(true);
        service.delete(1);
        verify(appointmentRepository).deleteById(1);
    }

    @Test
    void delete_shouldThrow_whenAppointmentNotFound() {
        when(appointmentRepository.existsById(1)).thenReturn(false);
        assertThatThrownBy(() -> service.delete(1))
                .isInstanceOf(EntityNotFoundException.class);
    }
}

