package fr.univtln.infomath.dronsim.control;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.bullet.control.RigidBodyControl;

public class LocalTestingControler implements ActionListener {

    private static final String FORWARD = "FORWARD", BACKWARD = "BACKWARD", LEFT = "LEFT", RIGHT = "RIGHT";
    private static final String ASCEND = "ASCEND", DESCEND = "DESCEND";
    private boolean forward, backward, left, right;
    private boolean ascend, descend;
    private RigidBodyControl control;
    private Camera cam;

    public LocalTestingControler(InputManager inputManager, RigidBodyControl droneControl, Camera cam) {
        this.control = droneControl;
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

        Vector3f verticalDir = cam.getDirection().clone();
        verticalDir.setX(0);
        verticalDir.setZ(0);
        verticalDir.normalizeLocal(); // pure direction verticale caméra

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
            force.normalizeLocal().multLocal(500); // intensité constante
            control.applyCentralForce(force);
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
