package com.devekoc.camerAtlas.mappers;

import com.devekoc.camerAtlas.dto.api.*;
import com.devekoc.camerAtlas.dto.division.DivisionListDTO;
import com.devekoc.camerAtlas.dto.region.*;
import com.devekoc.camerAtlas.entities.*;
import java.util.*;

public final class RegionMapper {

    public static Region fromCreateDTO(RegionCreateDTO dto, Region region, String imagePath) {
        region.setName(dto.name());
        region.setCapital(dto.capital());
        region.setSurface(dto.surface());
        region.setPopulation(dto.population());
        region.setGpsCoordinates(dto.gpsCoordinates());
        region.setMineralogicalCode(dto.mineralogicalCode());
        region.setImage(imagePath);
        return region;
    }

    public static RegionListDTO toListDTO(Region region, Optional<Appointment> appointment, List<Delimitation> delimitations) {
        return new RegionListDTO(
                region.getId(),
                region.getName(),
                region.getSurface(),
                region.getPopulation(),
                region.getGpsCoordinates(),
                region.getCapital(),
                region.getMineralogicalCode(),
                toRegionImageUrl(region.getImage()),
                mapGovernor(appointment),
                toDelimitationDetails(delimitations)
        );
    }

    public static RegionListWithDivisionsDTO toRegionWithDivisionsDTO(
            Region region,
            Optional<Appointment> governor,
            List<Delimitation> regionBoundaries,
            Map<Integer, Optional<Appointment>> divisionAppointments,
            Map<Integer, List<Delimitation>> divisionDelimitations) {

        List<DivisionListDTO> divisions = region.getDivisionsList().stream()
                .map(d -> DivisionMapper.toListDTO(d, divisionAppointments, divisionDelimitations))
                .toList();

        return new RegionListWithDivisionsDTO(
                region.getId(),
                region.getName(),
                region.getSurface(),
                region.getPopulation(),
                region.getGpsCoordinates(),
                region.getCapital(),
                region.getMineralogicalCode(),
                toRegionImageUrl(region.getImage()),
                mapGovernor(governor),
                toDelimitationDetails(regionBoundaries),
                divisions
        );
    }

    /** Conversion d’une liste de délimitations en DTOs simples */
    private static List<DelimitationDetailsDTO> toDelimitationDetails(List<Delimitation> delimitations) {
        return delimitations.stream()
                .map(d -> new DelimitationDetailsDTO(d.getBorderType(), d.getBorderName()))
                .toList();
    }

    /** Conversion d’un Appointment optionnel vers un DTO d’autorité */
    private static AuthorityDetailsDTO mapGovernor(Optional<Appointment> appointmentOpt) {
        return appointmentOpt
                .map(a -> new AuthorityDetailsDTO(
                        a.getAuthority().getName(),
                        a.getAuthority().getSurname(),
                        a.getAuthority().getDateOfBirth(),
                        a.getFonction().toString(),
                        a.getStartDate(),
                        a.getEndDate()
                ))
                .orElse(null);
    }

    /** Conversion du chemin d’image vers une URL relative */
    private static String toRegionImageUrl(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) return null;
        String normalized = imagePath.replace("\\", "/");
        return "/media/regions/" + normalized.substring(normalized.lastIndexOf('/') + 1);
    }
}
