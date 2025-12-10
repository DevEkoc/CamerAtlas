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
    superficie INT NOT NULL,
    population INT NOT NULL,
    coordonneesGPS VARCHAR(50) DEFAULT NULL,
    image VARCHAR(255) DEFAULT NULL
);

CREATE TABLE appointment (
    idAffectation INT AUTO_INCREMENT PRIMARY KEY,
    idAutorite INT NOT NULL,
    codeCirconscription INT NOT NULL,
    function ENUM('GOUVERNEUR', 'PREFET', 'SOUS-PREFET') NOT NULL,
    dateDebut DATE NOT NULL,
    dateFin DATE DEFAULT NULL,
    UNIQUE (idAutorite, codeCirconscription),
    CHECK ( dateFin IS NULL OR dateFin > dateDebut ),
    FOREIGN KEY (idAutorite) REFERENCES autorite(idAutorite) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (codeCirconscription) REFERENCES circonscription(codeCirconscription) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE delimitation (
    idDelimitation INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    codeCirconscription INT,
    typeFrontiere ENUM('NORD', 'NORD_OUEST', 'NORD_EST', 'SUD', 'SUD_OUEST', 'SUD_EST', 'EST', 'OUEST') NOT NULL,
    frontiere VARCHAR(150) NOT NULL,
    FOREIGN KEY (codeCirconscription) REFERENCES circonscription(codeCirconscription)
);

CREATE TABLE region (
    codeCirconscription INT PRIMARY KEY,
    chefLieu VARCHAR(50),
    codeMineralogique CHAR(2),
    FOREIGN KEY (codeCirconscription) REFERENCES circonscription(codeCirconscription)
);

CREATE TABLE division (
    codeCirconscription INT PRIMARY KEY,
    prefecture VARCHAR(50),
    idRegion INT NOT NULL,
    FOREIGN KEY (codeCirconscription) REFERENCES circonscription(codeCirconscription),
    FOREIGN KEY (idRegion) REFERENCES region(codeCirconscription)
);

CREATE TABLE subDivision (
    codeCirconscription INT PRIMARY KEY,
    sousPrefecture VARCHAR(50),
    idDepartement INT NOT NULL,
    FOREIGN KEY (codeCirconscription) REFERENCES circonscription(codeCirconscription),
    FOREIGN KEY (idDepartement) REFERENCES division(codeCirconscription)
);

CREATE TABLE neighborhood (
    idQuartier INT AUTO_INCREMENT PRIMARY KEY,
    nomQuartier VARCHAR(50) NOT NULL,
    nomPopulaire VARCHAR(50) DEFAULT NULL,
    sousPrefecture INT NOT NULL,
    FOREIGN KEY (sousPrefecture) REFERENCES subDivision(codeCirconscription)
);

# ALTER TABLE division
#     ADD COLUMN regionId INT NOT NULL
# ;
#
# ALTER TABLE subDivision
#     ADD COLUMN divisionId INT NOT NULL
# ;
#
# ALTER TABLE division
#     ADD CONSTRAINT FOREIGN KEY (regionId) REFERENCES region(circonscriptionId)
# ;
#
# ALTER TABLE subDivision
#     ADD CONSTRAINT FOREIGN KEY (divisionId) REFERENCES division(circonscriptionId)
# ;

# ALTER TABLE appointment
#     ADD CONSTRAINT dateFin_supp_dateDebut
#         CHECK (endDate IS NULL OR endDate > startDate)
# ;

# Authority ==> Authority
# Appointment ==> Appointment
# Possede ==> Delimitation