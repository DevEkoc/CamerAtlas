package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.entities.Frontiere;
import com.devekoc.camerAtlas.enumerations.TypeFrontiere;
import com.devekoc.camerAtlas.repositories.FrontiereRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FrontiereServiceTest {

    @Mock
    private FrontiereRepository frontiereRepository;

    @InjectMocks
    private FrontiereService frontiereService;

    @Test
    void doit_appeler_le_repository_pour_creer_une_frontiere() {
        Frontiere frontiere = new Frontiere(null, TypeFrontiere.SUD, "Limite Sud");

        frontiereService.creer(frontiere);

        verify(frontiereRepository, times(1)).save(frontiere);
    }

    @Test
    void doit_retourner_la_liste_des_frontieres_renvoyee_par_le_repository() {
        Frontiere frontiere = new Frontiere(1, TypeFrontiere.EST, "Limite Est");
        when(frontiereRepository.findAll()).thenReturn(Collections.singletonList(frontiere));

        List<Frontiere> resultat = frontiereService.lister();

        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getType()).isEqualTo(TypeFrontiere.EST);
        verify(frontiereRepository, times(1)).findAll();
    }

    @Test
    void doit_retourner_la_liste_des_frontieres_par_type_renvoyee_par_le_repository() {
        TypeFrontiere type = TypeFrontiere.OUEST;
        Frontiere frontiere = new Frontiere(1, type, "Limite Ouest");
        when(frontiereRepository.findByType(type)).thenReturn(Collections.singletonList(frontiere));

        List<Frontiere> resultat = frontiereService.rechercherParType(type);

        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getType()).isEqualTo(type);
        verify(frontiereRepository, times(1)).findByType(type);
    }
}