# NoSkip - Application de Gestion des Absences des Étudiants
## Aperçu du Projet
NoSkip est une application web et mobile complète destinée à la gestion des absences des étudiants. Développée dans le cadre du module Bases de Données NoSQL, elle permet aux enseignants de suivre l'assiduité de leurs étudiants de manière centralisée, intuitive et sécurisée.

## Contexte et Objectifs
- Digitalisation : Remplacer les méthodes traditionnelles (feuilles papier, Excel) par une solution numérique fiable

- Suivi en temps réel : Offrir aux enseignants un outil de suivi avec alertes automatiques

- Reporting avancé : Générer des rapports PDF/Excel pour les statistiques et les seuils d'absences dépassés

- Accessibilité multiplateforme : Application web et mobile pour une utilisation flexible

## Fonctionnalités
- Authentification
  
- Gestion des Classes

- Gestion des Étudiants

- Marquage des Absences

- Alertes Automatiques

- Statistiques et Rapports

- Bloc-notes Personnel

- Version Mobile

## Architecture et Technologies
### Frontend Web
- React.js - Bibliothèque JavaScript pour interfaces utilisateur

- Tailwind CSS - Framework CSS utilitaire

- Context API - Gestion d'état globale

- Axios - Client HTTP pour les appels API

### Backend
- Spring Boot - Framework Java pour applications web

- Maven - Gestion des dépendances et build

- Spring Security - Authentification et autorisation

- Spring Data MongoDB - Intégration avec la base NoSQL

### Base de Données
- MongoDB - Base de données NoSQL orientée documents

- Collections principales :

professors - Informations des enseignants

classes - Données des classes et modules

étudiants - Listes et informations des étudiants

absences - Historique des présences/absences

notes - Bloc-notes personnel des enseignants

### Application Mobile
- Android SDK - Développement natif Android

- Java - Langage de programmation principal

- Retrofit - Client HTTP pour API REST

- Material Design - Guidelines d'interface utilisateur

- Room Database - Persistance locale des données

## Outils de Développement
- VS Code - Éditeur pour le développement frontend

- IntelliJ IDEA Ultimate - IDE pour le backend Spring Boot

- Android Studio - Environnement de développement mobile

- MongoDB Compass - Interface visuelle pour la base de données

- Figma - Conception des maquettes et prototypes

- Git/GitHub - Versionning et collaboration d'équipe

## Diagramme de cas d'utilisation
<img width="747" height="582" alt="Screenshot 2025-11-01 at 23 23 56" src="https://github.com/user-attachments/assets/46982b96-81a0-43e2-afe2-766b568f9537" />







