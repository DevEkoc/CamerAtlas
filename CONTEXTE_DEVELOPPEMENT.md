
### Session du mardi 16 décembre 2025

**Problèmes et Résolutions - Authentification & Cookies :**

*   **Problème initial :** L'utilisateur rencontrait des erreurs 404 sur les sous-pages de l'interface utilisateur (`/demo/ui/buttons`, etc.) et une impossibilité de se connecter au frontend, malgré le backend démarré.
*   **Résolution 404 sur les pages UI :** L'erreur 404 était due à une erreur dans les liens générés sur la page d'index de l'UI. Le chemin `/demo/` avait été inclus par erreur, alors que `(demo)` est un groupe de routes Next.js qui ne doit pas apparaître dans l'URL. Les liens ont été corrigés pour pointer vers `/ui/buttons`, `/ui/cards`, etc.
*   **Problème de connexion (Backend `HttpMessageNotReadableException`) :**
    *   **Diagnostic :** Le backend signalait une erreur de `HttpMessageNotReadableException` indiquant qu'il ne pouvait pas désérialiser une chaîne simple en `UserConnectionDTO`. Simultanément, le frontend affichait une erreur TypeScript (`Argument of type 'string' is not assignable to parameter of type 'LoginCredentials'`).
    *   **Cause :** La fonction `login` du frontend était appelée de manière incorrecte avec deux arguments de type `string` (`login(username, password)`) au lieu d'un seul objet (`login({ username, password })`). De plus, le `loginMutation` n'était pas utilisé de manière idiomatique avec ses callbacks `onSuccess` et `onError`.
    *   **Correction Frontend :**
        *   Le hook `useAuth` a été refactorisé pour exposer l'objet `loginMutation` complet (`loginMutation.mutate`).
        *   La page `login/page.tsx` a été mise à jour pour appeler `loginMutation.mutate({ username, password })` et gérer les redirections et erreurs via les callbacks `onSuccess` et `onError` de la mutation.
        *   Le gestionnaire d'erreurs dans `auth.service.ts` a été amélioré pour tenter de parser et d'afficher les messages d'erreur spécifiques du backend.
*   **Problème de déconnexion au rafraîchissement de la page (Cookies `SameSite`) :**
    *   **Symptôme :** Après une connexion réussie, un rafraîchissement de la page déconnectait l'utilisateur. Le cookie `refresh_token` (HttpOnly) n'était pas envoyé avec la requête de rafraîchissement silencieux.
    *   **Diagnostic :** Le problème venait de l'attribut `SameSite=Lax` du cookie `refresh_token` combiné à la nature cross-origin des requêtes `POST` entre le frontend (localhost:3000) et le backend (localhost:8080). `SameSite=Lax` empêche l'envoi de cookies sur les requêtes `POST` cross-origin.
    *   **Solution choisie (Proxy de développement) :** Pour contourner le problème sans avoir à configurer HTTPS en développement, un proxy a été mis en place sur le serveur de développement Next.js.
        *   **Backend (`camerAtlas/src/main/java/com/devekoc/camerAtlas/controllers/UserController.java`) :** L'attribut `SameSite` du cookie `refresh_token` a été rétabli à `"Lax"` (car il est plus sécurisé pour les requêtes "same-origin" et sera compatible avec le proxy).
        *   **Frontend (`cameratlas-frontend/next.config.ts`) :** Une configuration `rewrites` a été ajoutée pour rediriger toutes les requêtes vers `/api/:path*` vers `http://localhost:8080/api/:path*`.
        *   **Frontend (`cameratlas-frontend/src/lib/api/config.ts`) :** La constante `API_BASE` a été modifiée de `http://localhost:8080/api` à `/api` pour utiliser le proxy.
    *   **Étapes pour l'utilisateur :** Pour que cette solution prenne effet, l'utilisateur doit redémarrer ses serveurs backend et frontend, et effacer les cookies de son navigateur pour `localhost`.
