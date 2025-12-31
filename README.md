# 🌍 CamerAtlas API

![Spring](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Maven](https://img.shields.io/badge/Apache_Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![JPA](https://img.shields.io/badge/JPA-a571a5?style=for-the-badge&logo=hibernate&logoColor=white)

Ce dépôt contient le code source de **CamerAtlas**, une application backend développée en **Java 21** avec **Spring Boot**, destinée à la **centralisation, la consultation et l’exploitation des données territoriales du Cameroun**.  
Le projet expose une **API REST sécurisée**, orientée métier, permettant un accès structuré et fiable aux informations territoriales.

---

## 🎯 Objectifs du projet
- Centraliser les données territoriales du Cameroun dans une base unique
- Offrir une API REST fiable pour la consultation et l’exploitation des données
- Garantir la sécurité, la cohérence et la traçabilité des accès
- Mettre en place une architecture backend maintenable et évolutive
- Fournir un mécanisme de contribution participative permettant aux utilisateurs autorisés de proposer des améliorations sur les données territoriales

---

## 🛠️ Stack technique

- **Langage** : Java 17 et versions ultérieures
- **Framework** : Spring Boot 3
- **Persistance** : Spring Data JPA / Hibernate
- **Base de données** : PostgreSQL
- **Sécurité** : Spring Security, JWT (Access Token + Refresh Token)
- **Build** : Maven
- **Documentation API** : Swagger / OpenAPI
- **Outils** : Git, GitHub, Postman

---

## ⚙️ Fonctionnalités principales

### 🔐 Sécurité & Authentification
- Authentification basée sur **JWT**
- Gestion des **access tokens** et **refresh tokens**
- Sécurisation des endpoints avec **Spring Security**

### 👤 Gestion des utilisateurs
- Création, consultation, mise à jour et suppression (CRUD)
- Attribution des rôles
- Contrôle d’accès aux ressources

### 👥 Gestion des rôles et des permissions
Le système repose sur une gestion fine des accès basée sur des rôles :
- **Public**
    - Consultation libre des données territoriales
    - Accès en lecture seule aux endpoints publics
- **Contributor**
    - Consultation des données
    - Soumission de suggestions d’amélioration ou de correction
    - Participation active à l’enrichissement des données
- **Admin**
    - Accès complet à l’application
    - CRUD sur les unités administratives
    - Gestion, validation et modération des suggestions
    - Administration des utilisateurs et des rôles

### 🗺️ Données territoriales
- Centralisation des données territoriales
- Consultation des données via API REST
- Filtres dynamiques et pagination
- Validation des données en entrée

### 🔄 Système de contribution et de suggestions
- Soumission de suggestions de correction ou d’enrichissement des données 
- Traçabilité des propositions (statut, auteur, date)
- Validation ou rejet des suggestions par les administrateurs 
- Amélioration continue de la qualité et de la fiabilité des données

### 🧩 API & Qualité
- API RESTful respectant les bonnes pratiques
- Gestion centralisée des exceptions
- Réponses API normalisées
- Documentation automatique via Swagger

---

## 🏗️ Architecture

Le projet adopte une **architecture en couches**, claire et découplée :

- **Controller** : exposition des endpoints REST
- **Service** : implémentation de la logique métier
- **Repository** : accès aux données (JPA)
- **Entity** : modélisation des entités métiers
- **DTO** : échanges de données avec l’API
- **Mapper** : conversion Entity ↔ DTO

### 🔁 Schéma de fonctionnement
Client ➡️ Controller ➡️ Service ➡️ Repository ➡️ Base de données

---
## 📘 Documentation API
Une documentation interactive est disponible via Swagger : `http://localhost:8080/swagger-ui.html`. Elle permet de :
- Visualiser l’ensemble des endpoints
- Tester les requêtes directement
- Comprendre les modèles de données
---
## ▶️ Lancer le projet en local

1. **Cloner le dépôt :**
```bash 
  git clone https://github.com/votre-utilisateur/camerAtlas.git
  cd camerAtlas
```
2. **Configurer la base de données dans application.properties :**
```properties
spring.datasource.url=
spring.datasource.username=
spring.datasource.password=
```
3.  **Lancez l'application :**
    - Utilisez Maven pour compiler le projet et lancer le serveur de développement.
    ```bash
    mvn clean install
    mvn spring-boot:run
    ``` 
L'API devrait maintenant être accessible à l'adresse `http://localhost:8080/api`.

---
## 🧪 Tests
- Tests unitaires des services avec JUnit 5 et Mockito 
- Vérification de la logique métier 
- Isolation des dépendances via mocks
---

## 🔐 Sécurité
- Authentification JWT 
- Autorisation par rôles 
- Protection des endpoints sensibles 
- Gestion centralisée des erreurs de sécurité
---

## 🚀 Améliorations prévues
- Ajout de tests d’intégration
- Conteneurisation avec Docker
- Mise en place d’un pipeline CI/CD
- Amélioration de la couverture de tests
- Monitoring et logs avancés
---

## 🧑‍💻 Auteur
**Christophe Cédric EKOBENA OMGBA**

---
## 📄 Licence

Copyright © 2025 **Christophe Cédric EKOBENA OMGBA**. Tous droits réservés.
