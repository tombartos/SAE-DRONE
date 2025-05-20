package fr.univtln.infomath.dronsim.shared;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class Utilisateur {
    private Long id;
    private String nom;
    private String prenom;
    private String login;
    private int role; // 0 = Pilote, 1 = Observateur, 2 = Maitre de jeu, 3 = Administrateur
}
