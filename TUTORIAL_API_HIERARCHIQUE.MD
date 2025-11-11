# Tutoriel : Créer un Endpoint Hiérarchique avec Entités Sémantiques

L'objectif est de créer un endpoint `GET /api/regions/{id}` qui retourne une réponse JSON riche contenant la région, son gouverneur, et la liste de ses départements avec leurs propres détails (préfet, frontières).

## Étape 1 : Définir les DTOs (Data Transfer Objects)

Nous avons besoin de nouveaux `records` Java pour représenter précisément la structure JSON désirée. Crée un nouveau package `com.devekoc.camerAtlas.dto.api` et ajoutes-y les fichiers suivants :

**1. DTO pour l'Autorité (Gouverneur/Préfet)**
Ce DTO sera réutilisable.
`AutoriteSimpleDTO.java`
```java
package com.devekoc.camerAtlas.dto.api;

public record AutoriteSimpleDTO(
    String nom,
    String fonction
) {}
```

**2. DTO pour les Frontières**
`FrontiereDTO.java`
```java
package com.devekoc.camerAtlas.dto.api;

public record FrontiereDTO(
    String direction,
    String nomCirconscriptionVoisine
) {}
```

**3. DTO détaillé pour une Division**
`DivisionDetailDTO.java`
```java
package com.devekoc.camerAtlas.dto.api;

import java.util.List;

public record DivisionDetailDTO(
    Integer id,
    String nom,
    Integer superficie,
    Integer population,
    String chefLieu,
    AutoriteSimpleDTO prefet,
    List<FrontiereDTO> frontieres
) {}
```

**4. DTO principal pour la Région**
Ce sera le DTO racine de notre réponse JSON.
`RegionDetailDTO.java`
```java
package com.devekoc.camerAtlas.dto.api;

import java.util.List;

public record RegionDetailDTO(
    Integer id,
    String nom,
    Integer superficie,
    Integer population,
    String chefLieu,
    String codeMineralogique,
    AutoriteSimpleDTO gouverneur,
    String imgUrl,
    List<DivisionDetailDTO> divisions
) {}
```

## Étape 2 : Ajouter les méthodes de recherche aux Repositories

Pour trouver facilement le gouverneur, le préfet ou les frontières, nous devons ajouter des méthodes spécifiques à nos repositories JPA.

**1. Dans `AffectationRepository.java`**
Ajoute cette méthode pour trouver une affectation active (sans date de fin) pour une circonscription et une fonction données.
```java
import java.util.Optional;
import com.devekoc.camerAtlas.entities.Circonscription;
import com.devekoc.camerAtlas.enumerations.Fonction;

// ... dans l'interface AffectationRepository
Optional<Affectation> findByCirconscriptionAndFonctionAndEndDateIsNull(Circonscription circonscription, Fonction fonction);
```

**2. Dans `DelimitationRepository.java`**
Ajoute cette méthode pour trouver toutes les frontières d'une circonscription.
```java
import java.util.List;
import com.devekoc.camerAtlas.entities.Circonscription;

// ... dans l'interface DelimitationRepository
List<Delimitation> findByCirconscription(Circonscription circonscription);
```

## Étape 3 : Créer la logique dans un nouveau Mapper

La logique de transformation est complexe. Il est préférable de créer une nouvelle classe `ApiMapper.java` dans le package `com.devekoc.camerAtlas.mappers` pour gérer cette orchestration.

`ApiMapper.java`

