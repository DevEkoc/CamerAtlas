package com.devekoc.camerAtlas.repositories;

import com.devekoc.camerAtlas.entities.Departement;
import com.devekoc.camerAtlas.entities.Region;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
public class DepartementRepositoryTest {

    @Autowired
    private DepartementRepository departementRepository;

    @Autowired
    private RegionRepository regionRepository;

    private Region region;

    @BeforeEach
    void setUp() {
        departementRepository.deleteAll();
        regionRepository.deleteAll();
        region = new Region();
        region.setNom("Est");
        region.setSuperficie(109002);
        region.setPopulation(835600);
        region.setChefLieu("Bertoua");
        regionRepository.save(region);
    }

    @Test
    void doitEnregistrerUnDepartement() {
        Departement departement = new Departement();
        departement.setNom("Lom-et-Djerem");
        departement.setSuperficie(26345);
        departement.setPopulation(228700);
        departement.setPrefecture("Bertoua");
        departement.setRegion(region);

        Departement savedDepartement = departementRepository.save(departement);

        assertThat(savedDepartement).isNotNull();
        assertThat(savedDepartement.getId()).isNotNull();
        assertThat(savedDepartement.getNom()).isEqualTo("Lom-et-Djerem");
    }

    @Test
    void doitListerLesDepartements() {
        Departement departement1 = new Departement();
        departement1.setNom("Lom-et-Djerem");
        departement1.setSuperficie(26345);
        departement1.setPopulation(228700);
        departement1.setPrefecture("Bertoua");
        departement1.setRegion(region);
        departementRepository.save(departement1);

        Departement departement2 = new Departement();
        departement2.setNom("Kadey");
        departement2.setSuperficie(15884);
        departement2.setPopulation(194800);
        departement2.setPrefecture("Batouri");
        departement2.setRegion(region);
        departementRepository.save(departement2);

        List<Departement> departements = departementRepository.findAll();

        assertThat(departements).hasSize(2);
        assertThat(departements).extracting(Departement::getNom).contains("Lom-et-Djerem", "Kadey");
    }

    @Test
    void doitTrouverUnDepartementParSonNom() {
        Departement departement = new Departement();
        departement.setNom("Haut-Nyong");
        departement.setSuperficie(36384);
        departement.setPopulation(216768);
        departement.setPrefecture("Abong-Mbang");
        departement.setRegion(region);
        departementRepository.save(departement);

        Departement foundDepartement = departementRepository.findByNom("Haut-Nyong").orElse(null);

        assertThat(foundDepartement).isNotNull();
        assertThat(foundDepartement.getNom()).isEqualTo("Haut-Nyong");
    }

    @Test
    void neDoitPasTrouverUnDepartementInexistantParSonNom() {
        assertThat(departementRepository.findByNom("Inexistant")).isEmpty();
    }

    @Test
    void doitVerifierSiUnDepartementExisteParSonNom() {
        Departement departement = new Departement();
        departement.setNom("Boumba-et-Ngoko");
        departement.setSuperficie(30389);
        departement.setPopulation(116796);
        departement.setPrefecture("Yokadouma");
        departement.setRegion(region);
        departementRepository.save(departement);

        boolean exists = departementRepository.existsByNom("Boumba-et-Ngoko");

        assertThat(exists).isTrue();
    }

    @Test
    void neDoitPasVerifierSiUnDepartementInexistantExisteParSonNom() {
        boolean exists = departementRepository.existsByNom("Inexistant");

        assertThat(exists).isFalse();
    }
}
