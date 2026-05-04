Voici une proposition détaillée pour le fichier rules.md. Ce fichier servira de "System Prompt" ou de guide de référence strict pour garantir que tout LLM (ou développeur) qui travaillera sur la base de code de TrOps génère un code Java propre, sécurisé et parfaitement aligné avec ton architecture.

📝 rules.md - Directives de Développement Backend (TrOps)
Ce document définit les standards architecturaux et les bonnes pratiques de développement Java / Spring Boot pour le projet TrOps. Tout code généré pour ce projet doit respecter strictement ces règles.

1. Architecture et Structure (Package by Feature)

Organisation : Le code est organisé par domaine métier (ex: mission, vehicle, expense), et non par couche technique.


Structure Interne : Chaque module métier doit contenir ses propres sous-dossiers : controller, service, repository, model, dto, exception.

Indépendance : Un domaine ne doit pas accéder directement à la base de données d'un autre domaine (ex: MissionService ne doit pas appeler VehicleRepository, il doit appeler VehicleService).

2. Le Paradigme Multi-Tenant (Isolation des données)

Règle d'or : L'isolation des données entre les différentes entreprises est absolue. Une entreprise ne doit jamais pouvoir accéder aux données d'une autre entreprise.


BaseEntity : Toute entité métier doit étendre une classe BaseEntity (ou inclure une colonne company_id).

Filtrage : Ne faites jamais confiance au client pour fournir le company_id. Ce paramètre doit être systématiquement extrait du token JWT de l'utilisateur connecté de manière sécurisée (côté Backend).


Requêtes Sécurisées : Chaque requête de lecture, modification ou suppression doit inclure implicitement ou explicitement une clause WHERE company_id = ?.

3. Standards Java 21
Records : Utilisez exclusivement les record (introduits dans les versions récentes de Java) pour tous les DTOs (Data Transfer Objects). Ne créez pas de classes classiques avec Lombok pour les objets immuables.

Variables locales : Utilisez var lorsque le type de retour est évident (ex: var mission = new Mission();), mais gardez un typage explicite si cela nuit à la lisibilité.

Switch Expressions : Utilisez la syntaxe moderne des switch (avec les flèches ->) pour la propreté du code et pour éviter les erreurs de type "fall-through".

4. Spring Boot & Clean Code
Injection de Dépendances : N'utilisez jamais l'annotation @Autowired sur les champs. Utilisez l'injection par constructeur. L'utilisation de l'annotation @RequiredArgsConstructor de Lombok sur une classe avec des champs private final est le standard exigé.


Contrôleurs (Controllers) : Les contrôleurs doivent être aussi "fins" que possible. Ils ne doivent contenir aucune logique métier. Leur seul rôle est de valider les requêtes entrantes, d'appeler le Service approprié, et de retourner une réponse HTTP formatée.


Services : Toute la logique métier (calculs de profit, vérification de disponibilité, règles de gestion) réside uniquement dans la couche Service.

Retour rapide (Early Return) : Évitez les blocs if/else imbriqués. Privilégiez les retours rapides ou les levées d'exceptions immédiates en début de méthode pour valider les conditions (Guard clauses).

5. Manipulation des Données (Entités vs DTOs)

Séparation stricte : Les entités (annotées avec @Entity) ne doivent jamais être retournées au Frontend via un Contrôleur.


Utilisation des DTOs : Utilisez toujours des DTOs (Data Transfer Objects) pour les requêtes entrantes (Requests) et sortantes (Responses) afin de maîtriser exactement les données exposées et d'éviter les boucles infinies de sérialisation JSON.

Mappers : Utilisez un outil de mapping (comme MapStruct) ou des méthodes statiques explicites dans les DTOs pour convertir les Entités en DTOs et inversement.

6. Gestion des Erreurs et Exceptions
Exceptions Personnalisées : Créez des exceptions métier spécifiques (ex: MissionNotFoundException, VehicleUnavailableException) plutôt que de lever des RuntimeException génériques.

Gestion Globale : Utilisez un @RestControllerAdvice pour intercepter toutes les exceptions et formater une réponse d'erreur standardisée (JSON contenant le timestamp, le statut HTTP, et le message d'erreur clair).

Statuts HTTP : Utilisez les bons codes HTTP (200 OK, 201 Created, 400 Bad Request, 401 Unauthorized, 403 Forbidden, 404 Not Found, 409 Conflict).

7. Instructions de Comportement pour le LLM
Pas de code générique (Boilerplate) : Inutile de générer les getters, setters, ou constructeurs si Lombok ou les Records peuvent faire le travail.

Nommage : Privilégiez des noms de variables, méthodes et classes en anglais, clairs et descriptifs.

Sécurité : Lors de la génération d'un point d'accès (Endpoint) modifiant la base de données, assurez-vous de toujours valider que la ressource ciblée appartient bien au company_id de l'utilisateur qui fait la requête.