```java
package com.devekoc.camerAtlas.mappers;

import com.devekoc.camerAtlas.dto.api.*;
import com.devekoc.camerAtlas.entities.*;
import com.devekoc.camerAtlas.enumerations.Fonction;
import com.devekoc.camerAtlas.repositories.AffectationRepository;
import com.devekoc.camerAtlas.repositories.DelimitationRepository;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ApiMapper {

    private final AffectationRepository affectationRepository;
    private final DelimitationRepository delimitationRepository;

    public ApiMapper(AffectationRepository affectationRepository, DelimitationRepository delimitationRepository) {
        this.affectationRepository = affectationRepository;
        this.delimitationRepository = delimitationRepository;
    }

    public RegionDetailDTO toRegionDetailDTO(Region region) {
        // 1. Trouver le gouverneur
        AutoriteSimpleDTO gouverneur = affectationRepository.findByCirconscriptionAndFonctionAndEndDateIsNull(region, Fonction.GOUVERNEUR)
                .map(affectation -> new AutoriteSimpleDTO(
                        affectation.getAuthority().getName() + " " + affectation.getAuthority().getSurname(),
                        affectation.getFonction().name()
                ))
                .orElse(null);

        // 2. Mapper les départements
        List<DivisionDetailDTO> divisionDTOs = region.getDivisionsList().stream()
                .map(this::toDivisionDetailDTO)
                .collect(Collectors.toList());

        // 3. Construire l'URL de l'image
        String imageUrl = (region.getImage() != null) ? "/api/media/regions/" + region.getImage().substring(region.getImage().replace("\", " / ").lastIndexOf(" / ") + 1) : null;

        return new RegionDetailDTO(
                region.getId(),
                region.getName(),
                region.getSurface(),
                region.getPopulation(),
                region.getCapital(),
                region.getMineralogicalCode(),
                gouverneur,
                imageUrl,
                divisionDTOs
        );
    }

    private DivisionDetailDTO toDivisionDetailDTO(Division division) {
        // 1. Trouver le préfet
        AutoriteSimpleDTO prefet = affectationRepository.findByCirconscriptionAndFonctionAndEndDateIsNull(division, Fonction.PREFET)
                .map(affectation -> new AutoriteSimpleDTO(
                        affectation.getAuthority().getName() + " " + affectation.getAuthority().getSurname(),
                        affectation.getFonction().name()
                ))
                .orElse(null);

        // 2. Trouver les frontières
        List<FrontiereDTO> frontiereDTOs = delimitationRepository.findByCirconscription(division).stream()
                .map(delimitation -> new FrontiereDTO(
                        delimitation.getBorderType().name(),
                        delimitation.getBorderName() // Note: ceci est un simple String dans l'entité actuelle
                ))
                .collect(Collectors.toList());

        return new DivisionDetailDTO(
                division.getId(),
                division.getName(),
                division.getSurface(),
                division.getPopulation(),
                division.getDivisionalOffice(),
                prefet,
                frontiereDTOs
        );
    }
}
```

## Étape 4 : Mettre à jour le Service

Injecte `ApiMapper` dans `RegionService` et crée une nouvelle méthode.

Dans `RegionService.java` :
```java
// ... imports
import com.devekoc.camerAtlas.dto.api.RegionDetailDTO;
import com.devekoc.camerAtlas.mappers.ApiMapper;

@Service
public class RegionService {
    private final RegionRepository regionRepository;
    private final MediaService mediaService;
    private final ApiMapper apiMapper; // <-- INJECTER LE NOUVEAU MAPPER

    public RegionService(RegionRepository regionRepository, MediaService mediaService, ApiMapper apiMapper) {
        this.regionRepository = regionRepository;
        this.mediaService = mediaService;
        this.apiMapper = apiMapper; // <-- AJOUTER AU CONSTRUCTEUR
    }

    // ... autres méthodes

    // NOUVELLE MÉTHODE
    public RegionDetailDTO findRegionDetails(int id) {
        Region region = regionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aucune région trouvée avec l'ID : " + id));
        
        return apiMapper.toRegionDetailDTO(region);
    }
}
```

## Étape 5 : Exposer le nouvel Endpoint

Enfin, modifie `RegionController.java` pour utiliser cette nouvelle méthode de service. Tu peux modifier l'endpoint existant `/id/{id}` pour qu'il retourne cette nouvelle structure.

Dans `RegionController.java` :
```java
// ... imports
import com.devekoc.camerAtlas.dto.api.RegionDetailDTO;

@RestController
@RequestMapping(value = "region")
public class RegionController {
    // ... constructeur

    // MODIFIER CETTE MÉTHODE
    @GetMapping(path = "id/{id}", produces =  APPLICATION_JSON_VALUE)
    public ResponseEntity<RegionDetailDTO> find(@PathVariable int id) { // Changer le type de retour
        RegionDetailDTO regionDetails = regionService.findRegionDetails(id);
        return ResponseEntity.ok(regionDetails);
    }
    
    // ... autres méthodes
}
```

Et voilà ! En suivant ces étapes, la route `GET /api/region/id/{id}` retournera la structure JSON hiérarchique et sémantique que tu as définie.
