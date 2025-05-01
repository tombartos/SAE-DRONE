# Scénarios
## UC ?? : Recevoir flux vidéo
### Prérequis
-Le pilote est connecté au simulateur et la simulation est lancée
### Scénario nominal
1. Le simulateur envoie un flux vidéo au pilote
2. Le pilote reçoit et voit le flux vidéo en temps réel


## UC ?? : Consulter les capteurs:
### Prérequis
-Le pilote est connecté au simulateur et la simulation est lancée
### Scénario nominal
1. Le pilote clique sur le bouton "Consulter les capteurs"
2. Le pilote voit la liste des capteurs avec leurs valeurs en temps réel

### Scénario alternatif : Capteur non disponible
A.2 Le pilote voit la liste des capteurs avec leurs valeurs en temps réel sauf celle du capteur non disponible

## UC ?? : Suivre un drone
### Prérequis
-L'observateur est connecté au simulateur et la simulation est lancée
### Scénario nominal
1. L'observateur clique sur le bouton "Liste des drones connectés"
2. L'application affiche la liste des drones connectés à la simulation
3. L'observateur clique sur le bouton "Suivre" du drone qu'il souhaite suivre
4. L'observateur voit le flux vidéo du drone qu'il suit


### Scénario Exception : Aucun drone connecté
A.1 L'observateur clique sur le bouton "Liste des drones connectés"
A.2 L'application affiche un message d'erreur indiquant qu'aucun drone n'est connecté à la simulation

## UC ?? : Changer mode première/troisième personne
### Prérequis
-L'observateur est connecté au simulateur, la simulation est lancée et l'observateur suit un drone

### Scénario nominal
1. L'observateur clique sur le bouton "Changer mode première/troisième personne"
2. L'application change le mode de vue de première personne à troisième personne

### Scénario alternatif
A.2 L'application change le mode de vue de troisième personne à première personne

### UC ?? : Voir environement
### Prérequis
-L'observateur est connecté au simulateur et la simulation est lancée

### Scénario nominal
1. L'observateur clique sur le bouton "Voir environnement"
2. L'application affiche l'environnement de la simulation en temps réel

### Scénario alternatif
3.A L'observateur bouge sa souris pour faire picoter la caméra
4.A L'application affiche l'environnement de la simulation en temps réel avec la nouvelle vue

## UC ?? : Déplacer la caméra
### Prérequis
-L'observateur est connecté au simulateur, la simulation est lancée et l'obsevateur voit l'environnement

### Scénario nominal
3.A L'observateur appuie sur les touches de déplacement pour déplacer la caméra
4.A L'application affiche l'environnement de la simulation en temps réel avec la nouvelle vue


