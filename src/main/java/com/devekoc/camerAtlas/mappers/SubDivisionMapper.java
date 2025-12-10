package com.devekoc.camerAtlas.mappers;

import com.devekoc.camerAtlas.dto.api.AuthorityDetailsDTO;
import com.devekoc.camerAtlas.dto.api.DelimitationDetailsDTO;
import com.devekoc.camerAtlas.dto.neighborhood.NeighborhoodListDTO;
import com.devekoc.camerAtlas.dto.subDivision.SubDivisionCreateDTO;
import com.devekoc.camerAtlas.dto.subDivision.SubDivisionListDTO;
import com.devekoc.camerAtlas.dto.subDivision.SubDivisionWithNeighborhoodsDTO;
import com.devekoc.camerAtlas.entities.Appointment;
import com.devekoc.camerAtlas.entities.Delimitation;
import com.devekoc.camerAtlas.entities.Division;
import com.devekoc.camerAtlas.entities.SubDivision;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class SubDivisionMapper {

    public static SubDivision fromCreateDTO (SubDivisionCreateDTO dto, SubDivision subDivision, Division division, String imagePath) {
        subDivision.setName(dto.name());
        subDivision.setSubDivisionalOffice(dto.subDivisionalOffice());
        subDivision.setSurface(dto.surface());
        subDivision.setPopulation(dto.population());
        subDivision.setGpsCoordinates(dto.gpsCoordinates());
        subDivision.setDivision(division);
        subDivision.setImage(imagePath);

        return subDivision;
    }

    public static SubDivisionListDTO toListDTO(SubDivision subDivision, Map<Integer, Optional<Appointment>> appointments, Map<Integer, List<Delimitation>> delimitations) {
        AuthorityDetailsDTO subDivisionalOfficer = mapSubDivisionalOfficer(subDivision, appointments);
        List<DelimitationDetailsDTO> boundaries = mapBoundaries(subDivision, delimitations);
        String imageUrl = toSubDivisionImageUrl(subDivision.getImage());

        return new SubDivisionListDTO(
                subDivision.getId(),
                subDivision.getName(),
                subDivision.getSurface(),
                subDivision.getPopulation(),
                subDivision.getGpsCoordinates(),
                subDivision.getSubDivisionalOffice(),
                subDivision.getDivision().getId(),
                subDivision.getDivision().getName(),
                imageUrl,
                subDivisionalOfficer,
                boundaries
        );
    }

    public static SubDivisionWithNeighborhoodsDTO toSubDivisionWithNeighborhoods (SubDivision subDivision, Map<Integer, Optional<Appointment>> appointments, Map<Integer, List<Delimitation>> delimitations) {
        List<NeighborhoodListDTO> neighborhoods = subDivision.getNeighbourhoodsList().stream()
                .map(NeighborhoodMapper::toListDTO)
                .toList();
        AuthorityDetailsDTO subDivisionalOfficer = mapSubDivisionalOfficer(subDivision, appointments);
        List<DelimitationDetailsDTO> boundaries = mapBoundaries(subDivision, delimitations);
        String imageUrl = toSubDivisionImageUrl(subDivision.getImage());

        return new SubDivisionWithNeighborhoodsDTO(
                subDivision.getId(),
                subDivision.getName(),
                subDivision.getSurface(),
                subDivision.getPopulation(),
                subDivision.getGpsCoordinates(),
                subDivision.getSubDivisionalOffice(),
                subDivision.getDivision().getId(),
                subDivision.getDivision().getName(),
                imageUrl,
                subDivisionalOfficer,
                boundaries,
                neighborhoods
        );
    }
    private static AuthorityDetailsDTO mapSubDivisionalOfficer(SubDivision subDivision, Map<Integer, Optional<Appointment>> appointments) {
        Optional<Appointment> appointmentOpt = appointments.getOrDefault(subDivision.getId(), Optional.empty());
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

    private static List<DelimitationDetailsDTO> mapBoundaries(SubDivision subDivision, Map<Integer, List<Delimitation>> delimitations) {
        return delimitations.getOrDefault(subDivision.getId(), Collections.emptyList())
                .stream()
                .map(d -> new DelimitationDetailsDTO(d.getBorderType(), d.getBorderName()))
                .collect(Collectors.toList());
    }

    private static String toSubDivisionImageUrl(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) {
            return null;
        }
        String path = imagePath.replace("\\", "/");
        String fileName = path.substring(path.lastIndexOf("/") + 1);
        return "/media/SubDivisions/" + fileName;
    }
}
