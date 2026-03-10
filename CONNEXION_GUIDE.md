# Guide de Connexion Frontend-Backend

## Architecture
- **Backend**: Spring Boot (Java) sur le port 8080
- **Frontend**: Angular sur le port 4200
- **Base de données**: MySQL sur le port 3306

## Configuration réalisée

### 1. Configuration CORS
Le backend est configuré pour accepter les requêtes depuis `http://localhost:4200` (Angular).

### 2. Proxy Angular
Un fichier `proxy.conf.json` a été créé pour rediriger les appels `/api/*` vers `http://localhost:8080`.

### 3. Gestion des environnements
- `environment.ts` : Configuration pour le développement (utilise le proxy)
- `environment.prod.ts` : Configuration pour la production (URL directe)

### 4. Authentification JWT
- Intercepteur HTTP pour ajouter automatiquement le token JWT
- Service de gestion des tokens
- Guard pour protéger les routes

## Prérequis et Configuration

### 1. Configuration MySQL (IMPORTANT)
Votre MySQL nécessite probablement un mot de passe. Exécutez d'abord :
```bash
setup-mysql.bat
```
Ce script va :
- Vérifier que MySQL fonctionne
- Créer la base de données `parent_portal`
- Configurer le mot de passe dans les propriétés

### 2. Alternative manuelle MySQL
Si le script ne fonctionne pas, configurez manuellement :

1. Ouvrez MySQL Command Line Client ou MySQL Workbench
2. Exécutez : `CREATE DATABASE IF NOT EXISTS parent_portal;`
3. Modifiez `Control-Parent(back)/src/main/resources/application.properties` :
   ```properties
   spring.datasource.password=VOTRE_MOT_DE_PASSE_MYSQL
   ```

## Démarrage

### Option 1: Script automatique (recommandé)
```bash
start-dev.bat
```
Ce script vérifie MySQL, compile le backend, et démarre les deux serveurs.

### Option 2: Démarrage manuel

#### Backend
```bash
cd Control-Parent(back)
./mvnw spring-boot:run
```

#### Frontend
```bash
cd Control-Parent(front)
npm install
npm start
```

## URLs
- **Backend API**: http://localhost:8080/api
- **Frontend**: http://localhost:4200

## Endpoints principaux
- `POST /api/auth/demander-code` - Demander un code OTP
- `POST /api/auth/verifier-code` - Vérifier le code OTP
- `GET /api/etudiants/mes-enfants` - Récupérer les enfants d'un parent (avec token JWT)
- `GET /api/pdf/notes/{matricule}` - Télécharger les notes (PDF)
- `GET /api/pdf/emploi-du-temps/{classeId}` - Télécharger l'emploi du temps (PDF)

## Résolution des problèmes courants

### 1. Erreur "Access denied for user 'root'@'localhost'"
- Exécutez `setup-mysql.bat`
- Ou configurez manuellement le mot de passe MySQL dans `application.properties`

### 2. Erreur "Unknown database 'parent_portal'"
- La base sera créée automatiquement au premier démarrage
- Ou créez-la manuellement : `CREATE DATABASE parent_portal;`

### 3. Port 8080 déjà utilisé
- Changez le port dans `application.properties` : `server.port=8081`
- Mettez à jour l'URL dans `proxy.conf.json` et `environment.prod.ts`

### 4. Erreurs de compilation Maven
- Vérifiez que Java 17+ est installé : `java -version`
- Nettoyez et recompilez : `./mvnw clean compile`

### 5. Erreurs npm
- Supprimez `node_modules` et `package-lock.json`
- Réinstallez : `npm install`

## Test de la connexion
1. Démarrer le backend (doit afficher "Started BackendApplication")
2. Démarrer le frontend (doit ouvrir http://localhost:4200)
3. Tester l'authentification avec un numéro de téléphone
4. Vérifier les appels API dans les outils de développement du navigateur (F12)