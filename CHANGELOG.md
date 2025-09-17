# Journal des modifications

Toutes les modifications apportées à ce projet seront documentées dans ce fichier.

## [0.0.4-SNAPSHOT]

### Ajouté
- **Fonctionnalité Rapport de Compte**
  - **Demandes Rapport Mensuel** : Les utilisateurs peuvent demander des relevés bancaires détaillés pour leurs comptes montrant l'activité du mois en cours
  - **Suivi de l'Activité des Comptes** : Piste d'audit sur toutes opérations de compte incluant les dépôts, retraits et modifications.
  - **Support de la Pagination** : Les historiques de relevés volumineux sont paginés pour des performances et une expérience utilisateur optimales
  - **Support des Types de Compte** : Les demandes de relevé fonctionnent pour les types de compte Fond et Épargne
  - **Historique des Opérations** : Enregistrements détaillés des opérations avec horodatage, montants et auteurs des opérations

- **Nouvelle resource API**
  - `GET /api/audit-account/statement/{accountType}/{ownerId}/{page}/{size}` - Demander un rapport de compte avec pagination

- **Modèles de Données Améliorés**
  - **AccountStatementDto** : Nouveau DTO pour les réponses de relevé avec solde de compte et opérations paginées
  - **OperationDto** : Nouveau DTO pour les enregistrements d'opérations individuelles dans les relevés
  - **AccountAuditController** : Nouveau contrôleur pour l'audit de compte et les demandes de relevé
  - **AuditService Amélioré** : Service d'audit étendu avec capacités de génération de relevé

- **Suite de Tests**
  - **Tests de Contrôleur** : Tests unitaires complets pour AccountAuditController (`AccountAuditControllerTest.java`)
  - **Tests de Service** : Tests unitaires pour la fonctionnalité de relevé du service d'audit (`AuditServiceTest.java`)
  - **Tests d'Intégration** : Tests API de bout en bout pour les demandes de relevé (`AccountAuditIntegrationTest.java`)
  - **Tests DTO** : Validation des DTO de relevé et cas limites (`AccountStatementDtoTest.java`, `OperationDtoTest.java`)
  - **Tests de Sécurité** : Validation d'autorisation pour l'accès aux relevés

- **Documentation API Améliorée**
  - **Intégration Swagger** : Documentation OpenAPI complète pour les resources de relevé
  - **Descriptions Détaillées** : Règles métier et contraintes clairement documentées
  - **Documentation des Réponses d'Erreur** : Documentation de la gestion d'erreurs
  - **Documentation des Schémas** : Documentation améliorée des schémas DTO et de réponse

- **Implémentation des Règles Métier**
  - **Validation d'Autorisation** : Les utilisateurs ne peuvent accéder qu'à leurs propres relevés de compte
  - **Validation du Type de Compte** : Validation appropriée des types de compte Fond vs Épargne
  - **Validation de Pagination** : Validation du numéro de page et de la taille pour des performances optimales
  - **Portée d'Activité Mensuelle** : Les relevés montrent les opérations du mois passé (fenêtre glissante)
  - **Chronologie des Opérations** : Les opérations sont triées par ordre chronologique inverse (les plus récentes en premier)

### Améliorations Techniques
- **Gestion d'Erreurs Améliorée** : Messages d'erreur spécifiques pour les échecs liés aux relevés
- **Améliorations de Sécurité** : Vérifications d'autorisation pour toutes les opérations de relevé
- **Qualité du Code** : Couverture de tests pour la nouvelle fonctionnalité
- **Documentation** : Documentation API mise à jour avec la fonctionnalité de relevé
- **Couche Service** : AuditService amélioré avec capacités de génération de relevé
- **Couche Contrôleur** : Nouveau AccountAuditController avec resources RESTful pour les relevés

### Changements Cassants
- Aucun

### Déprécié
- Aucun

---

## [0.0.3-SNAPSHOT]

