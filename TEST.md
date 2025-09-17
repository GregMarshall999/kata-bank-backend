# API Bancaire - Documentation des tests

## Aperçu

Ce document sert de guide pour l'API Bancaire.
La suite de tests est conçue pour garantir la fiabilité, la sécurité et la fonctionnalité de l'application bancaire via plusieurs couches de tests.

## Structure des tests

La suite de tests suit une approche par couches avec la structure suivante :

```
src/test/java/com/exalt_company/kata_bank_api/
├── entity/                                   # Tests de la couche entité
│   ├── AccountAuditTest.java                 # Tests de l'entité AccountAudit
│   ├── BankUserTest.java                     # Tests de l'entité BankUser
│   ├── BaseEntityTest.java                   # Tests de l'entité de base
│   ├── FundTest.java                         # Tests de l'entité Fund
│   ├── FundOverdrawTest.java                 # Tests de la fonctionnalité de découvert
│   ├── SavingTest.java                       # Tests de l'entité Saving
│   └── user_fields/                          # Tests des champs embarqués
│       ├── IdentityTest.java                 # Tests de la classe embarquée Identity
│       └── CredentialsTest.java              # Tests de la classe embarquée Credentials
├── service/                                  # Tests de la couche service
│   ├── AuditServiceTest.java                 # Tests du service d'audit (incluant les relevés)
│   ├── AuthServiceTest.java                  # Tests du service d'authentification
│   ├── BankUserServiceTest.java              # Tests du service utilisateur bancaire
│   ├── BaseServiceTest.java                  # Tests du service de base
│   ├── FundServiceTest.java                  # Tests du service Fund (incluant le découvert)
│   └── SavingServiceTest.java                # Tests du service Saving
├── repository/                               # Tests de la couche repository
│   ├── AccountAuditRepositoryTest.java       # Tests du repository AccountAudit
│   ├── BankUserRepositoryTest.java           # Tests du repository BankUser
│   ├── FundRepositoryTest.java               # Tests du repository Fund
│   └── SavingRepositoryTest.java             # Tests du repository Saving
├── controller/                               # Tests de la couche contrôleur
│   ├── AccountAuditControllerTest.java       # Tests du contrôleur AccountAudit (resources de relevés)
│   ├── AuthControllerTest.java               # Tests du contrôleur d'authentification
│   ├── BankUserControllerTest.java           # Tests du contrôleur utilisateur bancaire
│   ├── BaseControllerTest.java               # Tests du contrôleur de base
│   ├── FundControllerTest.java               # Tests du contrôleur Fund
│   └── SavingControllerTest.java             # Tests du contrôleur Saving
├── dto/                                      # Tests des DTO
│   ├── auth/                                 # DTO d'authentification
│   │   ├── AuthenticationRequestTest.java    # Tests du DTO de demande d'authentification
│   │   ├── AuthenticationResponseTest.java   # Tests du DTO de réponse d'authentification
│   │   └── RegisterRequestTest.java          # Tests du DTO de demande d'inscription
│   ├── fund/                                 # DTO de Fund
│   │   ├── BaseFundDtoTest.java              # Tests du DTO de base Fund
│   │   ├── FundDtoTest.java                  # Tests du DTO Fund
│   │   ├── FundOpDtoTest.java                # Tests du DTO d'opération Fund
│   │   └── OverdrawDtoTest.java              # Tests du DTO de découvert
│   ├── statement/                            # DTO de relevé
│   │   ├── AccountStatementDtoTest.java      # Tests du DTO de relevé de compte
│   │   └── OperationDtoTest.java             # Tests du DTO d'opération
│   ├── BankUserDtoTest.java                  # Tests du DTO utilisateur bancaire
│   ├── BaseDtoTest.java                      # Tests du DTO de base
│   ├── PageDtoTest.java                      # Tests du DTO de pagination
│   ├── PasswordedBankUserDtoTest.java        # Tests du DTO utilisateur avec mot de passe
│   └── SavingDtoTest.java                    # Tests du DTO Saving
├── enums/                                    # Tests des énumérations
│   ├── AccountTypeTest.java                  # Tests de l'énumération AccountType
│   ├── AuditOperationTest.java               # Tests de l'énumération AuditOperation
│   └── BankRoleTest.java                     # Tests de l'énumération BankRole
├── exception/                                # Tests de gestion des exceptions
│   ├── AuditExceptionTest.java               # Tests de l'exception Audit
│   ├── AuthExceptionTest.java                # Tests de l'exception d'authentification
│   ├── BankApiExceptionTest.java             # Tests de l'exception Bank API
│   ├── BaseExceptionTest.java                # Tests de l'exception de base
│   ├── ErrorResponseTest.java                # Tests du DTO de réponse d'erreur
│   ├── FundExceptionTest.java                # Tests de l'exception Fund
│   ├── GlobalExceptionHandlerTest.java       # Tests du gestionnaire global d'exceptions
│   └── SavingExceptionTest.java              # Tests de l'exception Saving
├── integration/                              # Tests d'intégration
│   ├── AccountAuditIntegrationTest.java      # Tests de bout en bout d'audit et de relevés
│   ├── AuthIntegrationTest.java              # Tests de bout en bout d'authentification
│   ├── BankUserIntegrationTest.java          # Tests de bout en bout de gestion des utilisateurs
│   ├── FundIntegrationTest.java              # Tests de bout en bout de gestion des fonds
│   ├── OverdrawIntegrationTest.java          # Tests de bout en bout de la fonctionnalité de découvert
│   ├── SavingIntegrationTest.java            # Tests de bout en bout des opérations d'épargne
│   └── SecurityIntegrationTest.java          # Tests de bout en bout de la sécurité et de l'autorisation
└── security/                                 # Tests de sécurité
    └── SecurityConfigTest.java               # Tests de la configuration de sécurité
```

