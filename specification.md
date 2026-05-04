# 📄 Spécifications Fonctionnelles Détaillées - TrOps

## 1. Contexte et Vision Globale
TrOps est une application web SaaS (Software as a Service) destinée à la gestion de flotte et aux opérations de transport, spécifiquement conçue pour le marché marocain[cite: 8]. La cible principale est constituée de PME possédant une flotte de 5 à 20 véhicules[cite: 8]. L'objectif principal de l'application est de remplacer la gestion manuelle (Excel, WhatsApp) par un outil centralisé offrant une visibilité financière claire, un suivi des missions et une automatisation des alertes (maintenance, documents)[cite: 8].

L'application repose sur un principe fondamental : **une simplicité d'utilisation extrême** pour garantir l'adoption par des utilisateurs non techniques, tout en mettant en évidence le retour sur investissement (ROI)[cite: 8]. TrOps se distingue par une approche "Insight-Driven", agissant comme un véritable assistant décisionnel.

## 2. Architecture et Principes Transverses
* **Modèle Multi-tenant (Multi-clients) :** L'architecture garantit une isolation stricte des données entre les différentes entreprises clientes[cite: 8]. Cela est géré au niveau de la base de données via une approche "Row-Level Isolation" utilisant une colonne `company_id` commune à toutes les entités[cite: 8].

* **Stack Technique :** Backend en Java 21 avec Spring Boot, Frontend en React, base de données PostgreSQL, et authentification sécurisée via JWT[cite: 8].
* **Clean Architecture & Design Patterns :** La logique métier complexe n'est pas couplée aux entités JPA. Utilisation du **Strategy Pattern** pour les moteurs de calcul (ex: rentabilité), permettant d'interchanger les règles métier dynamiquement sans altérer la couche de données.

* **Optimisation UX Backend :** Toutes les listes de l'application intègrent nativement la pagination, le tri (par date, profit, client) et le filtrage multicritères.

## 3. Rôles et Utilisateurs (F1 & F10)
Le système gère trois principaux niveaux d'accès (Rôles)[cite: 8]:
* **Administrateur :** Accès total à la configuration de la société, gestion des abonnements et création des utilisateurs.
* **Manager :** Accès à la gestion opérationnelle (missions, véhicules, clients) et au tableau de bord financier.
* **Opérateur :** Accès limité (par exemple, uniquement la consultation des missions ou la saisie de base).

**Fonctionnalités associées :**
* Connexion sécurisée via email et mot de passe (génération d'un token JWT)[cite: 8].
* Création, modification et suppression d'utilisateurs[cite: 8].
* Attribution et modification des rôles utilisateurs[cite: 8].

## 4. Spécifications des Modules Métiers (Features)

### 4.1. Gestion des Missions (F2) - *Cœur du système*
### 4.1. Gestion des Missions (F2) - *Cœur du système*
La mission est l'entité centrale qui relie un client, un véhicule et des flux financiers.
* **Cycle de vie (Statut) :** `PLANNED`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`.
* **Suivi Financier :** Saisir les revenus générés et les coûts associés[cite: 8].
* **Moteur de Calcul (Strategy Pattern) :** Le calcul des indicateurs financiers est délégué à un service externe injecté. Avant chaque sauvegarde, ce moteur évalue :
  - Le Profit net (`Revenus - Coûts`)[cite: 8].
  - La Marge bénéficiaire (`Profit / Revenus`).
  - Le **Score de Rentabilité** (`PROFITABLE`, `MEDIUM`, `LOSS`) basé sur des seuils de tolérance paramétrables.

### 4.2. Gestion de la Flotte (Véhicules) (F3)
* **Inventaire :** Ajouter, modifier et consulter la liste complète des véhicules de l'entreprise[cite: 8].
* **Historique et Finance :** Consulter l'historique des missions d'un véhicule et obtenir un résumé financier dédié (Revenus totaux, Coûts totaux, Profit net du véhicule)[cite: 8].
* **Disponibilité :** Si un véhicule est déclaré "en maintenance", le système doit le marquer comme indisponible pour de nouvelles missions[cite: 8].

### 4.3. Gestion des Clients (F4)
* **Répertoire :** Ajouter et modifier des fiches clients[cite: 8].
* **Liaison :** Associer les clients aux missions correspondantes pour suivre le volume d'affaires par client[cite: 8].

### 4.4. Module Analytique (Intelligence Artificielle d'Affaires)
Nouveau module clé pour fournir des "Insights" à l'utilisateur :
* Identification des meilleurs clients (`topClients`).
* Identification des véhicules les moins rentables (`worstVehicles`).
* Détection automatique des missions à perte (`missionsAtLoss`).
* Analyse des tendances mensuelles.

### 4.5. Système d'Alertes Intelligentes (F7 & F8)
Ce module vise à éliminer les oublis critiques et à protéger la marge financière.
* **Génération d'Alertes :** Création automatique d'alertes avec un niveau de sévérité (`LOW`, `MEDIUM`, `HIGH`).
* **Types d'Alertes :** `HIGH_COST_VEHICLE` (Véhicule anormalement coûteux), `LOW_PROFIT_CLIENT` (Client peu rentable), `LOSS_MISSION` (Mission effectuée à perte), `UPCOMING_MAINTENANCE` (Entretien imminent) et expiration de documents[cite: 8].

### 4.6. Tableau de Bord Décisionnel / Dashboard (F9)
Le dashboard est l'outil principal de valorisation du ROI pour le client. Il doit afficher de manière fluide et visuelle[cite: 8]:
* Le Chiffre d'Affaires global, le total des coûts, et le profit net généré[cite: 8].
* Les recommandations poussées par le module Analytique (Insights).
* Les alertes actives du système.

## 5. Exigences Non Fonctionnelles (Critères d'Acceptation)
* **Performances :** L'application, et particulièrement le dashboard, doit être fluide avec un temps de réponse inférieur à 2 secondes[cite: 8].
* **Ergonomie (UX) :** L'interface doit proposer une navigation intuitive et un "onboarding" très rapide pour contrer la résistance au changement des utilisateurs habitués à Excel[cite: 8].
* **Fiabilité et Sécurité :** Sauvegarde régulière des données, cohérence des informations et gestion stricte des accès par entreprise[cite: 8].