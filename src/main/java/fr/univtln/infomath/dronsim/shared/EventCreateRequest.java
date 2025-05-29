package fr.univtln.infomath.dronsim.shared;

import com.jme3.math.Vector3f;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) representing a request to create an event in the
 * simulation. This class is used to transfer event creation details from the
 * client to the server.
 *
 * @author Tom BARTIER
 * @version 1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventCreateRequest {
    private String type;
    private Vector3f center;
    private Vector3f size;
    private float speed;
    private Vector3f direction;
}
