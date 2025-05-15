package fr.univtln.infomath.dronsim.model;

import jakarta.persistence.Entity;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "Observateurs")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@NamedQueries({
        @NamedQuery(name = "Observateur.getByLogin", query = "SELECT o FROM Observateur o WHERE o.login = :login")
})
public class Observateur extends Utilisateur {

    // Ajoute ici des propriétés spécifiques si besoin
}
