# Prompts pour le Scraping des Données de CamerAtlas

Ce document contient une série de prompts destinés à une IA pour scraper les données administratives, géographiques et officielles du Cameroun. L'objectif est de collecter ces informations et de les formater en JSON, prêtes à être utilisées pour peupler la base de données de l'application CamerAtlas.

**Instructions Générales pour l'IA :**
- **Source principale :** Utilise Wikipedia (en français de préférence) comme source de départ. Croise les informations avec d'autres sources fiables si possible (sites officiels, documents administratifs).
- **Format de sortie :** Pour chaque prompt, la sortie doit être un tableau JSON (`array of objects`).
- **Gestion des données manquantes :** Si une information spécifique n'est pas trouvée pour un enregistrement, utilise la valeur `null` pour le champ correspondant dans le JSON. Ne pas inventer de données.
- **Précision des données :** Fais de ton mieux pour fournir des données précises, notamment pour les chiffres de population et de superficie. Indique l'année du recensement si disponible, sinon, fournis la donnée la plus récente.

---

### Prompt 1: Scraper les Régions du Cameroun

**Tâche :**
Scrape les informations sur les 10 régions du Cameroun.

**Champs à extraire :**
- `nom`: Le nom officiel de la région (ex: "Centre").
- `chefLieu`: Le chef-lieu de la région (ex: "Yaoundé").
- `superficie`: La superficie en km², en tant que nombre entier.
- `population`: Le nombre total d'habitants, en tant que nombre entier.
- `codeMineralogique`: Le code à deux lettres utilisé pour les plaques d'immatriculation (ex: "CE").
- `coordonnees`: Les coordonnées géographiques du chef-lieu au format "latitude, longitude".

**Format de sortie attendu (exemple) :**
```json
[
  {
    "nom": "Centre",
    "chefLieu": "Yaoundé",
    "superficie": 68953,
    "population": 4159500,
    "codeMineralogique": "CE",
    "coordonnees": "3.8667, 11.5167"
  },
  {
    "nom": "Littoral",
    "chefLieu": "Douala",
    "superficie": 20248,
    "population": 3355000,
    "codeMineralogique": "LT",
    "coordonnees": "4.05, 9.7"
  }
]
```

---

### Prompt 2: Scraper les Départements du Cameroun

**Tâche :**
Scrape les informations sur les 58 départements du Cameroun.

**Champs à extraire :**
- `nom`: Le nom officiel du département (ex: "Mfoundi").
- `prefecture`: La préfecture (chef-lieu) du département (ex: "Yaoundé").
- `superficie`: La superficie en km², en tant que nombre entier.
- `population`: Le nombre total d'habitants, en tant que nombre entier.
- `nomRegion`: Le nom de la région à laquelle le département appartient (ex: "Centre").

**Format de sortie attendu (exemple) :**
```json
[
  {
    "nom": "Mfoundi",
    "prefecture": "Yaoundé",
    "superficie": 297,
    "population": 1247500,
    "nomRegion": "Centre"
  },
  {
    "nom": "Wouri",
    "prefecture": "Douala",
    "superficie": 1200,
    "population": 1514978,
    "nomRegion": "Littoral"
  }
]
```

---

### Prompt 3: Scraper les Arrondissements du Cameroun

**Tâche :**
Scrape les informations sur tous les arrondissements du Cameroun.
**Note :** Cette information peut être difficile à trouver de manière centralisée. Il faudra probablement consulter les pages Wikipedia de chaque département.

**Champs à extraire :**
- `nom`: Le nom officiel de l'subDivision (ex: "Yaoundé I").
- `sousPrefecture`: La sous-préfecture (chef-lieu) de l'subDivision.
- `superficie`: La superficie en km². Utilise `null` si non disponible.
- `population`: Le nombre total d'habitants. Utilise `null` si non disponible.
- `nomDepartement`: Le nom du département auquel l'subDivision appartient (ex: "Mfoundi").

