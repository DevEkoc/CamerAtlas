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
CREATE TABLE administrateur (
    idAdministrateur INT AUTO_INCREMENT PRIMARY KEY,
    nomAdministrateur VARCHAR(50) NOT NULL,
    prenomAdministrateur VARCHAR(50) DEFAULT NULL,
    dateNaissance DATE DEFAULT NULL
);

CREATE TABLE circonscription (
    codeCirconscription INT AUTO_INCREMENT PRIMARY KEY,
    nomCirconscription VARCHAR(50) NOT NULL,
    superficie INT NOT NULL,
    population INT NOT NULL,
    coordonneesGPS VARCHAR(50) DEFAULT NULL
);

CREATE TABLE administration (
    idAdministration INT AUTO_INCREMENT PRIMARY KEY,
    idAdministrateur INT NOT NULL UNIQUE,
    codeCirconscription INT NOT NULL UNIQUE,
    fonction ENUM('GOUVERNEUR', 'PREFET', 'SOUS-PREFET') NOT NULL,
    dateDebut DATE NOT NULL,
    dateFin DATE DEFAULT NULL,
    FOREIGN KEY (idAdministrateur) REFERENCES administrateur(idAdministrateur),
    FOREIGN KEY (codeCirconscription) REFERENCES circonscription(codeCirconscription)
);

CREATE TABLE frontiere (
    idFrontiere INT AUTO_INCREMENT PRIMARY KEY,
    typeFrontiere ENUM('NORD', 'SUD', 'EST', 'OUEST') NOT NULL,
    limite VARCHAR(50) NOT NULL
);

CREATE TABLE possede (
    codeCirconscription INT,
    idFrontiere INT,
    PRIMARY KEY (codeCirconscription, idFrontiere)
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
    FOREIGN KEY (codeCirconscription) REFERENCES circonscription(codeCirconscription)
);

CREATE TABLE arrondissement (
    codeCirconscription INT PRIMARY KEY,
    sousPrefecture VARCHAR(50),
    FOREIGN KEY (codeCirconscription) REFERENCES circonscription(codeCirconscription)
);

CREATE TABLE quartier (
    idQuartier INT AUTO_INCREMENT PRIMARY KEY,
    nomQuartier VARCHAR(50) NOT NULL,
    nomPopulaire VARCHAR(50) DEFAULT NULL,
    sousPrefecture INT NOT NULL,
    FOREIGN KEY (sousPrefecture) REFERENCES arrondissement(codeCirconscription)
);