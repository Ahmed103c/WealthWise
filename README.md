# WealthWise - Plateforme Intelligente de Gestion Financière

## Description
WealthWise est une plateforme interactive qui permet aux utilisateurs de gérer efficacement leurs finances personnelles. Grâce à l'intégration de l'intelligence artificielle et du traitement du langage naturel, WealthWise offre une analyse détaillée des transactions et des comptes bancaires, ainsi qu'un chatbot intelligent capable de répondre aux questions financières des utilisateurs.

L'application repose sur une architecture full-stack combinant Spring Boot pour le backend et Angular pour le frontend, avec une base de données PostgreSQL assurant la gestion des transactions financières et des utilisateurs.

## Fonctionnalités
- **Authentification sécurisée (JWT)**
- **Gestion des utilisateurs et comptes bancaires**
- **Intégration bancaire et gestion des transactions**
- **Système de budgétisation intelligente**
- **Catégorisation automatique des transactions**
- **Système prédictif de notifications financières**
- **Interface conversationnelle SQL via un chatbot intelligent**
- **Dashboard interactif avec visualisation des données financières**

## Technologies utilisées
- **Backend :** Spring Boot (Java)
- **Frontend :** Angular 19
- **Base de données :** PostgreSQL
- **Sécurité :** Authentification JWT
- **Machine Learning :** NLP avec spaCy, GPT-4, TensorFlow, Scikit-learn
- **Intégration bancaire :** GoCardless (mode sandbox)
- **CI/CD :** GitLab avec pipelines d'intégration continue

## Installation et Exécution
### Prérequis
- Java 17+
- Node.js 18+
- PostgreSQL
- Angular CLI


### Installation du Backend
1. Cloner le dépôt :
   ```bash
   git clone <repository_url>
   cd wealthwise-backend
   ```
2. Configurer la base de données PostgreSQL (ajuster `application.properties`).
3. Compiler et exécuter l'application Spring Boot :
   ```bash
   mvn spring-boot:run
   ```

### Installation du Frontend
1. Aller dans le dossier du frontend :
   ```bash
   cd wealthwise-frontend
   ```
2. Installer les dépendances :
   ```bash
   npm install
   ```
3. Lancer l'application Angular :
   ```bash
   ng serve
   ```
4. Accéder à l'application via [http://localhost:4200](http://localhost:4200)

## Utilisation
- Inscription et connexion sécurisée via JWT.
- Ajout de comptes bancaires et synchronisation des transactions.
- Consultation des analyses financières et gestion du budget.
- Utilisation du chatbot pour obtenir des insights financiers.



## Contributions
Les contributions sont les bienvenues ! Merci de créer une issue ou une pull request avec vos améliorations.

## Auteurs
- **Ahmed RZEIGUI**
- **Mayssa OUERGHI**
- **Alae HMAMOUCHE**
- **Wiem BEMBLI**


