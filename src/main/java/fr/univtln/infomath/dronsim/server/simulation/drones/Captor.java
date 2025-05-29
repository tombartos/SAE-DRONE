package fr.univtln.infomath.dronsim.server.simulation.drones;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

/**
 * Represents a Captor module in the drone simulation.
 * <p>
 * This class extends the Module class and contains a list of float values
 * representing the readings from the captor.
 * Not used in the current version of the simulation, but may be used in future.
 * </p>
 *
 * @author Tom BARTIER
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Captor extends Module {
    private List<Float> values;
}