**Format de sortie attendu (exemple) :**
```json
[
  {
    "nom": "Yaoundé I",
    "sousPrefecture": "Nlongkak",
    "superficie": null,
    "population": null,
    "nomDepartement": "Mfoundi"
  },
  {
    "nom": "Douala V",
    "sousPrefecture": "Bonamoussadi",
    "superficie": null,
    "population": null,
    "nomDepartement": "Wouri"
  }
]
```

---

### Prompt 4: Scraper les Frontières du Cameroun

**Tâche :**
Identifie les frontières internationales du Cameroun.

**Champs à extraire :**
- `type`: Le point cardinal de la frontière (valeurs possibles : `NORD`, `SUD`, `EST`, `OUEST`). Une frontière peut être sur plusieurs points cardinaux (ex: Nigeria à l'Ouest et au Nord).
- `limite`: Le nom du pays voisin ou de la mer/océan (ex: "Nigeria", "Océan Atlantique").

**Format de sortie attendu (exemple) :**
```json
[
  {
    "type": "NORD",
    "limite": "Tchad"
  },
  {
    "type": "OUEST",
    "limite": "Nigeria"
  },
  {
    "type": "EST",
    "limite": "République centrafricaine"
  },
  {
    "type": "SUD",
    "limite": "Gabon"
  }
]
```

---

### Prompt 5: Scraper les Autorités Administratives

**Tâche :**
Trouve les informations sur les dirigeants administratifs actuels du Cameroun (Gouverneurs de région, Préfets de département).
**Note :** Ces données sont personnelles et dynamiques. Fais de ton mieux pour trouver les informations les plus récentes. Si une donnée (comme la date de naissance) est introuvable, utilise `null`.

**Champs à extraire :**
- `nom`: Le nom de famille de l'autorité.
- `prenom`: Le prénom de l'autorité.
- `dateNaissance`: La date de naissance au format "DD-MM-YYYY".

**Format de sortie attendu (exemple) :**
```json
[
  {
    "nom": "Midjiyawa",
    "prenom": "Bakari",
    "dateNaissance": null
  },
  {
    "nom": "Okalia",
    "prenom": "Bilai",
    "dateNaissance": "25-04-1956"
  }
]
```

---

### Prompt 6: Déduire les Délimitations (Relations Circonscription-Frontière)

**Tâche :**
Pour chaque frontière du Cameroun, identifie les circonscriptions (régions ou départements) qui la longent directement. Déduis ces informations de sources géographiques.

**Champs à extraire :**
- `nomCirconscription`: Le nom de la région ou du département qui est frontalier.
- `typeFrontiere`: Le point cardinal de la frontière (`NORD`, `SUD`, `EST`, `OUEST`).

**Format de sortie attendu (exemple) :**
```json
[
  {
    "nomCirconscription": "Extrême-Nord",
    "typeFrontiere": "NORD"
  },
  {
    "nomCirconscription": "Logone-et-Chari",
    "typeFrontiere": "NORD"
  },
  {
    "nomCirconscription": "Adamaoua",
    "typeFrontiere": "EST"
  }
]
```

---

### Prompt 7: Déduire les Affectations (Relations Autorité-Circonscription)

**Tâche :**
Identifie le poste actuel des autorités administratives (Gouverneurs, Préfets, Sous-Préfets). Associe chaque autorité à sa circonscription et à sa fonction.

**Champs à extraire :**
- `nomCompletAutorite`: Le nom complet de l'autorité (ex: "Paul Naseri Bea").
- `nomCirconscription`: Le nom de la région, du département ou de l'subDivision où l'autorité est en poste.
- `fonction`: Le titre de l'autorité (valeurs possibles : `GOUVERNEUR`, `PREFET`, `SOUS_PREFET`).

**Format de sortie attendu (exemple) :**
```json
[
  {
    "nomCompletAutorite": "Paul Naseri Bea",
    "nomCirconscription": "Centre",
    "fonction": "GOUVERNEUR"
  },
  {
    "nomCompletAutorite": "Benjamin Mboutou",
    "nomCirconscription": "Wouri",
    "fonction": "PREFET"
  }
]
```
