# 🚀 Feuille de Route Stratégique — CamerAtlas

## 🎯 Vision du Projet

**CamerAtlas** est une application web backend développée en **Spring Boot**, visant à modéliser et exposer via une **API REST** les divisions administratives du Cameroun. L'objectif est de fournir une source de données fiable, complète et facilement requêtable.

---

## ✅ Bilan de l'État Actuel (Mis à jour le 02/12/2025)

| Module | Statut | Détails Techniques |
|:---|:---:|:---|
| **Hiérarchie Administrative** | ✅ **Terminé** | CRUD complet pour `Region`, `Division`, `SubDivision`, `Neighborhood`. |
| **Entités Secondaires** | ✅ **Terminé** | CRUD complet pour `Authority`, `Appointment`, `Delimitation`. |
| **Sécurité de l'API** | ✅ **Terminé** | Authentification **JWT**. Rôles `ADMIN` et `CONTRIBUTOR`. Endpoints sécurisés. |
| **Système de Suggestions** | ✅ **Terminé** | Endpoints et logique pour la création, validation et le rejet des suggestions. |
| **DTOs & Mapping** | ✅ **Terminé** | DTOs (records) pour toutes les opérations. Le mapping est réalisé via des classes Mapper dédiées (`RegionMapper`, `DivisionMapper`, etc.). |
| **Gestion des Médias** | ✅ **Terminé** | Upload, service et **suppression des anciennes images** fonctionnels pour les entités. |
| **Gestion des Erreurs** | ✅ **Terminé** | Centralisée et robuste via `ApplicationControllerAdvice`. |
| **Opérations en masse** | ✅ **Terminé** | Endpoint `createSeveral` pour la création en cascade (ex: une région et ses divisions). |

---

## 🗺️ Feuille de Route d'Évolution

L'analyse du code a révélé que de nombreuses fonctionnalités des phases 1, 2 et 4 étaient déjà implémentées. La roadmap est réorganisée pour refléter les vraies priorités.

### 🏁 **Phase 1 (Priorité Haute) — Qualité et Fiabilisation**
**Objectif :** Garantir la non-régression, la stabilité et documenter l'API pour les futurs développements. C'est le chantier le plus important.

- [~] **Compléter la couverture de tests :**
    -   [x] **Structure de test créée.** Le répertoire `src/test/java` est en place.
    -   [~] **Écrire des tests unitaires pour les `Services`.** Une base de tests solide et de bonne qualité a été écrite pour la majorité des services. La couverture doit être finalisée (ex: `SuggestionService`).
    -   [ ] Écrire des tests d'intégration pour les `Controllers` avec `MockMvc` pour valider les contrats d'API (endpoints, DTOs, codes de statut HTTP).
- [ ] **Documenter l'API :**
    -   [ ] Intégrer **SpringDoc (OpenAPI 3)** en ajoutant la dépendance au `pom.xml`.
    -   [ ] Configurer SpringDoc et annoter les contrôleurs pour générer une documentation Swagger UI interactive.

---

### 🧱 **Phase 2 (Priorité Moyenne) — Montée en Charge et Performance**
**Objectif :** Assurer que l'API peut gérer un grand volume de données sans dégradation des performances.

- [ ] **Implémenter la Pagination et le Filtrage :**
    -   [ ] **Aucun endpoint n'est paginé.** Modifier tous les endpoints `GET` retournant des listes pour qu'ils acceptent un paramètre `Pageable`.
    -   [ ] Ajouter des paramètres de filtrage de base (ex: `GET /api/divisions?regionId=1`).
- [ ] **Recherche Avancée :**
    - [ ] Implémenter une recherche textuelle sur plusieurs champs avec Spring Data JPA Specifications.
- [ ] **Mettre en place un Cache :**
    - [ ] Intégrer un cache (ex: avec `Caffeine` ou `Redis`) sur les données peu volatiles pour améliorer les temps de réponse.

---

### 🏛️ **Phase 3 (Priorité Basse) — Complétude Fonctionnelle**
**Objectif :** Finaliser les fonctionnalités restantes.

- [ ] **Généraliser la gestion des médias :**
    -   [x] L'upload est déjà fonctionnel pour `Region`, `Division`, `Subdivision`.
    -   [ ] Permettre l'upload d'images pour les `Neighborhood`.
    -   [ ] Créer le dossier `uploads/neighborhoods`.
    -   [ ] Étendre le `MediaController` pour servir ces nouvelles images.
- [ ] **Audit :**
    - [ ] Ajouter des champs `dateCreation`, `dateModification` aux entités principales pour tracer les changements.

---

### 🚀 **Phase 4 – Industrialisation et Déploiement**
**Objectif :** Préparer le projet pour la production et automatiser le déploiement.

- [ ] **Conteneurisation :**
    -   [ ] Créer un `Dockerfile` pour l'application Spring Boot.
    -   [ ] Créer un fichier `docker-compose.yml` pour lancer l'application et une base de données facilement.
- [ ] **Mettre en place une CI/CD (Intégration Continue / Déploiement Continu) :**
    -   [ ] Créer un workflow **GitHub Actions** qui, à chaque `push` sur la branche `main` :
        1.  Compile le code.
        2.  Lance les tests (sera utile une fois la Phase 1 terminée).
        3.  Construit l'image Docker.
        4.  (Optionnel) Pousse l'image sur un registre (Docker Hub, GHCR).
- [ ] **Mettre à jour le `README.md` principal :**
    -   [ ] Ajouter les instructions de lancement via Docker.
    -   [ ] Inclure le lien vers la documentation Swagger une fois la Phase 1 terminée.

---

## ✓ Fonctionnalités Terminées (Archives)

- [x] **Intégrer Spring Security** (Phase 1 initiale)
- [x] **Mettre à jour la logique de suppression des images** (Phase 1 initiale)
- [x] **Refactoring des Mappers** (Phase 2 initiale)
- [x] **Activer les opérations en cascade** (Phase 2 initiale)
- [x] **Mettre en place le rôle `CONTRIBUTEUR`** (Phase 4 initiale)
- [x] **Développer le système de suggestions** (Phase 4 initiale)