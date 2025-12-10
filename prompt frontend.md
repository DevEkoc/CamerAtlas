Tu es un expert Next.js, TypeScript, Tailwind et intégration API sécurisée.
Je veux que tu génères un frontend complet en Next.js 14 (App Router), en TypeScript, utilisant TailwindCSS, react-query ou axios, et conforme à l’API CamerAtlas.

Respecte strictement ces contraintes :

BACKEND (à respecter côté frontend) :
- API Base URL : http://localhost:8080
- CORS autorise http://localhost:3000 et allowCredentials=true
- Les endpoints d'auth sont :
  POST /auth/login → renvoie {access_token} et SET un cookie httpOnly "refresh_token"
  POST /auth/refresh → lit le cookie httpOnly et renvoie {access_token}
  POST /auth/logout → supprime le cookie
  GET /auth/me → renvoie l’utilisateur connecté
- Toutes les autres routes sont protégées par Authorization: Bearer <access_token>
- Les images sont accessibles via /media/... (URL relative)

OBJECTIF FRONTEND :
Créer une application Next.js App Router complète, propre, modulaire, type-safe, qui :
- gère l'authentification (login, logout, session, refresh automatique)
- protège certaines routes
- récupère les données des endpoints (regions, divisions, etc.)
- affiche des listes + pages de détails
- utilise Tailwind pour le style
- utilise axios comme client HTTP, avec un wrapper capable de rafraîchir le token automatiquement
- utilise un context Auth pour stocker l’access_token en mémoire
- utilise withCredentials=true pour envoyer le cookie httpOnly

DÉROULEMENT (MAX 8 ÉTAPES) :
Tu dois produire la solution en 8 étapes, chaque étape devant fournir :
- les commandes terminal à exécuter
- les fichiers créés/modifiés (chemin + contenu complet)
- comment tester localement à cette étape

ÉTAPES DEMANDÉES :
1) Initialisation du projet Next.js 14 App Router en TypeScript + installation TailwindCSS + configuration de base.
2) Création du layout global (app/layout.tsx), Header, Footer, styles Tailwind.
3) Création de lib/api.ts : wrapper axios complet avec access token en mémoire + interception 401 + appel automatique au refresh.
4) Création de l'AuthContext : login(), logout(), loadUser(), persist accessToken en mémoire, fetch /auth/me.
5) Création des pages : /login, /dashboard, /regions, /regions/[id], avec des composants simples (listes, détails).
6) Création d’un middleware ou guard (server-side ou client-side) pour protéger les routes nécessitant un utilisateur connecté.
7) Gestion des images via un composant MediaImage qui reconstruit l’URL complète à partir du path envoyé par l’API.
8) Fichier env.example, instructions de build (dev/prod), check-list backend (CORS, cookies, /auth/me…), plus un test manuel via curl pour vérifier le login.

À la fin de chaque étape : arrête-toi et attends que je dise “NEXT” avant de passer à l’étape suivante.
