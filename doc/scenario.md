## UC ?? : Lancer une simulation
### - Prérequies
- Le MDJ doit être connecté au simulateur
- Le MDJ doit avoir charger une carte dans le simulateur

### Scénario nominal :
1. Le MDJ clique sur le bouton démarer la simulation
2. Le Système affiche une fenêtre avec tous les éléments de la simulations (pilote avec drone attitré, carte, liste d'évènement,...)
3. Le Système demande d'appuyer sur le bouton "démarer la simulation" 
4. Le MDJ appuie sur le bouton "démarer la simulation"
5. Le système ferme la fenêtre et lance la simulation

### Scénario exception : Le MDJ ne lance pas la simulation
E2. Le MDJ appuye sur le bouton "Annuler"
E3. Le système ferme la fenêtre


## UC ?? : Piloter Manuellemnt
### Prérequies
- Le pilote doit possèder un controleur configurer
- Le pilote doit être dans une simulation avec un drone attitré

### Scénario nominal
1. Le Pilote transmet des directives au drone via son controleur
2. Le Système simule le drone en fonction des directives reçues
3. Retour à l'étape 1

### Scénario alternatif : Le controle utiliser n'est pas reconnu
A2. Le Système indique au Pilote que l'action n'est pas reconnue
A3. Retour à l'étape 1


## UC ?? : Se déplacer librement
### Prérequies
- L'Observateur doit possèder un controleur configurer
- L'Observateur doit être dans une simulation

### Scénario nominal
1. L'Observateur transmet des directives de déplacement à ça caméra avec son controleur
2. Le Système déplace la caméra selon les directives reçues
3. Retour à l'étape 1

### Scénario alternatif : Le controle utiliser n'est pas reconnu
A2. Le Système indique à l'Observateur que l'action n'est pas reconnue
A3. Retour à l'étape 1