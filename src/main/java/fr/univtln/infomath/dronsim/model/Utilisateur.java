package fr.univtln.infomath.dronsim.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;
import java.util.List;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;

@Inheritance(strategy = InheritanceType.JOINED)
@Entity
@Table(name = "Utilisateurs")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@Getter
@ToString

/**
 * Abstract class representing a user in the system.
 * A user can have personal notes and is identified by their login.
 */
public abstract class Utilisateur {
    @Id
    @SequenceGenerator(name = "user_seq", sequenceName = "user_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    private Long id;
    @Column(nullable = false)
    private String nom;
    @Column(nullable = false)
    private String prenom;
    @Column(nullable = false)
    private String login; // Username

}