### Ajouté
- **Fonctionnalité de Compte d'Épargne**
  - **Ouverture de Compte d'Épargne** : Les utilisateurs peuvent créer de nouveaux comptes d'épargne avec des limites de solde maximum configurables
  - **Fermeture de Compte d'Épargne** : Les utilisateurs peuvent fermer des comptes d'épargne lorsque le solde est zéro
  - **Opérations de Dépôt d'Épargne** : Dépôt d'argent dans des comptes d'épargne avec validation du solde maximum
  - **Opérations de Retrait d'Épargne** : Retrait d'argent des comptes d'épargne avec protection du solde
  - **Gestion du Solde Maximum** : Limites de solde maximum configurables par compte d'épargne
  - **Application des Règles Métier** : Validation des opérations d'épargne

- **Nouvelles Resources API**
  - `POST /api/saving/open` - Créer un nouveau compte d'épargne
  - `POST /api/saving/close` - Fermer un compte d'épargne existant
  - `POST /api/saving/deposit` - Dépôt d'argent dans un compte d'épargne
  - `POST /api/saving/withdraw` - Retrait d'argent d'un compte d'épargne

- **Modèles de Données Améliorés**
  - **Entité Saving** : Nouvelle entité avec solde, maxBalance et relation propriétaire
  - **SavingDto** : Nouveau DTO pour les opérations d'épargne avec validation
  - **SavingMapper** : Mapper MapStruct pour les conversions entité-DTO
  - **SavingRepository** : Interface de repository pour la persistance des données d'épargne

- **Suite de Tests**
  - **Tests d'Entité** : Tests unitaires complets pour l'entité Saving (`SavingTest.java`)
  - **Tests de Service** : Tests unitaires pour toute la logique métier d'épargne (`SavingServiceTest.java`)
  - **Tests d'Intégration** : Tests API de bout en bout pour la fonctionnalité d'épargne (`SavingIntegrationTest.java`)
  - **Tests d'Exception** : Gestion des exceptions d'épargne et validation (`SavingExceptionTest.java`)
  - **Tests de Gestion d'Erreurs** : Couverture des scénarios d'erreur pour les opérations d'épargne

- **Documentation API Améliorée**
  - **Intégration Swagger** : Documentation OpenAPI complète pour les resources d'épargne
  - **Descriptions Détaillées** : Règles métier et contraintes clairement documentées
  - **Documentation des Réponses d'Erreur** : Documentation de la gestion d'erreurs
  - **Documentation des Schémas** : Documentation améliorée des schémas DTO et de réponse

- **Implémentation des Règles Métier**
  - **Protection du Solde Maximum** : Les dépôts ne peuvent pas dépasser le solde maximum configuré
  - **Exigence de Solde Zéro** : Les comptes d'épargne ne peuvent être fermés que lorsque le solde est zéro
  - **Prévention du Solde Négatif** : Les retraits ne peuvent pas résulter en soldes négatifs
  - **Validation d'Autorisation** : Toutes les opérations nécessitent une autorisation utilisateur appropriée
  - **Unicité des Comptes** : Les utilisateurs ne peuvent pas ouvrir plusieurs comptes d'épargne avec le même ID

### Améliorations Techniques
- **Gestion d'Erreurs Améliorée** : Messages d'erreur spécifiques pour les échecs liés à l'épargne
- **Améliorations de Sécurité** : Vérifications d'autorisation pour toutes les opérations d'épargne
- **Qualité du Code** : Couverture de tests pour la nouvelle fonctionnalité
- **Documentation** : TEST.md mis à jour avec la documentation de test d'épargne
- **Couche Service** : Nouveau SavingService avec opérations CRUD complètes
- **Couche Contrôleur** : Nouveau SavingController avec resources RESTful

### Changements Cassants
- Aucun

### Déprécié
- Aucun

---

## [0.0.2-SNAPSHOT]

