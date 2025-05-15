package fr.univtln.infomath.dronsim.simulation.Drones;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Captor extends Module {
    private List<Float> values;
}
