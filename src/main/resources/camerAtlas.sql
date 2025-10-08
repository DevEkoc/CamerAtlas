-- ============================================================================
-- Script de création de la base de données camerAtlas (MySQL)
-- Application qui présente le découpage administratif du Cameroun
-- ============================================================================

-- Création de la base de données
DROP DATABASE IF EXISTS camerAtlas;
CREATE DATABASE camerAtlas ;

USE camerAtlas;

-- ============================================================================
-- CREATION DES TABLES
-- ============================================================================
CREATE TABLE autorite (
    idAutorite INT AUTO_INCREMENT PRIMARY KEY,
    nomAutorite VARCHAR(50) NOT NULL,
    prenomAutorite VARCHAR(50) DEFAULT NULL,
    dateNaissance DATE DEFAULT NULL
);

CREATE TABLE circonscription (
    codeCirconscription INT AUTO_INCREMENT PRIMARY KEY,
    nomCirconscription VARCHAR(50) UNIQUE NOT NULL,
#     codePostal VARCHAR(10) UNIQUE DEFAULT NULL,
    superficie INT NOT NULL,
    population INT NOT NULL,
    coordonneesGPS VARCHAR(50) DEFAULT NULL
);

CREATE TABLE affectation (
    idAffectation INT AUTO_INCREMENT PRIMARY KEY,
    idAutorite INT NOT NULL,
    codeCirconscription INT NOT NULL,
    fonction ENUM('GOUVERNEUR', 'PREFET', 'SOUS-PREFET') NOT NULL,
    dateDebut DATE NOT NULL,
    dateFin DATE DEFAULT NULL,
    UNIQUE (idAutorite, codeCirconscription),
    CHECK ( dateFin IS NULL OR dateFin > dateDebut ),
    FOREIGN KEY (idAutorite) REFERENCES autorite(idAutorite) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (codeCirconscription) REFERENCES circonscription(codeCirconscription) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE frontiere (
    idFrontiere INT AUTO_INCREMENT PRIMARY KEY,
    typeFrontiere ENUM('NORD', 'SUD', 'EST', 'OUEST') NOT NULL,
    limite VARCHAR(50) NOT NULL
);

CREATE TABLE delimitation (
    codeCirconscription INT,
    idFrontiere INT,
    PRIMARY KEY (codeCirconscription, idFrontiere),
    FOREIGN KEY (codeCirconscription) REFERENCES circonscription(codeCirconscription),
    FOREIGN KEY (idFrontiere) REFERENCES frontiere(idFrontiere)
);

CREATE TABLE region (
    codeCirconscription INT PRIMARY KEY,
    chefLieu VARCHAR(50),
    codeMineralogique CHAR(2),
    FOREIGN KEY (codeCirconscription) REFERENCES circonscription(codeCirconscription)
);

CREATE TABLE departement (
    codeCirconscription INT PRIMARY KEY,
    prefecture VARCHAR(50),
    idRegion INT NOT NULL,
    FOREIGN KEY (codeCirconscription) REFERENCES circonscription(codeCirconscription),
    FOREIGN KEY (idRegion) REFERENCES region(codeCirconscription)
);

CREATE TABLE arrondissement (
    codeCirconscription INT PRIMARY KEY,
    sousPrefecture VARCHAR(50),
    idDepartement INT NOT NULL,
    FOREIGN KEY (codeCirconscription) REFERENCES circonscription(codeCirconscription),
    FOREIGN KEY (idDepartement) REFERENCES departement(codeCirconscription)
);

CREATE TABLE quartier (
    idQuartier INT AUTO_INCREMENT PRIMARY KEY,
    nomQuartier VARCHAR(50) NOT NULL,
    nomPopulaire VARCHAR(50) DEFAULT NULL,
    sousPrefecture INT NOT NULL,
    FOREIGN KEY (sousPrefecture) REFERENCES arrondissement(codeCirconscription)
);

# ALTER TABLE departement
#     ADD COLUMN idRegion INT NOT NULL
# ;
#
# ALTER TABLE arrondissement
#     ADD COLUMN idDepartement INT NOT NULL
# ;
#
# ALTER TABLE departement
#     ADD CONSTRAINT FOREIGN KEY (idRegion) REFERENCES region(codeCirconscription)
# ;
#
# ALTER TABLE arrondissement
#     ADD CONSTRAINT FOREIGN KEY (idDepartement) REFERENCES departement(codeCirconscription)
# ;

# ALTER TABLE affectation
#     ADD CONSTRAINT dateFin_supp_dateDebut
#         CHECK (dateFin IS NULL OR dateFin > dateDebut)
# ;

# Autorite ==> Autorite
# Affectation ==> Affectation
# Possede ==> Delimitation