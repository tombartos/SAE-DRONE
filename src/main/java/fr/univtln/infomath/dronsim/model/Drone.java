package fr.univtln.infomath.dronsim.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
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
@Table(name = "Drone")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(exclude = "id")
@NamedQueries({
        @NamedQuery(name = "Drone.getDronesByPilot", query = "SELECT d FROM Drone d WHERE d.pilot = :pilot"),
        @NamedQuery(name = "Drone.getDroneById", query = "SELECT d FROM Drone d WHERE d.id = :id"),
})

public class Drone {

    @Id
    @SequenceGenerator(name = "drone_seq", sequenceName = "drone_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "drone_seq")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String modelPath;

    @Column(nullable = false)
    private float speed;

    @Column(nullable = false)
    private float mass;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private Pilot pilot;

}
