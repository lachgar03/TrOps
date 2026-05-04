
# 📄 Spécifications Fonctionnelles Détaillées - TrOps

## 1. Contexte et Vision Globale
[cite_start]TrOps est une application web SaaS (Software as a Service) destinée à la gestion de flotte et aux opérations de transport, spécifiquement conçue pour le marché marocain[cite: 1, 152]. [cite_start]La cible principale est constituée de PME possédant une flotte de 5 à 20 véhicules[cite: 156]. [cite_start]L'objectif principal de l'application est de remplacer la gestion manuelle (Excel, WhatsApp) par un outil centralisé offrant une visibilité financière claire, un suivi des missions et une automatisation des alertes (maintenance, documents)[cite: 1, 153].

[cite_start]L'application repose sur un principe fondamental : **une simplicité d'utilisation extrême** pour garantir l'adoption par des utilisateurs non techniques, tout en mettant en évidence le retour sur investissement (ROI)[cite: 4, 157, 159].

## 2. Architecture et Principes Transverses
* [cite_start]**Modèle Multi-tenant (Multi-clients) :** L'architecture garantit une isolation stricte des données entre les différentes entreprises clientes[cite: 4, 60]. [cite_start]Cela est géré au niveau de la base de données via une approche "Row-Level Isolation" utilisant une colonne `company_id` commune à toutes les entités[cite: 62].
* [cite_start]**Stack Technique :** Backend en Java 21 avec Spring Boot, Frontend en React, base de données PostgreSQL, et authentification sécurisée via JWT[cite: 11, 41, 46, 49].

## 3. Rôles et Utilisateurs (F1 & F10)
[cite_start]Le système gère trois principaux niveaux d'accès (Rôles)[cite: 3]:
* **Administrateur :** Accès total à la configuration de la société, gestion des abonnements et création des utilisateurs.
* **Manager :** Accès à la gestion opérationnelle (missions, véhicules, clients) et au tableau de bord financier.
* **Opérateur :** Accès limité (par exemple, uniquement la consultation des missions ou la saisie de base).

**Fonctionnalités associées :**
* [cite_start]Connexion sécurisée via email et mot de passe (génération d'un token JWT)[cite: 5, 49].
* [cite_start]Création, modification et suppression d'utilisateurs[cite: 9].
* [cite_start]Attribution et modification des rôles utilisateurs[cite: 9].

## 4. Spécifications des Modules Métiers (Features)

### 4.1. Gestion des Missions (F2) - *Cœur du système*
La mission est l'entité centrale qui relie un client, un véhicule et des flux financiers.
* [cite_start]**Création/Modification :** Permettre de créer, modifier ou annuler une mission[cite: 5].
* [cite_start]**Assignation :** Associer obligatoirement un véhicule et un client à chaque mission[cite: 5, 10].
* [cite_start]**Suivi Financier :** Saisir les revenus générés par la mission et les coûts associés[cite: 6].
* [cite_start]**Calcul Automatique :** Le système doit calculer automatiquement le profit de la mission selon la règle : `Profit = Revenus - Coûts`[cite: 7, 10].

### 4.2. Gestion de la Flotte (Véhicules) (F3)
* [cite_start]**Inventaire :** Ajouter, modifier et consulter la liste complète des véhicules de l'entreprise[cite: 7].
* [cite_start]**Historique :** Consulter l'historique de toutes les missions effectuées par un véhicule spécifique[cite: 7].
* [cite_start]**Disponibilité :** Si un véhicule est déclaré "en maintenance", le système doit le marquer comme indisponible pour de nouvelles missions[cite: 10].

### 4.3. Gestion des Clients (F4)
* [cite_start]**Répertoire :** Ajouter et modifier des fiches clients[cite: 7].
* [cite_start]**Liaison :** Associer les clients aux missions correspondantes pour suivre le volume d'affaires par client[cite: 7].

### 4.4. Suivi des Dépenses (F5)
* [cite_start]**Saisie :** Enregistrer des dépenses courantes[cite: 7].
* [cite_start]**Imputation :** Associer directement une dépense à une mission spécifique ou à un véhicule spécifique[cite: 7].
* [cite_start]**Catégorisation :** Classer les dépenses par catégories (ex: carburant, péage, réparations)[cite: 7].

### 4.5. Maintenance et Entretien (F6)
* [cite_start]**Suivi :** Enregistrer les opérations de maintenance effectuées sur les véhicules[cite: 7].
* [cite_start]**Planification :** Planifier les futures opérations de maintenance[cite: 7].
* [cite_start]**Historique :** Suivre l'historique complet de l'entretien par véhicule[cite: 7].

### 4.6. Gestion Documentaire et Alertes (F7 & F8)
Ce module vise à éliminer les oublis critiques (assurances, visites techniques).
* [cite_start]**Stockage :** Ajouter des documents et les associer à un véhicule spécifique[cite: 7, 8].
* [cite_start]**Validité :** Définir une date d'expiration pour chaque document[cite: 7].
* [cite_start]**Notifications (Alertes) :** Le système génère automatiquement des alertes pour les documents expirés ou dont la date d'expiration approche[cite: 9, 10].
* [cite_start]**Alertes Maintenance :** Génération d'alertes pour les maintenances planifiées approchantes[cite: 9].

### 4.7. Tableau de Bord Décisionnel / Dashboard (F9)
Le dashboard est l'outil principal de valorisation du ROI pour le client. [cite_start]Il doit afficher de manière fluide et visuelle[cite: 9]:
* Le Chiffre d'Affaires global.
* Le total des coûts.
* Le profit net généré.
* Le nombre total de missions effectuées.
* Des graphiques représentatifs de l'activité et de la rentabilité.

## 5. Exigences Non Fonctionnelles (Critères d'Acceptation)
* [cite_start]**Performances :** L'application, et particulièrement le dashboard, doit être fluide avec un temps de réponse inférieur à 2 secondes[cite: 9].
* [cite_start]**Ergonomie (UX) :** L'interface doit proposer une navigation intuitive et un "onboarding" très rapide pour contrer la résistance au changement des utilisateurs habitués à Excel[cite: 9, 32, 159].
* [cite_start]**Fiabilité et Sécurité :** Sauvegarde régulière des données, cohérence des informations et gestion stricte des accès par entreprise[cite: 9, 10].