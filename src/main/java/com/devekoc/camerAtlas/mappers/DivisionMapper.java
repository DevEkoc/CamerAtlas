package com.devekoc.camerAtlas.mappers;

import com.devekoc.camerAtlas.dto.api.AuthorityDetailsDTO;
import com.devekoc.camerAtlas.dto.api.DelimitationDetailsDTO;
import com.devekoc.camerAtlas.dto.division.DivisionCreateDTO;
import com.devekoc.camerAtlas.dto.division.DivisionListDTO;
import com.devekoc.camerAtlas.dto.division.DivisionWithSubDivisionsDTO;
import com.devekoc.camerAtlas.dto.subDivision.SubDivisionListDTO;
import com.devekoc.camerAtlas.entities.Appointment;
import com.devekoc.camerAtlas.entities.Delimitation;
import com.devekoc.camerAtlas.entities.Division;
import com.devekoc.camerAtlas.entities.Region;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class DivisionMapper {

    public static Division fromCreateDTO(DivisionCreateDTO dto, Division division, Region region, String imagePath) {
        division.setName(dto.name());
        division.setDivisionalOffice(dto.divisionalOffice());
        division.setSurface(dto.surface());
        division.setPopulation(dto.population());
        division.setGpsCoordinates(dto.gpsCoordinates());
        division.setRegion(region);
        division.setImage(imagePath);
        return division;
    }

    public static DivisionListDTO toListDTO(Division division,
                                            Map<Integer, Optional<Appointment>> appointments,
                                            Map<Integer, List<Delimitation>> delimitations) {
        AuthorityDetailsDTO seniorDivisionalOfficer = mapSeniorDivisionalOfficer(division, appointments);
        List<DelimitationDetailsDTO> boundaries = mapBoundaries(division, delimitations);
        String imageUrl = toDivisionImageUrl(division.getImage());

        return new DivisionListDTO(
                division.getId(),
                division.getName(),
                division.getSurface(),
                division.getPopulation(),
                division.getGpsCoordinates(),
                division.getDivisionalOffice(),
                division.getRegion().getId(),
                division.getRegion().getName(),
                imageUrl,
                seniorDivisionalOfficer,
                boundaries
        );
    }

    public static DivisionWithSubDivisionsDTO toDivisionWithSubDivisions (Division division, Optional<Appointment> seniorDivisionalOfficer, List<Delimitation> regionBoundaries, Map<Integer, Optional<Appointment>> subDivisionAppointments, Map<Integer, List<Delimitation>> subDivisionDelimitations) {
        List<SubDivisionListDTO> subDivisions = division.getSubDivisionsList().stream()
                .map(s -> SubDivisionMapper.toListDTO(s, subDivisionAppointments, subDivisionDelimitations))
                .toList();

        return new DivisionWithSubDivisionsDTO (
                division.getId(),
                division.getName(),
                division.getSurface(),
                division.getPopulation(),
                division.getGpsCoordinates(),
                division.getDivisionalOffice(),
                division.getRegion().getId(),
                division.getRegion().getName(),
                toDivisionImageUrl(division.getImage()),
                mapSeniorDivisionalOfficer(seniorDivisionalOfficer),
                mapBoundaries(regionBoundaries),
                subDivisions
        );

    }

    private static AuthorityDetailsDTO mapSeniorDivisionalOfficer(Division division, Map<Integer, Optional<Appointment>> appointments) {
        Optional<Appointment> appointmentOpt = appointments.getOrDefault(division.getId(), Optional.empty());
        return mapSeniorDivisionalOfficer(appointmentOpt);
    }

    private static AuthorityDetailsDTO mapSeniorDivisionalOfficer(Optional<Appointment> appointmentOpt) {
        if (appointmentOpt.isEmpty()) {
            return null;
        }
        Appointment appointment = appointmentOpt.get();
        return new AuthorityDetailsDTO(
                appointment.getAuthority().getName(),
                appointment.getAuthority().getSurname(),
                appointment.getAuthority().getDateOfBirth(),
                appointment.getFunction().toString(),
                appointment.getStartDate(),
                appointment.getEndDate()
        );
    }

    private static List<DelimitationDetailsDTO> mapBoundaries(Division division, Map<Integer, List<Delimitation>> delimitations) {
        return mapBoundaries(delimitations.getOrDefault(division.getId(), Collections.emptyList()));
    }

    private static List<DelimitationDetailsDTO> mapBoundaries(List<Delimitation> delimitations) {
        return delimitations
                .stream()
                .map(d -> new DelimitationDetailsDTO(d.getBorderType(), d.getBorderName()))
                .toList();
    }

    private static String toDivisionImageUrl(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) {
            return null;
        }
        String path = imagePath.replace("\\", "/");
        String fileName = path.substring(path.lastIndexOf("/") + 1);
        return "/media/divisions/" + fileName;
    }
}