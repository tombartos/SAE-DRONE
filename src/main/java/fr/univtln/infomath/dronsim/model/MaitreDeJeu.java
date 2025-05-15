package fr.univtln.infomath.dronsim.model;

import jakarta.persistence.Entity;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import lombok.Builder;
import lombok.Setter;

@Entity
@Table(name = "MaitresDeJeu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@NamedQueries({
        @NamedQuery(name = "MaitreDeJeu.getByLogin", query = "SELECT m FROM MaitreDeJeu m WHERE m.login = :login")
})
public class MaitreDeJeu extends Utilisateur {

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @Builder.Default
    private List<Simulator> simulators = new ArrayList<>();

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @Builder.Default
    private List<Drone> drones = new ArrayList<>();

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @Builder.Default
    private List<Pilot> pilots = new ArrayList<>();
}
