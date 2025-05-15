package fr.univtln.infomath.dronsim.model;

import java.util.List;
import java.util.ArrayList;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.FetchType;
import lombok.AllArgsConstructor;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "Simulator")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(exclude = "id")
@NamedQueries({
        @NamedQuery(name = "Simulator.getSimulatorsByPilot", query = "SELECT s FROM Simulator s WHERE s.pilot = :pilot"),
        @NamedQuery(name = "Simulator.getSimulatorById", query = "SELECT s FROM Simulator s WHERE s.id = :id"),
})
public class Simulator {

    @Id
    @SequenceGenerator(name = "simulator_seq", sequenceName = "simulator_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "simulator_seq")
    private Long id;

    @ManyToMany(mappedBy = "simulators", cascade = { CascadeType.MERGE }, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Pilot> pilots = new ArrayList<>();

    @ManyToMany(mappedBy = "simulators", cascade = { CascadeType.MERGE }, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Drone> drones = new ArrayList<>();

}
