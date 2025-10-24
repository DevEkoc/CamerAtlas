# Contexte de Développement et Stratégie de Test - Projet CamerAtlas

Ce document résume l'état actuel du projet, les décisions prises et les prochaines étapes concernant l'écriture de la suite de tests pour l'application.

## 1. Objectif

L'objectif est de construire une suite de tests complète et robuste pour l'API Spring Boot `camerAtlas`, en couvrant toutes les couches (Repository, Service, Controller) et tous les cas de validation.

## 2. Stratégie de Test Adoptée

-   **Base de données de test :** H2 en mémoire, configurée dans `application-test.properties`.
-   **Frameworks :** JUnit 5, Mockito, et AssertJ.
-   **Conventions de nommage :**
    -   Tests unitaires : `NomDeLaClasseTest.java`
    -   Tests d'intégration : `NomDeLaClasseIT.java`
    -   Méthodes de test : En français, décrivant une seule responsabilité.

### Approche par Couche :

-   **Couche Repository :** Tests d'intégration avec `@DataJpaTest`.
    -   *Exemple : `RegionRepositoryTest.java`*
-   **Couche Service :** Tests unitaires avec `@ExtendWith(MockitoExtension.class)`, en moquant les dépendances.
    -   *Exemple : `RegionServiceTest.java`*
-   **Couche Controller :** Tests d'intégration complets avec `@SpringBootTest` et `MockMvc`.
    -   *Exemple : `RegionControllerIT.java`*

### Exigences Clés :

-   **Couverture de validation exhaustive :** Pour les tests de contrôleur, chaque contrainte de validation sur les DTOs d'entrée (`@Valid`) doit être testée avec un cas d'échec spécifique.
-   **Activation du profil de test :** L'annotation `@ActiveProfiles("test")` est indispensable sur les classes de test d'intégration (`@SpringBootTest`) pour s'assurer que la configuration de la base de données en mémoire H2 (`application-test.properties`) est bien chargée à la place de la configuration de production.

## 3. Stratégie de Nettoyage de la Base de Données

Pour garantir l'indépendance des tests, nous utilisons une approche à 3 niveaux :
1.  **`spring.jpa.hibernate.ddl-auto=create-drop`**: Le schéma de la base de données est entièrement recréé au début de la suite de tests.
2.  **`@Transactional`**: Chaque méthode de test est exécutée dans une transaction qui est annulée à la fin (rollback).
3.  **Nettoyage manuel via `@BeforeEach`**: Pour les tests d'intégration (`...IT.java`), une méthode de nettoyage explicite vide les tables dans le bon ordre pour éviter les erreurs de clé étrangère.
    ```java
    @BeforeEach
    void nettoyerLaBase() {
        arrondissementRepository.deleteAll();
        departementRepository.deleteAll();
        regionRepository.deleteAll();
        // Ajouter d'autres repositories au besoin...
    }
    ```

## 4. Progression Actuelle

-   **Entité `Region` :** La couverture de test est complète pour les 3 couches.
-   **Entité `Departement` :** La couverture de test est complète pour les 3 couches.

## 4. Progression Actuelle

-   **Entité `Region` :** La couverture de test est complète pour les 3 couches.
-   **Entité `Departement` :** La couverture de test est complète pour les 3 couches.
-   **Entité `Arrondissement` :** La couverture de test est complète pour les 3 couches.

## 4. Progression Actuelle

-   **Entité `Region` :** La couverture de test est complète pour les 3 couches.
-   **Entité `Departement` :** La couverture de test est complète pour les 3 couches.
-   **Entité `Arrondissement` :** La couverture de test est complète pour les 3 couches.
-   **Entité `Quartier` :** La couverture de test est complète pour les 3 couches.

## 4. Progression Actuelle

-   **Entité `Region` :** La couverture de test est complète pour les 3 couches.
-   **Entité `Departement` :** La couverture de test est complète pour les 3 couches.
-   **Entité `Arrondissement` :** La couverture de test est complète pour les 3 couches.
-   **Entité `Quartier` :** La couverture de test est complète pour les 3 couches.
-   **Entité `Autorite` :** La couverture de test est complète pour les 3 couches.
-   **Entité `Affectation` :** La couverture de test est complète pour les 3 couches.

## 5. Prochaines Étapes

-   Appliquer la même stratégie de test aux entités suivantes :
    1.  **`Frontiere`**
    2.  **`Delimitation`**

    Les prochaines entités à tester sont **`Frontiere`** et **`Delimitation`**.