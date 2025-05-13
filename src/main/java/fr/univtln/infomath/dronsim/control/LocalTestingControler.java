package fr.univtln.infomath.dronsim.control;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.renderer.Camera;

import fr.univtln.infomath.dronsim.simulation.jmeMessages.DroneMovementRequestMessage;

import java.util.ArrayList;
import java.util.List;

import com.jme3.bullet.control.RigidBodyControl;

public class LocalTestingControler implements ActionListener, GeneralControlerInterface {

    private static final String FORWARD = "FORWARD", BACKWARD = "BACKWARD", LEFT = "LEFT", RIGHT = "RIGHT";
    private static final String ASCEND = "ASCEND", DESCEND = "DESCEND";
    private boolean forward, backward, left, right;
    private boolean ascend, descend;
    private RigidBodyControl control;
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
            Message message = new DroneMovementRequestMessage(droneId, directions);
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

    public RigidBodyControl getControl() {
        return control;
    }
}
