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
- **Frontiere** : Définit une limite géographique (pays, région, etc.).
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

Le préfixe de base pour tous les endpoints est `/api`.

### Affectation (`/api/affectation`)
| Méthode | Endpoint | Description |
|---|---|---|
| `POST` | `/` | Crée une nouvelle affectation. |
| `GET` | `/` | Liste toutes les affectations. |
| `PUT` | `/{id}` | Met à jour une affectation existante. |
| `DELETE` | `/{id}` | Supprime une affectation par son ID. |

### Arrondissement (`/api/arrondissement`)
| Méthode | Endpoint | Description |
|---|---|---|
| `POST` | `/` | Crée un nouvel arrondissement. |
| `GET` | `/` | Liste tous les arrondissements. |
| `GET` | `/id/{id}` | Recherche un arrondissement par son ID. |
| `GET` | `/nom/{nom}` | Recherche un arrondissement par son nom. |
| `PUT` | `/{id}` | Met à jour un arrondissement existant. |
| `DELETE` | `/id/{id}` | Supprime un arrondissement par son ID. |
| `DELETE` | `/nom/{nom}` | Supprime un arrondissement par son nom. |

### Autorité (`/api/autorite`)
| Méthode | Endpoint | Description |
|---|---|---|
| `POST` | `/` | Crée une nouvelle autorité. |
| `GET` | `/` | Liste toutes les autorités. |
| `GET` | `/{id}` | Recherche une autorité par son ID. |
| `PUT` | `/{id}` | Met à jour une autorité existante. |
| `DELETE` | `/{id}` | Supprime une autorité par son ID. |

### Circonscription (`/api/circonscription`)
| Méthode | Endpoint | Description |
|---|---|---|
| `POST` | `/` | Crée une nouvelle circonscription (non utilisable directement car la classe est abstraite). |
| `GET` | `/` | Liste toutes les circonscriptions (Régions, Départements, Arrondissements). |
| `GET` | `/{id}` | Recherche une circonscription par son ID. |
| `PUT` | `/{id}` | Met à jour une circonscription (non utilisable directement). |
| `DELETE` | `/{id}` | Supprime une circonscription par son ID. |

### Délimitation (`/api/delimitation`)
| Méthode | Endpoint | Description |
|---|---|---|
| `POST` | `/` | Crée une nouvelle délimitation entre une circonscription et une frontière. |
| `GET` | `/` | Liste toutes les délimitations. |
| `DELETE` | `/{id}` | Supprime une délimitation par son ID. |

### Département (`/api/departement`)
| Méthode | Endpoint | Description |
|---|---|---|
| `POST` | `/` | Crée un nouveau département. |
| `GET` | `/` | Liste tous les départements. |
| `GET` | `/id/{id}` | Recherche un département par son ID. |
| `GET` | `/nom/{nom}` | Recherche un département par son nom. |
| `PUT` | `/{id}` | Met à jour un département existant. |
| `DELETE` | `/id/{id}` | Supprime un département par son ID. |
| `DELETE` | `/nom/{nom}` | Supprime un département par son nom. |

### Frontière (`/api/frontiere`)
| Méthode | Endpoint | Description |
|---|---|---|
| `POST` | `/` | Crée une nouvelle frontière. |
| `GET` | `/` | Liste toutes les frontières. |
| `GET` | `?type={type}` | Recherche les frontières par type (ex: `PAYS`, `REGION`). |

### Quartier (`/api/quartier`)
| Méthode | Endpoint | Description |
|---|---|---|
| `POST` | `/` | Crée un nouveau quartier. |
| `GET` | `/` | Liste tous les quartiers. |
| `GET` | `/id/{id}` | Recherche un quartier par son ID. |
| `GET` | `/nom/{nom}` | Recherche un quartier par son nom. |
| `PUT` | `/{id}` | Met à jour un quartier existant. |
| `DELETE` | `/id/{id}` | Supprime un quartier par son ID. |
| `DELETE` | `/nom/{nom}` | Supprime un quartier par son nom. |

### Région (`/api/region`)
| Méthode | Endpoint | Description |
|---|---|---|
| `POST` | `/` | Crée une nouvelle région. |
| `GET` | `/` | Liste toutes les régions. |
| `GET` | `/id/{id}` | Recherche une région par son ID. |
| `GET` | `/nom/{nom}` | Recherche une région par son nom. |
| `PUT` | `/{id}` | Met à jour une région existante. |
| `DELETE` | `/id/{id}` | Supprime une région par son ID. |
| `DELETE` | `/nom/{nom}` | Supprime une région par son nom. |