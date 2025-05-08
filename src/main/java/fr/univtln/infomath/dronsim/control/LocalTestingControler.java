package fr.univtln.infomath.dronsim.control;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.bullet.control.RigidBodyControl;
import fr.univtln.infomath.dronsim.simulation.Drone;

public class LocalTestingControler implements ActionListener {

    private static final float PLAYER_ACCEL = 1.0f;
    private static final float PLAYER_FORCE = 1000 * PLAYER_ACCEL; // F = M(4m diameter steel ball) * A ( 1m/s²)

    private static final String FORWARD = "FORWARD", BACKWARD = "BACKWARD", LEFT = "LEFT", RIGHT = "RIGHT";
    private static final String ASCEND = "ASCEND", DESCEND = "DESCEND";
    private boolean forward, backward, left, right;
    private boolean ascend, descend;
    private Drone drone;
    private Camera cam;

    public LocalTestingControler(InputManager inputManager, Drone drone, Camera cam) {
        this.drone = drone;
        this.cam = cam;

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
        Vector3f force = new Vector3f();

        // Directions "plafond"
        Vector3f forwardDir = cam.getDirection().clone();
        forwardDir.setY(0);
        forwardDir.normalizeLocal();

        Vector3f leftDir = cam.getLeft().clone();
        leftDir.setY(0);
        leftDir.normalizeLocal();

        Vector3f verticalDir = Vector3f.UNIT_Y.clone();

        if (forward)
            force.addLocal(forwardDir);
        if (backward)
            force.subtractLocal(forwardDir);
        if (left)
            force.addLocal(leftDir);
        if (right)
            force.subtractLocal(leftDir);
        if (ascend)
            force.addLocal(verticalDir);
        if (descend)
            force.subtractLocal(verticalDir);

        if (!force.equals(Vector3f.ZERO)) {
            force.normalizeLocal().multLocal(PLAYER_FORCE); // intensité constante
            drone.getControl().applyCentralForce(force);
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

    public Drone getDrone() {
        return drone;
    }

    public RigidBodyControl getControl() {
        return drone.getControl();
    }
}
