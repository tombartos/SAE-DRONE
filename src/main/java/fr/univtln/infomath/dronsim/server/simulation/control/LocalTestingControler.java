package fr.univtln.infomath.dronsim.server.simulation.control;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.network.Client;
import com.jme3.network.Message;
import fr.univtln.infomath.dronsim.server.simulation.jme_messages.DroneMovementRequestMessage;
import java.util.ArrayList;
import java.util.List;

// ATTENTION : cette classe est une version de test, elle ne respecte pas l'interface Controler
// elle ne respecte pas la phylosophie de l'interface controler
public class LocalTestingControler implements ActionListener {

    private static final String FORWARD = "FORWARD", BACKWARD = "BACKWARD", LEFT = "LEFT", RIGHT = "RIGHT";
    private static final String ASCEND = "ASCEND", DESCEND = "DESCEND";
    private boolean forward, backward, left, right;
    private boolean ascend, descend;
    private Client client;
    private int droneId;

    public LocalTestingControler(InputManager inputManager, Client client, int droneId) {
        this.client = client;
        this.droneId = droneId;

        inputManager.addMapping(FORWARD, new KeyTrigger(KeyInput.KEY_UP), new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping(
                BACKWARD, new KeyTrigger(KeyInput.KEY_DOWN),
                new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping(LEFT, new KeyTrigger(KeyInput.KEY_LEFT), new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping(
                RIGHT, new KeyTrigger(KeyInput.KEY_RIGHT),
                new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping(ASCEND, new KeyTrigger(KeyInput.KEY_E));
        inputManager.addMapping(DESCEND, new KeyTrigger(KeyInput.KEY_Q));
        inputManager.addListener(this, FORWARD, BACKWARD, LEFT, RIGHT, ASCEND, DESCEND);
    }

    public void update(float tpf) {
        // Test version, a changer pour la version finale respectant l'interface
        // generale
        List<String> directions = new ArrayList<>();
        if (forward) {
            directions.add("FORWARD");
        }
        if (backward) {
            directions.add("BACKWARD");
        }
        if (left) {
            directions.add("LEFT");
        }
        if (right) {
            directions.add("RIGHT");
        }
        if (ascend) {
            directions.add("ASCEND");
        }
        if (descend) {
            directions.add("DESCEND");
        }
        if (!directions.isEmpty()) {
            List<Integer> motorsSpeeds = new ArrayList<>();
            motorsSpeeds.add(1000);
            Message message = new DroneMovementRequestMessage(droneId, directions, motorsSpeeds);
            client.send(message);
        }
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {

        switch (name) {
            case FORWARD -> forward = isPressed;
            case BACKWARD -> backward = isPressed;
            case LEFT -> left = isPressed;
            case RIGHT -> right = isPressed;
            case ASCEND -> ascend = isPressed;
            case DESCEND -> descend = isPressed;
        }
    }

}
