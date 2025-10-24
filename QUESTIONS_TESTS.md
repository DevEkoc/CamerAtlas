# Questions pour la Stratégie de Test de l'Application CamerAtlas

Bonjour !

Pour vous aider à mettre en place une stratégie de test robuste et cohérente pour votre application Spring Boot, j'ai besoin de comprendre certains aspects de votre projet et de vos préférences.

Merci de répondre aux questions suivantes. Vos réponses me permettront de générer des tests pertinents et d'adopter les bonnes pratiques déjà en place.

## 1. Contexte Général et Priorités

*   Quelle est la fonctionnalité la plus critique de l'application que nous devrions tester en priorité ?
*   Y a-t-il des parties du code que vous suspectez d'être fragiles ou complexes et qui mériteraient une attention particulière ?
*   R/ En fait comme je suis débutant, je viens de coder une API complète et je veux tester le code avant d'ajouter les données réelles.

## 2. Outils et Frameworks

*   Je vois que vous utilisez JUnit. Confirmez-vous que nous devons utiliser **JUnit 5** (qui est le standard avec les versions récentes de Spring Boot) ? R/ Oui
*   Utilisez-vous une bibliothèque d'assertions spécifique comme **AssertJ** (recommandé et inclus par défaut) ou préférez-vous les assertions standard de JUnit ? R/ Oui
*   Pour les tests unitaires, nous utiliserons **Mockito** pour mocker les dépendances. Est-ce que cela vous convient ? R/ Oui

## 3. Stratégie de Test par Couche

### Couche Repository (`@DataJpaTest`)

*   Pour tester la couche d'accès aux données, préférez-vous utiliser une base de données en mémoire (comme **H2**) ou des conteneurs de test (**Testcontainers**) pour lancer une vraie base de données (ex: PostgreSQL, MySQL) dans un conteneur Docker ? R/ Je préfère une BD en mémoire
*   La base de données en mémoire est plus rapide, tandis que Testcontainers garantit que les tests s'exécutent contre le même type de base de données qu'en production. Quelle est votre préférence ?

### Couche Service

*   Pour les services, l'approche standard est de faire des tests unitaires en moquant les repositories et les autres dépendances. Êtes-vous d'accord avec cette approche ? R/ Oui

### Couche Controller (`@WebMvcTest` / `@SpringBootTest`)

*   **Tests d'intégration légers :** Souhaitez-vous utiliser `@WebMvcTest` pour tester les contrôleurs ? Cela permet de tester la couche web (validation des DTOs, codes de statut HTTP, etc.) sans charger tout le contexte de l'application, en moquant la couche service.
*   **Tests d'intégration complets :** Ou préférez-vous des tests de bout en bout avec `@SpringBootTest` qui lancent l'application complète et envoient de vraies requêtes HTTP, testant ainsi l'intégration de toutes les couches jusqu'à la base de données ? R/ Je veux les tests complets

## 4. Conventions et Qualité

*   J'ai remarqué les fichiers `AffectationIntegrationTest.java` et `AutoriteControllerIT.java`. Y a-t-il une convention de nommage que vous souhaitez suivre ? Par exemple :
    *   `NomDeLaClasseTest.java` pour les tests unitaires.
    *   `NomDeLaClasseIT.java` pour les tests d'intégration.
*   Avez-vous un objectif de couverture de code (code coverage) à atteindre ? R/ Exactement

*   Egalement, je veux que pour les tests qui nécessitent des données préexistantes, (par exemple pour créer une affectation, il faut qu'il y ait une autorité et une circonscription), tu crées ces données préexistantes
Une fois que vous m'aurez fourni ces informations, je pourrai commencer à écrire les tests de manière efficace.
