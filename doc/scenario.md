# Scénarios
## UC 1 : Recevoir flux vidéo
### Prérequis
-Le pilote est connecté au simulateur et la simulation est lancée
### US 1 : Scénario nominal
1. Le simulateur envoie un flux vidéo au pilote
2. Le pilote reçoit et voit le flux vidéo en temps réel

## UC 2 : Consulter les capteurs:
### Prérequis
-Le pilote est connecté au simulateur et la simulation est lancée
### US 1 : Scénario nominal
1. Le pilote clique sur le bouton "Consulter les capteurs"
2. Le pilote voit la liste des capteurs avec leurs valeurs en temps réel

### US 2 : Scénario alternatif : Capteur non disponible
A.2 Le pilote voit la liste des capteurs avec leurs valeurs en temps réel sauf celle du capteur non disponible

## UC 3 : Suivre un drone
### Prérequis
-L'observateur est connecté au simulateur et la simulation est lancée
### US 1 : Scénario nominal
1. L'observateur clique sur le bouton "Liste des drones connectés"
2. L'application affiche la liste des drones connectés à la simulation
3. L'observateur clique sur le bouton "Suivre" du drone qu'il souhaite suivre
4. L'observateur voit le flux vidéo du drone qu'il suit

### US 2 : Scénario Exception : Aucun drone connecté
A.1 L'observateur clique sur le bouton "Liste des drones connectés"
A.2 L'application affiche un message d'erreur indiquant qu'aucun drone n'est connecté à la simulation

## UC 4 : Changer mode première/troisième personne
### Prérequis
-L'observateur est connecté au simulateur, la simulation est lancée et l'observateur suit un drone

### US 1 : Scénario nominal
1. L'observateur clique sur le bouton "Changer mode première/troisième personne"
2. L'application change le mode de vue de première personne à troisième personne

### US 2 : Scénario alternatif
A.2 L'application change le mode de vue de troisième personne à première personne

## UC 5 : Voir environement
### Prérequis
-L'observateur est connecté au simulateur et la simulation est lancée

### US 1 : Scénario nominal
1. L'observateur clique sur le bouton "Voir environnement"
2. L'application affiche l'environnement de la simulation en temps réel

### US 2 : Scénario alternatif
3.A L'observateur bouge sa souris pour faire pivoter la caméra
4.A L'application affiche l'environnement de la simulation en temps réel avec la nouvelle vue

## UC 6 : Déplacer la caméra
### Prérequis
-L'observateur est connecté au simulateur, la simulation est lancée et l'obsevateur voit l'environnement

### US 1 : Scénario nominal
1. L'observateur appuie sur les touches de déplacement pour déplacer la caméra
2. L'application affiche l'environnement de la simulation en temps réel avec la nouvelle vue

## UC 7 : Ajouter un pilote
### Prérequis
-Le maitre de jeu est connecté au simulateur

### US 1 : Scénario nominal
1. Le maitre de jeu clique sur le bouton "Ajouter un pilote"
2. L'application demande au maitre de jeu de choisir le pilote
3. Le maitre de jeu choisit le pilote
4. L'application demande au maitre de jeu de choisir le mode de connexion
5. Le maitre de jeu choisit le mode de connexion "local"
6. L'application demande au maitre de jeu de choisir le drone attribué au pilote
7. Le maitre de jeu choisit le drone
8. L'application affiche un message de confirmation indiquant que le pilote a été ajouté avec succès

### US 2 : Scénario alternatif
A.5 Le maitre de jeu choisit le mode de connexion "cloud"
Le scénario continue en 6.

## UC 8 : Changer la carte
### Prérequis
-Le maitre de jeu est connecté au simulateur et la simulation est lancée

### US 1 : Scénario nominal
1. Le maitre de jeu clique sur le bouton "Changer la carte"
2. L'application demande au maitre de jeu de choisir la nouvelle carte
3. Le maitre de jeu choisit la nouvelle carte
4. L'application redémarre la simulation avec la nouvelle carte

