package fr.univtln.infomath.dronsim.model;

import jakarta.persistence.Entity;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "Administrateurs")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@NamedQueries({
        @NamedQuery(name = "Administrateur.getByLogin", query = "SELECT a FROM Administrateur a WHERE a.login = :login")
})
public class Administrateur extends Utilisateur {

    // Ajoute des attributs si besoin plus tard
}