### Ajouté
- **Fonctionnalité de Découvert Bancaire**
  - **Demande Activation de Découvert** : Les utilisateurs peuvent demander l'autorisation de retirer plus que leur solde actuel
  - **Demande Désactivation de Découvert** : Les utilisateurs peuvent annuler la fonctionnalité de découvert lorsque le solde n'est pas négatif
  - **Opérations de Retrait Améliorées** : La fonctionnalité de retrait prend maintenant en charge le découvert lorsqu'il est activé
  - **Gestion des Limites de Découvert** : Montants maximum de découvert configurables par fonds
  - **Application des Règles Métier** : Validation des opérations de découvert

- **Nouvelles Resources API**
  - `PUT /api/fund/request-overdraw` - Activer les capacités de découvert sur un fonds
  - `PUT /api/fund/cancel-overdraw` - Désactiver les capacités de découvert sur un fonds
  - Amélioré `POST /api/fund/withdraw` - Prend maintenant en charge la fonctionnalité de découvert

- **Modèles de Données Améliorés**
  - **Entité Fund** : Ajout du champ booléen `canOverdraw` et du champ double `maxOverdraw`
  - **OverdrawDto** : Nouveau DTO pour les opérations de découvert avec paramètre maxOverdraw
  - **DTO Améliorés** : Ajout de la documentation Swagger à tous les DTO liés aux fonds

- **Suite de Tests**
  - **Tests de Couche Service** : Tests unitaires complets pour toute la logique métier de découvert
  - **Tests d'Intégration** : Tests API de bout en bout pour la fonctionnalité de découvert
  - **Tests d'Entité** : Tests des capacités de découvert de l'entité Principal
  - **Tests DTO** : Validation OverdrawDto et cas limites
  - **Tests de Gestion d'Erreurs** : Couverture des scénarios d'erreur

- **Documentation API Améliorée**
  - **Intégration Swagger** : Documentation OpenAPI complète pour les resources de découvert
  - **Descriptions Détaillées** : Règles métier et contraintes clairement documentées
  - **Documentation des Réponses d'Erreur** : Documentation de la gestion d'erreurs
  - **Documentation des Schémas** : Documentation améliorée des schémas DTO et de réponse

- **Implémentation des Règles Métier**
  - **Autorisation de Découvert** : Les utilisateurs doivent demander les capacités de découvert avant de les utiliser
  - **Protection du Solde** : Impossible d'annuler le découvert lorsque le solde est négatif
  - **Application des Limites** : Les retraits ne peuvent pas dépasser les limites de découvert
  - **Validation d'Autorisation** : Toutes les opérations nécessitent une autorisation utilisateur appropriée

### Améliorations Techniques
- **Gestion d'Erreurs Améliorée** : Messages d'erreur spécifiques pour les échecs liés au découvert
- **Améliorations de Sécurité** : Vérifications d'autorisation pour toutes les opérations de découvert
- **Qualité du Code** : Couverture de tests pour la nouvelle fonctionnalité
- **Documentation** : TEST.md mis à jour avec la documentation de test de découvert

### Changements Cassants
- Aucun

### Déprécié
- Aucun

---

## [0.0.1-SNAPSHOT]

### Ajouté
- **Gestion des Utilisateurs Bancaires**
  - Fonctionnalité d'enregistrement et de création d'utilisateurs
  - Authentification des utilisateurs avec JWT (JSON Web Tokens)
  - Contrôle d'accès basé sur les rôles avec l'énumération `BankRole`
  - Gestion sécurisée des mots de passe et validation

- **Opérations de Fonds**
  - Fonctionnalité de dépôt pour ajouter des fonds aux comptes
  - Fonctionnalité de retrait pour retirer des fonds des comptes
  - Validation et traitement des transactions
  - Suivi et gestion du solde des fonds

