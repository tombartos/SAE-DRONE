package fr.univtln.infomath.dronsim.model;

import jakarta.persistence.Entity;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.FetchType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "Pilots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@NamedQueries({
        @NamedQuery(name = "Pilot.getByLogin", query = "SELECT p FROM Pilot p WHERE p.login = :login")
})
public class Pilot extends Utilisateur {

    @OneToOne(mappedBy = "pilot", fetch = FetchType.LAZY, optional = true)
    private Drone drone;

    // Tu pourras ajouter d'autres propriétés spécifiques ici
}
