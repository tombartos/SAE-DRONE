package fr.univtln.infomath.dronsim.simulation.jmeMessages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import com.jme3.math.Vector3f;

@Serializable
@AllArgsConstructor
@NoArgsConstructor
// TODO: a modifer pour respecter le diagramme de classes
public class DroneInitMessage extends AbstractMessage {
    private int id;
    private String modelPath;
    private String name;
    private Vector3f position;
    private float mass;
}