- **Fonctionnalités de Sécurité**
  - Système d'authentification basé sur JWT
  - Intégration Spring Security
  - Resources API protégés
  - Filtres d'autorisation pour un accès sécurisé

- **Règles Métier**
  - **Politique d'Interdiction de Solde Négatif** : Les retraits sont empêchés lorsqu'ils résulteraient en un solde négatif
  - Validation des transactions pour assurer l'intégrité des comptes
  - Application de la logique métier pour les opérations de fonds

- **Infrastructure Technique**
  - Framework d'application Spring Boot 3.5.5
  - Intégration de base de données MySQL pour la production
  - Base de données H2 pour l'environnement de test
  - JPA/Hibernate pour la persistance des données
  - MapStruct pour le mapping d'objets
  - Couverture de tests incluant les tests unitaires et d'intégration
  - Conception API RESTful avec codes de statut HTTP appropriés
  - Gestion d'exceptions globale avec réponses d'erreur personnalisées

- **Resources API**
  - Resources d'authentification (connexion, enregistrement)
  - Resources de gestion des utilisateurs bancaires
  - Resources d'opérations de fonds (dépôt, retrait)
  - Opérations CRUD de base avec support de pagination

- **Documentation API**
  - Intégration Swagger/OpenAPI 3 pour la documentation interactive de l'API
  - Documentation API auto-générée à partir des annotations de code
  - Interface de test API interactive à `/swagger-ui.html`
  - Point de terminaison de documentation API à `/api-docs`
  - Configuration Swagger UI personnalisée pour une meilleure expérience utilisateur

- **Modèles de Données**
  - Entité `BankUser` avec informations utilisateur et rôles
  - Entité `Fund` pour gérer les soldes de compte et transactions
  - DTOs pour la gestion des requêtes/réponses API
  - Entités et DTOs de base pour la fonctionnalité commune

### Détails Techniques
- **Version Java** : 17
- **Framework** : Spring Boot 3.5.5
- **Base de Données** : MySQL 9.4.0 (production), H2 (test)
- **Sécurité** : JWT 0.13.0
- **Mapping** : MapStruct 1.6.3
- **Architecture** : Architecture hexagonale avec séparation claire des préoccupations

### Sécurité
- Authentification basée sur les tokens JWT
- Chiffrement et stockage sécurisé des mots de passe
- Autorisation basée sur les rôles
- Resources API protégés
- Validation et assainissement des entrées

### Tests
- Tests unitaires pour toute la logique métier
- Tests d'intégration pour les resources API
- Tests de couche repository
- Tests de couche service avec dépendances simulées
- Tests de sécurité avec Spring Security Test

---

## Historique des Versions

- **0.0.4-SNAPSHOT** - Version de fonctionnalité Rapport de compte
  - Demandes de rapport de compte mensuel avec pagination
  - Piste d'audit et historique des opérations
  - Documentation API améliorée pour les resources de rapport
  - Couverture de tests complète pour la fonctionnalité de rapport

- **0.0.3-SNAPSHOT** - Version de fonctionnalité de compte d'épargne
  - Gestion complète des comptes d'épargne (ouverture, fermeture, dépôt, retrait)
  - Validation et protection du solde maximum
  - Couverture de tests pour les opérations d'épargne
  - Documentation API améliorée et application des règles métier

- **0.0.2-SNAPSHOT** - Version de fonctionnalité de découvert bancaire
  - Capacités de découvert (demande, annulation, retrait)
  - Documentation API améliorée avec Swagger
  - Couverture de tests complète pour la fonctionnalité de découvert
  - Application des règles métier et validation

- **0.0.1-SNAPSHOT** - Version initiale avec fonctionnalité bancaire de base
  - Authentification et autorisation des utilisateurs
  - Opérations de dépôt et retrait de fonds
  - Prévention du solde négatif
  - Infrastructure API complète

---

## Licence

Ce projet fait partie du défi kata de l'Exalt Company.
