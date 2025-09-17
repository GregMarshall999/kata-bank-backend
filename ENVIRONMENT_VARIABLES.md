# Variables d'Environnement

Ce document liste toutes les variables d'environnement qui peuvent être configurées pour l'application Kata Bank API.

## Configuration de la Base de Données

| Variable | Description | Valeur par Défaut | Requis |
|----------|-------------|-------------------|--------|
| `DATABASE_URL` | URL de connexion à la base de données | `jdbc:mysql://localhost:3306/kata_bank?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC` | Non |
| `DATABASE_USERNAME` | Nom d'utilisateur de la base de données | `root` | Non |
| `DATABASE_PASSWORD` | Mot de passe de la base de données | (vide) | Non |

## Configuration JPA

| Variable | Description | Valeur par Défaut | Requis |
|----------|-------------|-------------------|--------|
| `JPA_DDL_AUTO` | Mode DDL automatique Hibernate (create, create-drop, validate, update, none) | `update` | Non |
| `JPA_SHOW_SQL` | Afficher les requêtes SQL dans les logs | `true` | Non |
| `JPA_FORMAT_SQL` | Formater la sortie SQL pour une meilleure lisibilité | `true` | Non |

## Configuration des Logs

| Variable | Description | Valeur par Défaut | Requis |
|----------|-------------|-------------------|--------|
| `LOG_LEVEL_ROOT` | Niveau de log racine (TRACE, DEBUG, INFO, WARN, ERROR) | `INFO` | Non |
| `LOG_LEVEL_WEB` | Niveau de log Spring Web | `DEBUG` | Non |
| `LOG_LEVEL_HIBERNATE_SQL` | Niveau de log SQL Hibernate | `DEBUG` | Non |
| `LOG_LEVEL_HIBERNATE_BINDER` | Niveau de log de liaison des paramètres Hibernate | `TRACE` | Non |

## Configuration du Serveur

| Variable | Description | Valeur par Défaut | Requis |
|----------|-------------|-------------------|--------|
| `SERVER_PORT` | Numéro de port du serveur | `8080` | Non |

## Configuration de Sécurité

| Variable | Description | Valeur par Défaut | Requis |
|----------|-------------|-------------------|--------|
| `JWT_SECRET` | Clé secrète JWT pour la signature des tokens | `ZGVmYXVsdC1qd3Qtc2VjcmV0LWtleS1mb3ItZGV2ZWxvcG1lbnQtb25seS1jaGFuZ2UtaW4tcHJvZHVjdGlvbg==` | **Oui** (changer en production) |
| `JWT_EXPIRATION` | Temps d'expiration du token JWT en millisecondes | `86400000` (24 heures) | Non |

## Exemple de Configuration d'Environnement

### Environnement de Développement
```bash
# Base de données
export DATABASE_URL="jdbc:mysql://localhost:3306/kata_bank?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
export DATABASE_USERNAME="root"
export DATABASE_PASSWORD=""

# JPA
export JPA_DDL_AUTO="update"
export JPA_SHOW_SQL="true"
export JPA_FORMAT_SQL="true"

# Logs
export LOG_LEVEL_ROOT="INFO"
export LOG_LEVEL_WEB="DEBUG"
export LOG_LEVEL_HIBERNATE_SQL="DEBUG"
export LOG_LEVEL_HIBERNATE_BINDER="TRACE"

# Serveur
export SERVER_PORT="8080"

# Sécurité
export JWT_SECRET="votre-secret-jwt-developpement"
export JWT_EXPIRATION="3600000"
```

### Environnement de Production
```bash
# Base de données
export DATABASE_URL="jdbc:mysql://production-db:3306/kata_bank?useSSL=true&serverTimezone=UTC"
export DATABASE_USERNAME="prod_user"
export DATABASE_PASSWORD="mot_de_passe_production_securise"

# JPA
export JPA_DDL_AUTO="validate"
export JPA_SHOW_SQL="false"
export JPA_FORMAT_SQL="false"

# Logs
export LOG_LEVEL_ROOT="WARN"
export LOG_LEVEL_WEB="INFO"
export LOG_LEVEL_HIBERNATE_SQL="WARN"
export LOG_LEVEL_HIBERNATE_BINDER="WARN"

# Serveur
export SERVER_PORT="8080"

# Sécurité
export JWT_SECRET="votre-secret-jwt-production-securise"
export JWT_EXPIRATION="86400000"
```

## Variables d'Environnement Docker

Lors de l'exécution avec Docker, vous pouvez définir ces variables dans votre `docker-compose.yml` :

```yaml
environment:
  - DATABASE_URL=jdbc:mysql://db:3306/kata_bank?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
  - DATABASE_USERNAME=root
  - DATABASE_PASSWORD=password
  - JWT_SECRET=votre-secret-jwt
  - LOG_LEVEL_ROOT=INFO
```

## Notes Importantes

1. **Secret JWT** : Toujours changer le secret JWT par défaut dans les environnements de production
2. **Mot de passe de base de données** : Ne jamais commiter les mots de passe de base de données dans le contrôle de version
3. **Niveaux de log** : Utiliser des niveaux de log appropriés pour chaque environnement (DEBUG pour le développement, WARN/ERROR pour la production)
4. **SSL** : Activer SSL pour les connexions de base de données dans les environnements de production
