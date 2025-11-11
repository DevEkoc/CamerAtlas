# Liste des Endpoints de l'API CamerAtlas (Vision Cible)

Ce document détaille la structure cible des endpoints de l'API, en accord avec la vision d'une API sémantique et hiérarchique.

---

## 🔑 Rôles d'Accès

*   **Public** : Accès en lecture seule à toutes les données publiées.
*   **CONTRIBUTEUR** : Peut soumettre des suggestions d'ajout, de modification ou de suppression.
*   **ADMIN** : Contrôle total sur les données et la validation des suggestions.

---

## 🤖 Endpoints d'Authentification

| Méthode | URI | Accès | Description |
|:---|:---|:---:|:---|
| `POST` | `/api/auth/login` | **Public** | Connecte un utilisateur (Contributeur ou Admin) et retourne un token JWT. |
| `POST` | `/api/auth/register` | **Public** | Enregistre un nouvel utilisateur avec le rôle `CONTRIBUTEUR`. |

---

## 🌍 Endpoints de Consultation (Accès Public)

Ces endpoints permettent une navigation hiérarchique et retournent des entités sémantiques complètes.

| Méthode | URI | Description |
|:---|:---|:---|
| `GET` | `/api/media/{...}` | Récupère un fichier média (image). |
| `GET` | `/api/regions` | Liste toutes les régions (informations de base). |
| `GET` | `/api/regions/{id}` | Récupère une région (données complètes, incluant gouverneur, frontières et liste des départements). |
| `GET` | `/api/regions/{id}/divisions` | Récupère la liste des départements d'une région. |
| `GET` | `/api/divisions/{id}` | Récupère un département (données complètes, incluant préfet, frontières et liste des arrondissements). |
| `GET` | `/api/divisions/{id}/subdivisions` | Récupère la liste des arrondissements d'un département. |
| `GET` | `/api/subdivisions/{id}` | Récupère un arrondissement (données complètes, incluant sous-préfet et liste des quartiers). |
| `GET` | `/api/subdivisions/{id}/neighborhoods`| Récupère la liste des quartiers d'un arrondissement. |
| `GET` | `/api/authorities/{id}` | Récupère les informations publiques d'une autorité. |

---

## 🔒 Endpoints d'Administration (Rôle ADMIN)

Endpoints pour la gestion directe des données et la consultation de listes complètes.

| Méthode | URI | Description |
|:---|:---|:---|
| `POST` | `/api/admin/regions` | Crée une région. |
| `PUT` | `/api/admin/regions/{id}` | Modifie une région. |
| `DELETE`| `/api/admin/regions/{id}` | Supprime une région. |
| `GET` | `/api/admin/divisions` | **(Admin)** Liste tous les départements (pour administration). |
| `POST` | `/api/admin/divisions` | Crée un département. |
| `PUT` | `/api/admin/divisions/{id}` | Modifie un département. |
| `DELETE`| `/api/admin/divisions/{id}` | Supprime un département. |
| `GET` | `/api/admin/subdivisions` | **(Admin)** Liste tous les arrondissements. |
| `POST` | `/api/admin/subdivisions` | Crée un arrondissement. |
| `PUT` | `/api/admin/subdivisions/{id}` | Modifie un arrondissement. |
| `DELETE`| `/api/admin/subdivisions/{id}` | Supprime un arrondissement. |
| `GET` | `/api/admin/neighborhoods` | **(Admin)** Liste tous les quartiers. |
| `POST` | `/api/admin/neighborhoods` | Crée un quartier. |
| `PUT` | `/api/admin/neighborhoods/{id}` | Modifie un quartier. |
| `DELETE`| `/api/admin/neighborhoods/{id}` | Supprime un quartier. |
| `GET` | `/api/admin/authorities` | **(Admin)** Liste toutes les autorités. |
| `POST` | `/api/admin/authorities` | Crée une autorité. |
| `PUT` | `/api/admin/authorities/{id}` | Modifie une autorité. |
| `DELETE`| `/api/admin/authorities/{id}` | Supprime une autorité. |
| `GET` | `/api/admin/suggestions` | **(Admin)** Liste toutes les suggestions en attente. |
| `POST` | `/api/admin/suggestions/{id}/approve` | Approuve une suggestion. |
| `POST` | `/api/admin/suggestions/{id}/reject` | Rejette une suggestion. |

---

## ✍️ Endpoints de Contribution (Rôle CONTRIBUTEUR)

Endpoints pour proposer des changements.

| Méthode | URI | Description |
|:---|:---|:---|
| `POST` | `/api/suggestions/regions` | Suggère la création d'une région. |
| `PUT` | `/api/suggestions/regions/{id}` | Suggère la modification d'une région. |
| `DELETE`| `/api/suggestions/regions/{id}` | Suggère la suppression d'une région. |
| `POST` | `/api/suggestions/divisions` | Suggère la création d'un département. |
| `PUT` | `/api/suggestions/divisions/{id}` | Suggère la modification d'un département. |
| `DELETE`| `/api/suggestions/divisions/{id}` | Suggère la suppression d'un département. |
| `...` | `...` | (et ainsi de suite pour les autres entités) |