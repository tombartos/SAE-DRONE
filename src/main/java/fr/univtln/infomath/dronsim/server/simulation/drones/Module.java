package fr.univtln.infomath.dronsim.server.simulation.drones;

import lombok.Getter;

/**
 * Represents a module in the drone simulation. A module can be a motor, a
 * battery, or any other component
 * that can be attached to a drone. This class serves as a base class for all
 * modules. This is not used in the current version of the simulation, but may
 * be used in future.
 *
 * @author Tom BARTIER
 */
@Getter
public abstract class Module {
    private int id;
    private String name;
    private int weight;

}
