Tu es chargé de créer un frontend professionnel en Next.js 14 (App Router), TypeScript, TailwindCSS, pour l’API CamerAtlas déjà existante.

L’objectif est d’obtenir une interface fluide, moderne, accessible, réactive, adaptée à :

une API REST publique (consultation)

une zone admin sécurisée (CRUD + suggestions)

Tu dois suivre strictement les contraintes, la palette graphique et les 8 étapes séquentielles.
Contraintes générales

Utiliser App Router (app/).

Cookies HttpOnly pour refresh token (fetch avec credentials: "include").

UI moderne, minimaliste, aérée, responsive (mobile-first).

Livrer étape par étape (max 8 étapes). Ne passer à l’étape suivante que si je valide l’étape en cours.

Couleurs & graphisme (obligatoire)

Palette principale (primary / secondary / accent / neutral / success / error) :

Primary : #0F62FE (bleu vif)

Primary-700 / hover : #0353E9

Secondary : #00B39F (vert-bleu)

Accent : #FF7A59 (orange doux)

Neutral-100 : #F8FAFC ; Neutral-700 : #374151

Success : #16A34A ; Error : #EF4444

Typographie : système + Inter (préconfigurer via Tailwind).

Composants : cartes avec rounded-2xl, shadow-sm, spacing généreux p-4 / p-6.

Animations : micro-interactions 150–250ms, utiliser Framer Motion pour transitions de pages et modales.

Mode sombre : variantes contrastées (optionnel mais prévoir classes).

Comportement & UX (obligatoire)

Interface fluide : loaders skeleton pour listes, animations douces, lazy images.

Responsive grid : mobile (1col), md (2col), lg (3col+).

Formulaires : validation client (zod ou yup) + messages serveurs visibles.

Uploads d’images : preview avant envoi, progress bar, support drag & drop.

Pagination / infinite scroll selon liste (paramétrable) — prévoir page + size.

Optimisations pour l’API : utiliser React Query (ou SWR) pour cache, invalidation et rafraîchissement automatique après /auth/refresh.

Suggestions : interface optimistique (submit -> apparait en attente), historique, page admin pour approve/reject.

Sécurité & intégration auth

Login : POST /auth/login -> récupère access_token (body) + cookie HttpOnly refresh_token.

Tous les fetchs incluent credentials: "include".

Intercepteur (helper) pour : injecter Authorization: Bearer ${accessToken} dans headers, et si 401 -> appeler /auth/refresh (POST, cookie) pour obtenir nouveau access token, retry automatique.

Logout : appeler /auth/logout (cookie supprimé côté serveur).

/auth/me exposé pour seed auth state côté client (SSG/SSR ou client fetch).

Structure du projet (obligatoire)

app/ (routes, layouts)

components/ui/ (Button, Card, Modal, FormField, FileUpload, Toast)

components/regions/, components/divisions/...

lib/api/ (fetch wrapper, auth helper)

services/ (region.service.ts, auth.service.ts, suggestion.service.ts...)

hooks/ (useAuth, usePagination, useToast)

styles/ (tailwind config, theme)

public/ (icons, placeholders)

Bundle de librairies recommandées

react-query (ou @tanstack/react-query) pour cache/data fetching

framer-motion pour animations

react-hook-form + zod pour formulaires et validation

clsx pour classes conditionnelles

axios facultatif (ou fetch natif) — si axios, intégrer interceptor pour refresh token

tailwindcss/typography plugin pour styling riche

Étapes (max 8) — exécuter strictement dans l’ordre

Init & config

Créer projet Next.js + TypeScript.

Installer Tailwind + config (inclure palette).

Installer dépendances recommandées.

Configurer App Router, global layout, Fonts (Inter), provider React Query et Framer Motion.

Commit initial.

Core API layer & auth helper

Implémenter lib/api/fetchClient.ts (wrapper fetch ou axios) : credentials: "include", auto-refresh logic (call /auth/refresh on 401, retry once).

Créer services/auth.service.ts (login, logout, refresh, me).

Créer hook useAuth() qui expose user, login, logout, isAuthenticated.

Design System minimal & composants UI

Button, Input, Card, Modal, FileUpload (with preview), Toast, Skeleton.

Tailwind theme + tokens de couleurs et spacing.

Ajouter animations micro (hover/active) + accessible focus states.

Pages Auth & navigation

/login, /profile (me), layout/navbar avec état auth, rôle badge (ADMIN/CONTRIBUTOR).

Guard admin routes via middleware or client check.

CRUD Regions (exemple canonique)

List (paginated), Detail (with children), Create/Edit (multipart image upload), Delete.

Use React Query for data, optimistic updates for create.

Image preview + upload progress.

Pattern réutilisable pour Divisions / SubDivisions / Neighborhoods

Générer pages/lists/forms basées sur pattern Region.

Pagination, filtering (?regionId=), search.

Suggestions & Admin workflow

Public form to submit suggestion (payload JSON builder, or typed forms per target).

Admin panel to list pending suggestions, approve/reject, show diff (old vs payload).

Optimistic UI on approve/reject with rollback on error.

Polish & déploiement

Tests e2e basiques (Cypress / Playwright) pour login, list, create region.

Lighthouse check, performance optimisations, production build.

Document how to set env (API_URL, cookie domain, secure flags).

Remarques finales (à inclure dans la génération)

Toutes les requêtes publiques (GET lists/details) doivent être cachées au niveau du client (stale-while-revalidate).

Les actions admin utilisent Authorization header avec access token.

Prévoir pages statiques côté front pour le public (ISR/SSG) pour les listes si tu veux scale.

Fournir un README d’installation + scripts (dev, build, start, lint, test).

Commence immédiatement par l’ÉTAPE 1 : initialisation et configuration (tailwind, palette, providers).
Rends-moi la structure de fichiers créée + les fichiers de config (package.json, tailwind.config.js, next.config.js, tsconfig.json) et le snippet app/layout.tsx + provider React Query.

Ne passe pas à l’étape 2 avant ma validation.