## Configuration des tests

### Propriétés de test

La suite de tests utilise un fichier de configuration dédié : `src/test/resources/application-test.properties`

```properties
# Configuration de la base H2 en mémoire
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# Configuration JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.defer-datasource-initialization=true

# Console H2 (pour le débogage)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Configuration JWT
security.jwt.secret=dGVzdFNlY3JldEtleUZvclRlc3RpbmdQdXJwb3Nlc09ubHlEb05vdFVzZUluUHJvZHVjdGlvbg==
security.jwt.expiration=86400000

# Configuration des logs
logging.level.com.exalt_company.kata_bank_api=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

### Dépendances

La suite de tests requiert les dépendances suivantes (déjà incluses dans `pom.xml`) :

```xml
<!-- Spring Boot Test Starter -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- Spring Security Test -->
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- Base de données H2 pour les tests -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

## Exécution des tests

### Exécuter tous les tests

```bash
mvn test
```

### Exécuter des catégories de tests spécifiques

```bash
# N'exécuter que les tests unitaires
mvn test -Dtest="*Test" -DfailIfNoTests=false

# N'exécuter que les tests d'intégration
mvn test -Dtest="*IntegrationTest" -DfailIfNoTests=false

# N'exécuter que les tests d'entité
mvn test -Dtest="*Test" -DfailIfNoTests=false

# N'exécuter que les tests de services
mvn test -Dtest="*ServiceTest" -DfailIfNoTests=false

# N'exécuter que les tests d'exceptions
mvn test -Dtest="*ExceptionTest" -DfailIfNoTests=false

# N'exécuter que les tests liés au découvert
mvn test -Dtest="*Overdraw*" -DfailIfNoTests=false

# N'exécuter que les tests liés à Fund (incluant le découvert)
mvn test -Dtest="*Fund*" -DfailIfNoTests=false

# N'exécuter que les tests liés à Saving
mvn test -Dtest="*Saving*" -DfailIfNoTests=false

# N'exécuter que les tests liés à l'audit
mvn test -Dtest="*Audit*" -DfailIfNoTests=false

# N'exécuter que les tests liés à l'authentification
mvn test -Dtest="*Auth*" -DfailIfNoTests=false

# N'exécuter que les tests liés aux relevés
mvn test -Dtest="*Statement*" -DfailIfNoTests=false

# N'exécuter que les tests liés à l'audit (incluant les relevés)
mvn test -Dtest="*Audit*" -DfailIfNoTests=false
```

