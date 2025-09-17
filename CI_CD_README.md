# Configuration CI/CD pour l'API Kata Bank

Ce document décrit la configuration du pipeline CI/CD pour le projet API Kata Bank utilisant GitLab CI/CD.

## Aperçu

Le pipeline CI/CD comprend les étapes suivantes :
1. **Validate** - Vérifications de qualité du code et validation des dépendances
2. **Build** - Compilation et empaquetage
3. **Test** - Tests unitaires et d'intégration
4. **Security** - Analyse de sécurité et analyse du code
5. **Deploy** - Déploiement vers les environnements de staging et de production

## Étapes du Pipeline

### 1. Étape Validate
- Validation de la structure du projet Maven
- Vérifications des vulnérabilités des dépendances
- Validation du style de code (si configuré)

### 2. Étape Build
- Compile l'application
- Exécute les tests unitaires
- Crée le package JAR
- Construit l'image Docker (pour la branche principale)

### 3. Étape Test
- Tests d'intégration avec la base de données MySQL
- Rapport de couverture de tests

### 4. Étape Security
- Analyse des vulnérabilités des dépendances OWASP
- Analyse de qualité du code SonarQube

### 5. Étape Deploy
- Déploiement manuel vers l'environnement de staging
- Déploiement manuel vers l'environnement de production

## Prérequis

### Variables GitLab
Définissez les variables suivantes dans les paramètres de votre projet GitLab (Settings > CI/CD > Variables) :

#### Variables Requises :
- `SONAR_HOST_URL` - URL du serveur SonarQube
- `SONAR_TOKEN` - Token d'authentification SonarQube

#### Variables Optionnelles :
- `DATABASE_URL` - URL de la base de données de production
- `DATABASE_USERNAME` - Nom d'utilisateur de la base de données de production
- `DATABASE_PASSWORD` - Mot de passe de la base de données de production
- `JWT_SECRET` - Secret JWT de production

## Développement Local

### Utilisation de Docker Compose

1. **Démarrer l'application avec tous les services :**
   ```bash
   docker-compose up -d
   ```

2. **Voir les logs :**
   ```bash
   docker-compose logs -f kata-bank-api
   ```

3. **Arrêter tous les services :**
   ```bash
   docker-compose down
   ```

4. **Reconstruire et démarrer :**
   ```bash
   docker-compose up --build -d
   ```

### Construction Docker Manuelle

1. **Construire l'image Docker :**
   ```bash
   docker build -t kata-bank-api .
   ```

2. **Exécuter le conteneur :**
   ```bash
   docker run -p 8080:8080 \
     -e DATABASE_URL=jdbc:mysql://host.docker.internal:3306/kata_bank \
     -e DATABASE_USERNAME=root \
     -e DATABASE_PASSWORD=password \
     kata-bank-api
   ```

## Configuration de l'Environnement

### Environnement de Développement
- Profil : `dev`
- Base de données : MySQL local
- Journalisation : Niveau DEBUG
- Swagger : Activé

### Environnement Docker
- Profil : `docker`
- Base de données : Conteneur MySQL
- Journalisation : Niveau INFO
- Swagger : Activé

### Environnement de Production
- Profil : `prod`
- Base de données : MySQL de production
- Journalisation : Niveau WARN
- Swagger : Désactivé
- Sécurité : Renforcée

## Déploiement Kubernetes

### Prérequis
- Cluster Kubernetes
- kubectl configuré
- Helm (optionnel)

### Étapes de Déploiement

1. **Créer le namespace :**
   ```bash
   kubectl create namespace kata-bank
   ```

2. **Créer les secrets :**
   ```bash
   kubectl apply -f k8s/secrets.yaml -n kata-bank
   ```

3. **Déployer l'application :**
   ```bash
   kubectl apply -f k8s/deployment.yaml -n kata-bank
   kubectl apply -f k8s/service.yaml -n kata-bank
   ```

4. **Vérifier le déploiement :**
   ```bash
   kubectl get pods -n kata-bank
   kubectl get services -n kata-bank
   ```

## Considérations de Sécurité

### Sécurité Docker
- Utilisateur non-root dans le conteneur
- Image de base minimale (JRE uniquement)
- Vérifications de santé implémentées
- Limites de ressources définies

### Sécurité Kubernetes
- Secrets pour les données sensibles
- Limites et demandes de ressources
- Sondes de liveness et readiness
- Politiques réseau (recommandé)

### Sécurité de l'Application
- Secrets JWT dans les variables d'environnement
- Identifiants de base de données dans les secrets
- Application HTTPS
- Validation des entrées

## Surveillance et Journalisation

### Vérifications de Santé
- Point de terminaison de santé de l'application : `/actuator/health`
- Vérification de connectivité de la base de données
- Surveillance de l'espace disque

### Métriques
- Métriques Prometheus activées
- Métriques d'application exposées
- Métriques métier personnalisées (peuvent être ajoutées)

### Journalisation
- Journalisation structurée
- Rotation des logs
- Différents niveaux de log par environnement

## Dépannage

### Problèmes Courants

1. **La construction échoue avec des problèmes de dépendances :**
   ```bash
   mvn clean install -U
   ```

2. **La construction Docker échoue :**
   - Vérifier que le démon Docker fonctionne
   - S'assurer d'avoir suffisamment d'espace disque
   - Vérifier la syntaxe du Dockerfile

3. **Problèmes de connexion à la base de données :**
   - Vérifier que la base de données fonctionne
   - Vérifier la chaîne de connexion
   - S'assurer de la connectivité réseau

4. **Problèmes de déploiement Kubernetes :**
   ```bash
   kubectl describe pod <pod-name> -n kata-bank
   kubectl logs <pod-name> -n kata-bank
   ```

### Commandes de Débogage

1. **Vérifier le statut du pipeline :**
   - GitLab CI/CD > Pipelines

2. **Voir les artefacts de construction :**
   - Télécharger les fichiers JAR depuis les artefacts du pipeline

3. **Tester localement :**
   ```bash
   mvn spring-boot:run -Dspring.profiles.active=dev
   ```

## Bonnes Pratiques

### Qualité du Code
- Écrire des tests unitaires pour toutes les nouvelles fonctionnalités
- Maintenir une couverture de tests au-dessus de 80%
- Suivre les standards de codage
- Utiliser des messages de commit significatifs

### Sécurité
- Ne jamais commiter de secrets dans le dépôt
- Utiliser des variables d'environnement pour la configuration
- Mettre à jour régulièrement les dépendances
- Scanner les vulnérabilités

### Performance
- Surveiller l'utilisation des ressources
- Optimiser les requêtes de base de données
- Utiliser le pooling de connexions
- Implémenter la mise en cache quand approprié

## Support

Pour les problèmes liés à :
- **Pipeline CI/CD** : Consulter la documentation GitLab CI/CD
- **Docker** : Se référer à la documentation Docker
- **Kubernetes** : Consulter la documentation Kubernetes
- **Application** : Examiner la documentation Spring Boot

## Contribution

Lors de la contribution à ce projet :
1. Créer une branche de fonctionnalité
2. Écrire des tests pour la nouvelle fonctionnalité
3. S'assurer que tous les tests passent
4. Créer une demande de fusion
5. Attendre que le pipeline CI/CD se termine
6. Demander une révision de code
