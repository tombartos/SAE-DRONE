package fr.univtln.infomath.dronsim.server.simulation.drones;

import lombok.Getter;

@Getter
public abstract class Module {
    private int id;
    private String name;
    private int weight;

}
