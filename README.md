# CamerAtlas API

![Spring](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Maven](https://img.shields.io/badge/Apache_Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![JPA](https://img.shields.io/badge/JPA-a571a5?style=for-the-badge&logo=hibernate&logoColor=white)

Ce dépôt contient le code source de **CamerAtlas**, une API RESTful robuste développée avec **Spring Boot**. Le projet vise à modéliser, stocker et servir des données complètes sur les divisions administratives du Cameroun, de la région jusqu'au neighborhood.

## 🎯 Fonctionnalités Clés

- **API RESTful Complète :** Endpoints CRUD (Create, Read, Update, Delete) pour toutes les entités administratives (Régions, Départements, Arrondissements, Quartiers, etc.).
- **Modélisation Hiérarchique :** Utilisation de l'héritage JPA (`@Inheritance`) pour une représentation claire et maintenable de la hiérarchie des circonscriptions.
- **Gestion des Médias :** Système d'upload permettant d'associer des images aux entités (régions, départements, etc.), avec un service dédié pour une logique centralisée.
- **Validation des Données :** Utilisation de `jakarta.validation` et de contraintes personnalisées pour garantir l'intégrité des données entrantes.
- **Gestion d'Erreurs Centralisée :** Un `ControllerAdvice` global intercepte les exceptions pour fournir des réponses d'erreur JSON claires et standardisées.
- **Architecture Propre :** Conçu selon une architecture 3-tiers (Controller, Service, Repository) et utilisant des DTOs (Data Transfer Objects) pour découpler l'API du modèle de données.

## 🛠️ Stack Technique

- **Framework :** [Spring Boot](https://spring.io/projects/spring-boot) (v3.x)
- **Langage :** [Java](https://www.java.com/) (v17+)
- **Accès aux données :** [Spring Data JPA](https://spring.io/projects/spring-data-jpa), [Hibernate](https://hibernate.org/)
- **Base de données :** [MySQL](https://www.mysql.com/)
- **Gestion de dépendances :** [Apache Maven](https://maven.apache.org/)
- **Validation :** Jakarta Bean Validation
- **Logging :** SLF4J & Logback

## 🚀 Installation et Lancement

Suivez ces instructions pour lancer le projet en local sur votre machine.

### Prérequis

- [JDK (Java Development Kit)](https://www.oracle.com/java/technologies/downloads/) (version 17 ou supérieure)
- [Apache Maven](https://maven.apache.org/download.cgi) (version 3.8 ou supérieure)
- Un serveur de base de données [MySQL](https://dev.mysql.com/downloads/mysql/) fonctionnel.

### Configuration

1.  **Clonez le dépôt :**
    ```bash
    git clone https://github.com/votre-utilisateur/camerAtlas.git
    cd camerAtlas
    ```

2.  **Configurez la base de données :**
    - Assurez-vous que votre serveur MySQL est en cours d'exécution.
    - Créez une base de données pour le projet. Exemple :
      ```sql
      CREATE DATABASE cameratlas_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
      ```

3.  **Configurez les variables d'environnement :**
    - À la racine du projet, créez un fichier nommé `.env`.
    - Remplissez ce fichier avec les informations de connexion à votre base de données. Voici un exemple :
      ```properties
      # URL de connexion JDBC pour MySQL
      DATABASE_URL=jdbc:mysql://localhost:3306/cameratlas_db

      # Utilisateur de la base de données
      DATABASE_USER=root

      # Mot de passe de l'utilisateur
      DATABASE_PASSWORD=votre_mot_de_passe

      # Classe du driver JDBC
      DATABASE_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver
      ```

4.  **Lancez l'application :**
    - Utilisez Maven pour compiler le projet et lancer le serveur de développement.
    ```bash
    mvn spring-boot:run
    ```

L'API devrait maintenant être accessible à l'adresse `http://localhost:8080/api`.

## 🌐 Endpoints Principaux de l'API

L'API expose plusieurs ressources pour interagir avec les données géographiques et administratives :

- `GET /api/regions` : Liste toutes les régions.
- `GET /api/regions/{id}` : Récupère une région spécifique.
- `GET /api/departements` : Liste tous les départements.
- `GET /api/arrondissements` : Liste tous les arrondissements.
- `GET /api/quartiers` : Liste tous les quartiers.
- `GET /api/autorites` : Liste les autorités.
- `GET /api/media/regions/{filename}` : Accède à l'image d'une région.

Des endpoints `POST`, `PUT`, `DELETE` sont également disponibles pour la gestion des données (potentiellement sécurisés).

## Auteur

- **Christophe Cédric EKOBENA OMGBA**

## Licence

Copyright © 2024 Christophe Cédric EKOBENA OMGBA. Tous droits réservés.

Ce projet est présenté à des fins de démonstration et de consultation. La permission de voir le code source est accordée, mais toute utilisation, copie, modification, distribution ou vente du logiciel et de sa documentation est strictement interdite sans l'autorisation écrite préalable de l'auteur.