### Exécuter des classes de test individuelles

```bash
# Exécuter une classe de test spécifique
mvn test -Dtest=BankUserTest

# Exécuter une méthode de test spécifique
mvn test -Dtest=BankUserTest#testBankUserCreation

# Exécuter les classes de tests liées au découvert
mvn test -Dtest=FundServiceTest
mvn test -Dtest=OverdrawIntegrationTest
mvn test -Dtest=FundOverdrawTest
mvn test -Dtest=OverdrawDtoTest

# Exécuter des méthodes de test spécifiques au découvert
mvn test -Dtest=FundServiceTest#testRequestOverdrawCapabilitiesSuccess
mvn test -Dtest=OverdrawIntegrationTest#testWithdrawWithOverdrawEnabledSuccess

# Exécuter les classes de tests liées à l'épargne
mvn test -Dtest=SavingServiceTest
mvn test -Dtest=SavingIntegrationTest
mvn test -Dtest=SavingTest
mvn test -Dtest=SavingExceptionTest

# Exécuter des méthodes de test spécifiques à l'épargne
mvn test -Dtest=SavingServiceTest#testOpenSavingsAccountSuccess
mvn test -Dtest=SavingIntegrationTest#testCompleteSavingsWorkflow

# Exécuter les classes de tests liées à l'audit
mvn test -Dtest=AuditServiceTest
mvn test -Dtest=AuditIntegrationTest
mvn test -Dtest=AccountAuditTest
mvn test -Dtest=AccountAuditRepositoryTest
mvn test -Dtest=AccountAuditControllerTest

# Exécuter les classes de tests liées à l'authentification
mvn test -Dtest=AuthServiceTest
mvn test -Dtest=AuthIntegrationTest
mvn test -Dtest=AuthExceptionTest

# Exécuter les classes de tests liées aux relevés
mvn test -Dtest=AccountStatementDtoTest
mvn test -Dtest=OperationDtoTest

# Exécuter les classes de tests liées aux utilisateurs bancaires
mvn test -Dtest=BankUserServiceTest
mvn test -Dtest=BankUserIntegrationTest
mvn test -Dtest=BankUserControllerTest
mvn test -Dtest=BankUserDtoTest

# Exécuter les classes de tests liées à la sécurité
mvn test -Dtest=SecurityConfigTest
mvn test -Dtest=SecurityIntegrationTest
```

## Couverture des tests

La suite de tests couvre :

- **Couche Entité** : 100% de couverture des classes entités et champs embarqués, incluant AccountAudit, BankUser, Fund, Saving, BaseEntity et les classes de champs utilisateur
- **Couche Service** : Tests de la logique métier avec dépendances mockées pour les services Audit, Auth, BankUser, Base, Fund et Saving
- **Couche Repository** : Tests des opérations base de données avec H2 pour les repositories AccountAudit, BankUser, Fund et Saving
- **Couche Contrôleur** : Tests des resources REST pour AccountAudit, Auth, BankUser, Base, Fund et Saving
- **Couche DTO** : Validation des objets de transfert pour l'authentification, les opérations de fonds, les relevés de compte, la gestion des utilisateurs et les opérations d'épargne
- **Couche Enum** : Tests des valeurs et comportements des énumérations AccountType, AuditOperation et BankRole
- **Couche Exception** : Tests de gestion des exceptions et des réponses d'erreur pour toutes les exceptions personnalisées incluant Audit, Auth, BankApi, Base, Fund et Saving
- **Couche Intégration** : Tests de bout en bout pour l'authentification, la gestion des utilisateurs, la gestion des fonds, les opérations d'épargne, les opérations d'audit, la fonctionnalité de découvert et la sécurité
- **Sécurité** : Tests de configuration de sécurité et d'autorisation pour tous les resources et opérations
- **Découvert bancaire** : Tests de la fonctionnalité de découvert incluant demande, annulation et retrait
- **Épargne bancaire** : Tests complets des opérations de compte d'épargne incluant ouverture, fermeture, dépôt et retrait avec validation de solde maximum
- **Système d'audit** : Tests complets des opérations d'audit, des opérations repository, des resources contrôleur et de la génération de relevés
- **Système de relevés** : Tests complets des relevés de compte et DTO d'opérations avec scénarios de validation complets
- **Gestion des utilisateurs bancaires** : Tests complets de l'inscription, de l'authentification et des opérations de gestion

