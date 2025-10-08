# CamerAtlas

CamerAtlas est une application Spring Boot qui fournit une API REST pour gérer les divisions administratives du Cameroun, y compris les régions, les départements, les arrondissements, ainsi que les autorités qui les gouvernent.

## Table des matières
- [Concepts Clés](#concepts-clés)
- [Technologies Utilisées](#technologies-utilisées)
- [Prérequis](#prérequis)
- [Configuration](#configuration)
- [Lancement de l'application](#lancement-de-lapplication)
- [Structure du Projet](#structure-du-projet)
- [Endpoints de l'API](#endpoints-de-lapi)

## Concepts Clés

Le modèle de données s'articule autour de plusieurs entités principales :

- **Circonscription** : Représente une unité administrative générique (Région, Département, Arrondissement). C'est une classe mère dont héritent les autres types de circonscriptions.
- **Autorite** : Désigne un fonctionnaire de l'administration (par exemple, un Gouverneur, un Préfet).
- **Affectation** : Matérialise l'assignation d'une `Autorite` à une `Circonscription` pour une période donnée, avec une fonction spécifique (`GOUVERNEUR`, `PREFET`, `SOUS-PREFET`).
- **Frontiere** : Définit une limite géographique (Nord, Sud, Est, Ouest).
- **Delimitation** : Table de jointure qui associe une `Circonscription` à ses `Frontiere`.

## Technologies Utilisées

- **Backend**:
  - Java 21
  - Spring Boot 3.5.6
  - Spring Web (pour l'API REST)
  - Spring Data JPA / Hibernate (pour la persistance des données)
- **Base de données**:
  - MySQL
- **Gestion de projet**:
  - Apache Maven

## Prérequis

- JDK 21 ou supérieur
- Apache Maven
- Une instance de base de données MySQL en cours d'exécution

## Configuration

1.  **Base de données** :
    Exécutez le script SQL situé dans `src/main/resources/camerAtlas.sql` pour créer la base de données `cameratlas` et ses tables.

2.  **Variables d'environnement** :
    Créez un fichier `.env` à la racine du projet (`camerAtlas/.env`) et configurez vos identifiants de base de données. Vous pouvez vous baser sur l'exemple suivant :

    ```env
    DATABASE_URL=jdbc:mysql://localhost:3306/cameratlas
    DATABASE_USER=votre_utilisateur_db
    DATABASE_PASSWORD=votre_mot_de_passe_db
    DATABASE_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver
    ```

## Lancement de l'application

Une fois la configuration terminée, vous pouvez démarrer l'application en utilisant le wrapper Maven fourni :

```bash
./mvnw spring-boot:run
```

L'application sera accessible à l'adresse `http://localhost:8080/api`.

## Structure du Projet

Le code source est organisé dans les packages suivants :

- `com.devekoc.camerAtlas`:
  - `config`: Configuration de l'application (ex: chargement du fichier `.env`).
  - `controllers`: Contient les contrôleurs REST qui exposent les endpoints de l'API.
  - `dto`: Data Transfer Objects, utilisés pour structurer les données des requêtes et des réponses.
  - `entities`: Classes JPA qui modélisent les tables de la base de données.
  - `enumerations`: Énumérations utilisées dans le modèle (ex: `Fonction`, `TypeFrontiere`).
  - `exceptions`: Exceptions personnalisées pour la gestion des erreurs métier.
  - `repositories`: Interfaces Spring Data JPA pour l'accès aux données.
  - `services`: Contient la logique métier de l'application.

## Endpoints de l'API

Le contexte de l'application est `/api`.

### Autorités
- `GET /api/autorite` : Liste toutes les autorités.
- `GET /api/autorite/{id}` : Récupère une autorité par son ID.
- `POST /api/autorite` : Crée une nouvelle autorité.
- `PUT /api/autorite/{id}` : Met à jour une autorité existante.
- `DELETE /api/autorite/{id}` : Supprime une autorité.

### Circonscriptions
- `GET /api/circonscription` : Liste toutes les circonscriptions.
- `GET /api/circonscription/{id}` : Récupère une circonscription par son ID.
- `POST /api/circonscription` : Crée une nouvelle circonscription.
- `PUT /api/circonscription/{id}` : Met à jour une circonscription.
- `DELETE /api/circonscription/{id}` : Supprime une circonscription.

### Affectations
- `GET /api/affectation` : Liste toutes les affectations.
- `POST /api/affectation` : Crée une nouvelle affectation.
- `PUT /api/affectation/{id}` : Met à jour une affectation.
- `DELETE /api/affectation/{id}` : Supprime une affectation.

### Frontières
- `GET /api/frontiere` : Liste toutes les frontières.
- `GET /api/frontiere?type={type}` : Recherche les frontières par type (`NORD`, `SUD`, `EST`, `OUEST`).
- `POST /api/frontiere` : Crée une nouvelle frontière.

### Délimitations
- `GET /api/delimitation` : Liste toutes les délimitations.
- `POST /api/delimitation` : Crée une nouvelle délimitation.
- `DELETE /api/delimitation/{codeCirconscription}/{idFrontiere}` : Supprime une délimitation.

### Régions
- `POST /api/region` : Crée une nouvelle région.
