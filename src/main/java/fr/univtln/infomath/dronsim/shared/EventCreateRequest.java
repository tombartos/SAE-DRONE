package fr.univtln.infomath.dronsim.shared;

import com.jme3.math.Vector3f;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