## Tests de sécurité

La suite inclut des tests orientés sécurité :

- Génération et validation de jetons JWT
- Encodage et vérification des mots de passe
- Contrôle d'accès basé sur les rôles
- Scénarios d'échec d'authentification
- Sécurisation de l'inscription utilisateur
- Autorisation des opérations de fonds
- Autorisation et validation des opérations de découvert
- Autorisation et validation des opérations d'épargne
- Autorisation des opérations d'audit et contrôle d'accès aux relevés
- Sécurité des resources contrôleur
- Autorisation pour la gestion des utilisateurs bancaires
- Validation de la configuration de sécurité
- Prévention de l'accès inter-utilisateurs aux ressources

##  Performance

- Les tests utilisent une base H2 en mémoire pour une exécution rapide
- Les tests d'intégration utilisent @SpringBootTest pour un contexte applicatif complet
- Les tests repository utilisent @SpringBootTest pour l'intégration base de données
- Les données de test sont minimales et ciblées
- Les tests contrôleur utilisent MockMvc pour des resources isolés
- Les tests de relevés utilisent la pagination pour gérer efficacement les grands jeux de données

## Dépannage

### Problèmes courants

1. **Problèmes de connexion à la base de test**
   - Vérifier que la dépendance H2 est incluse
   - Vérifier la configuration `application-test.properties`
   - Vérifier la configuration @SpringBootTest pour les tests d'intégration

2. **Échecs des tests d'intégration**
   - Vérifier la configuration @SpringBootTest
   - Assurer une mise en place et un nettoyage corrects des données de test
   - Vérifier l'initialisation du schéma de base de données

3. **Problèmes de jetons JWT**
   - Vérifier la configuration du secret JWT dans les propriétés de test
   - Vérifier les paramètres d'expiration des jetons
   - S'assurer du bon format du jeton dans les requêtes de test

### Débogage des tests

1. **Activer les logs de débogage**
   ```properties
   logging.level.com.exalt_company.kata_bank_api=DEBUG
   logging.level.org.hibernate.SQL=DEBUG
   logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
   ```

2. **Utiliser la console H2**
   - Accès via http://localhost:8080/h2-console pendant les tests
   - JDBC URL : jdbc:h2:mem:testdb

3. **Exécuter les tests dans l'IDE**
   - Utiliser les runners de tests de l'IDE pour un meilleur débogage
   - Placer des points d'arrêt dans les méthodes de test
   - Utiliser @SpringBootTest pour les tests d'intégration à contexte complet

## Améliorations futures

Améliorations potentielles pour la suite de tests :

1. **Tests de contrôleurs supplémentaires** : Ajouter des tests dédiés pour les contrôleurs restants
2. **Tests de performance** : Ajouter des tests de charge pour les resources critiques
3. **Tests de contrat** : Mettre en place des tests de contrat orientés consommateur
4. **Mutation testing** : Ajouter des tests de mutation pour une meilleure qualité de tests
5. **Tests de documentation d'API** : Tester la documentation OpenAPI/Swagger
6. **Tests de migration de base de données** : Tester les scénarios d'évolution de schéma
7. **Tests d'intérêts d'épargne** : Ajouter des tests pour le calcul des intérêts si implémenté
8. **Tests de virement d'épargne** : Ajouter des tests pour les virements entre comptes d'épargne
9. **Tests de gestion des utilisateurs** : Ajouter des tests pour les opérations CRUD utilisateur
10. **Tests de sécurité offensive** : Ajouter des tests de vulnérabilités de sécurité


