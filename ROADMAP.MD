# 🚀 Feuille de Route Stratégique — CamerAtlas

## 🎯 Vision du Projet

**CamerAtlas** est une application web backend développée en **Spring Boot**, visant à modéliser et exposer via une **API REST** les divisions administratives du Cameroun. L'objectif est de fournir une source de données fiable, complète et facilement requêtable.

---

## ✅ Bilan de l'État Actuel (Ce qui est déjà fait)

Le projet dispose déjà d'une base technique très solide et fonctionnelle. La plupart des fonctionnalités fondamentales sont implémentées.

| Module | Statut | Détails Techniques |
|:---|:---:|:---|
| **Hiérarchie Administrative** | ✅ **Terminé** | CRUD complet pour `Region`, `Departement`, `Arrondissement`, `Quartier`. |
| **Entités Secondaires** | ✅ **Terminé** | CRUD complet pour `Autorite`, `Affectation`, `Delimitation`. |
| **Structure de l'API** | ✅ **Terminé** | Endpoints RESTful pour chaque entité avec validation des entrées (`@Valid`). |
| **DTOs & Mapping** | ✅ **Terminé** | Utilisation systématique de DTOs (records) pour les créations et listages. Le mapping est fait dans les services ou via des classes dédiées (`RegionMapper`). |
| **Gestion des Médias** | ✅ **Partiel** | **Upload d'images fonctionnel** pour les `Region`. Les images sont stockées dans `uploads/regions`. |
| **Service des Médias** | ✅ **Terminé**| Un `MediaController` existe et **sert déjà les images** via l'endpoint `GET /api/media/regions/{filename}`. |
| **Gestion des Erreurs** | ✅ **Terminé** | Centralisée et robuste via `ApplicationControllerAdvice`. |
| **Relations Hiérarchiques** | ✅ **Terminé** | Les DTOs de listing (ex: `RegionListerDTO`) incluent déjà les enfants directs (ex: liste des départements). |
| **Validation Métier** | ✅ **Terminé** | Des règles complexes sont en place (ex: une autorité ne peut avoir qu'un poste actif, géré dans `AffectationService`). |

---

## 🗺️ Feuille de Route d'Évolution

Basé sur les fondations existantes, voici une proposition de roadmap stratégique.

### 🏁 Phase 1 — Robustesse et Fiabilité
**Objectif :** Sécuriser l'application, garantir sa stabilité et améliorer l'expérience développeur.

- [ ] **Intégrer Spring Security :**
    -   [ ] Mettre en place une authentification par **JWT (JSON Web Tokens)**.
    -   [ ] Créer les endpoints `/auth/login` et `/auth/register`.
    -   [ ] Sécuriser les routes `POST`, `PUT`, `DELETE` pour qu'elles nécessitent un rôle `ADMIN`.
- [ ] **Compléter la couverture de tests :**
    -   [ ] Écrire des tests unitaires pour les `Services` (logique métier, cas limites).
    -   [ ] Écrire des tests d'intégration pour les `Controllers` avec `MockMvc` pour valider les endpoints, les DTOs et les réponses HTTP.
- [ ] **Documenter l'API :**
    -   [ ] Intégrer **SpringDoc (OpenAPI 3)** pour générer une documentation Swagger UI interactive.
- [ ] **Mettre à jour les images :**
    -   [ ] Dans `RegionService`, ajouter la logique pour **supprimer l'ancienne image** du disque lors de la mise à jour d'une région avec une nouvelle image.

---

### 🧱 Phase 2 — Complétude Fonctionnelle
**Objectif :** Étendre les fonctionnalités existantes pour une API plus riche et performante.

- [ ] **Pagination et Filtrage :**
    -   [ ] Implémenter la pagination sur tous les endpoints `GET` qui retournent des listes (`Region`, `Departement`, etc.) en utilisant `Pageable`.
    -   [ ] Ajouter des paramètres de filtrage (ex: `GET /api/division?regionId=1`).
- [ ] **Généraliser la gestion des médias :**
    -   [ ] Permettre l'upload d'images pour les `Departement` et `Arrondissement`.
    -   [ ] Créer les dossiers `uploads/departements` et `uploads/arrondissements`.
    -   [ ] Étendre le `MediaController` pour servir ces nouvelles images.
- [ ] **Activer les opérations en cascade :**
    -   [ ] Décommenter et finaliser la méthode `creerEnCascade` dans `RegionController`.
    -   [ ] L'implémenter pour les autres entités où c'est pertinent.
- [ ] **Refactoring - Centraliser les Mappers :**
    -   [ ] Créer des classes `DepartementMapper`, `ArrondissementMapper`, etc., sur le modèle de `RegionMapper` pour externaliser la logique de conversion des services.

---

### 🚀 Phase 3 – Industrialisation et Déploiement
**Objectif :** Préparer le projet pour la production.

- [ ] **Conteneurisation :**
    -   [ ] Créer un `Dockerfile` pour l'application Spring Boot.
    -   [ ] Créer un fichier `docker-compose.yml` pour lancer l'application et sa base de données MySQL facilement.
- [ ] **Mettre en place une CI/CD (Intégration Continue / Déploiement Continu) :**
    -   [ ] Créer un workflow **GitHub Actions** qui, à chaque `push` sur la branche `main` :
        1.  Compile le code.
        2.  Lance les tests.
        3.  Construit l'image Docker.
        4.  (Optionnel) Pousse l'image sur un registre (Docker Hub, GHCR).
- [ ] **Mettre à jour le `README.md` principal :**
    -   [ ] Ajouter les instructions de lancement via Docker.
    -   [ ] Inclure le lien vers la documentation Swagger.

---

### 🏛️ Phase 4 – API Collaborative
**Objectif :** Permettre à des utilisateurs authentifiés de contribuer à l'enrichissement des données via un système de suggestions.

- [ ] **Mettre en place le rôle `CONTRIBUTEUR` :**
    -   [ ] Étendre Spring Security pour gérer un rôle `CONTRIBUTEUR` en plus du rôle `ADMIN`.
    -   [ ] Mettre à jour l'endpoint `/auth/register` pour qu'il assigne le rôle `CONTRIBUTEUR` par défaut.
- [ ] **Développer le système de suggestions :**
    -   [ ] Créer les entités JPA (ex: `Suggestion`) pour stocker les propositions de changement (type, entité concernée, contenu suggéré).
    -   [ ] Développer les services pour créer et gérer le cycle de vie des suggestions (en attente, approuvée, rejetée).
- [ ] **Créer les endpoints pour les contributeurs :**
    -   [ ] Implémenter les routes `POST`, `PUT`, `DELETE` sur `/api/suggestions/{entity}` pour permettre aux contributeurs de soumettre leurs propositions.
- [ ] **Créer les endpoints pour les administrateurs :**
    -   [ ] Implémenter les routes pour lister (`GET /api/admin/suggestions`), approuver (`POST /api/admin/suggestions/{id}/approve`) et rejeter (`POST /api/admin/suggestions/{id}/reject`) les suggestions.

---

### 💡 Phase 5 – Évolutions Futures (Optionnel)

- [ ] **Recherche Avancée :** Implémenter une recherche textuelle sur plusieurs champs avec Spring Data JPA Specifications.
- [ ] **Audit :** Ajouter des champs `dateCreation`, `dateModification` aux entités pour tracer les changements.
- [ ] **Cache :** Mettre en place un cache (ex: avec `Caffeine` ou `Redis`) sur les données peu volatiles pour améliorer les performances.
- [ ] **Frontend :** Développer une interface utilisateur (React, Vue.js, Angular) pour consommer l'API.