## UC 9 : Gérer les utilisateurs
### Prérequis
-L'administrateur est connecté au simulateur

### US 1 : Scénario nominal
1. L'administrateur clique sur le bouton "Gérer les utilisateurs"
2. L'application affiche la liste des utilisateurs
3. L'administrateur clique sur le bouton "Ajouter un utilisateur"
4. L'application demande à l'administrateur de choisir le type d'utilisateur
5. L'administrateur choisit le type d'utilisateur
6. L'application demande à l'administrateur de choisir le nom d'utilisateur
7. L'administrateur choisit le nom d'utilisateur
8. L'application affiche un message de confirmation indiquant que l'utilisateur a été ajouté avec succès

### US 2 : Scénario alternatif
A.3 L'administrateur clique sur le bouton "Supprimer un utilisateur"
A.4 L'application demande à l'administrateur de choisir l'utilisateur à supprimer
A.5 L'administrateur choisit l'utilisateur à supprimer
A.6 L'application affiche un message de confirmation indiquant que l'utilisateur a été supprimé avec succès

### US 3 : Scénario alternatif
Prérequis : la simulation est lancée

A.4 L'administrateur choisit le nom d'utilisateur\
A.5 L'application affiche les informations de l'utilisateur\
A.6 L'administrateur clique sur le bouton "Exclure l'utilisateur"\
A.7 L'application affiche un message de confirmation indiquant que l'utilisateur a été exclu avec succès

## UC 10 : Gérer les condition météo
### Prérequis
Le maitre de jeu accède à l’interface de contrôle de simulation et la simulation est lancée.

### US 1 : Scénario nominal
1. Le maitre de jeu clique sur le bouton "Conditions météo"
2. Le système affiche les paramètres modifiables : vent, pluie, luminosité,etc.
3. Le maitre de jeu ajuste les valeurs via des curseurs ou des champs numériques.
4. Le maitre de jeu valide la configuration.
5. Le simulateur applique dynamiquement les nouvelles conditions.
6. Les drones actifs sont immédiatement soumis à ces contraintes physiques.

## UC 11 : Gérer les évènements
### Prérequis
Le maitre de jeu accède à l’interface de contrôle de simulation et la simulation est lancée.

### US 1 : Scénario nominal
1. Le maitre de jeu clique sur le bouton "consulter événements"
2. Le système affiche les événements disponibles : obstacle dynamique(comme un poisson, un bateau ou un filet), courant localisé imprévu, perturbation de signal, turbidité de l’eau.
3. Le maitre de jeu valide et déclenche l’événement.
4. Le simulateur applique immédiatement l’événement dans l’environnement 3D.
5. Le drone réagit selon ses algorithmes embarqués (autonomie, retour au point sûr, etc.).

## UC 12 : Lancer une simulation
### - Prérequies
- Le MDJ doit être connecté au simulateur
- Le MDJ doit avoir charger une carte dans le simulateur

### US 1 : Scénario nominal
1. Le MDJ clique sur le bouton démarer la simulation
2. Le Système affiche une fenêtre avec tous les éléments de la simulations (pilote avec drone attitré, carte, liste d'évènement,...)
3. Le Système demande d'appuyer sur le bouton "démarer la simulation"
4. Le MDJ appuie sur le bouton "démarer la simulation"
5. Le système ferme la fenêtre et lance la simulation

### US 2 : Scénario exception : Le MDJ ne lance pas la simulation
E2. Le MDJ appuye sur le bouton "Annuler"
E3. Le système ferme la fenêtre

## UC 13 : Piloter Manuellemnt
### Prérequies
- Le pilote doit possèder un controleur configuré
- Le pilote doit être dans une simulation avec un drone attitré

### US 1 : Scénario nominal
1. Le Pilote transmet des directives au drone via son controleur
2. Le Système simule le drone en fonction des directives reçues
3. Retour à l'étape 1

### US 2 : Scénario alternatif : Le controler utilisé n'est pas reconnu
A2. Le Système indique au Pilote que l'action n'est pas reconnue\
A3. Retour à l'étape 1
