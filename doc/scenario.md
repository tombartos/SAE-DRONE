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

