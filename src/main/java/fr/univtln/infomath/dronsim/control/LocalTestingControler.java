package fr.univtln.infomath.dronsim.control;

import java.util.ArrayList;
import java.util.List;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import fr.univtln.infomath.dronsim.Drawer;
import fr.univtln.infomath.dronsim.simulation.Drone;
import lombok.Getter;

@Getter
public class LocalTestingControler implements ActionListener {

    private static final float PLAYER_ACCEL = 1.0f;
    float waterLevel = 2.0f;
    private float cameraYaw = 0;
    private final float maxYaw = FastMath.HALF_PI / 2; // limite de ±45°

    private static final String FORWARD = "FORWARD", BACKWARD = "BACKWARD", LEFT = "LEFT", RIGHT = "RIGHT";
    private static final String ASCEND = "ASCEND", DESCEND = "DESCEND";
    private static final String ROTATE_LEFT = "ROTATE_LEFT", ROTATE_RIGHT = "ROTATE_RIGHT", PITCH_UP = "PITCH_UP",
            PITCH_DOWN = "PITCH_DOWN";

    private static final String CAM_LEFT = "CAM_LEFT", CAM_RIGHT = "CAM_RIGHT";

    private boolean forward, backward, left, right;
    private boolean ascend, descend;
    private boolean rotateLeft, rotateRight, pitchUp, pitchDown;
    private boolean camLeft, camRight;

    private Drone drone;
    private Camera cam;
    private Node rootNode;

    public LocalTestingControler(InputManager inputManager, Drone drone, Camera cam, Node rootNode) {
        this.drone = drone;
        this.cam = cam;
        this.rootNode = rootNode;

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
        inputManager.addMapping(ROTATE_LEFT, new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping(ROTATE_RIGHT, new KeyTrigger(KeyInput.KEY_L));
        inputManager.addMapping(PITCH_UP, new KeyTrigger(KeyInput.KEY_K));
        inputManager.addMapping(PITCH_DOWN, new KeyTrigger(KeyInput.KEY_I));
        inputManager.addMapping(CAM_LEFT, new KeyTrigger(KeyInput.KEY_G));
        inputManager.addMapping(CAM_RIGHT, new KeyTrigger(KeyInput.KEY_H));

        inputManager.addListener(this, FORWARD, BACKWARD, LEFT, RIGHT, ASCEND, DESCEND, ROTATE_LEFT, ROTATE_RIGHT,
                PITCH_UP, PITCH_DOWN, CAM_LEFT, CAM_RIGHT);
    }

    public void update(float tpf) {

        // Vector3f force = new Vector3f();

        // Vector3f forwardDir =
        // drone.getNode().getLocalRotation().mult(Vector3f.UNIT_Z).setY(0).normalizeLocal();
        // Vector3f rightDir =
        // drone.getNode().getLocalRotation().mult(Vector3f.UNIT_X).setY(0).normalizeLocal();
        // Vector3f upDir =
        // drone.getNode().getLocalRotation().mult(Vector3f.UNIT_Y).normalizeLocal();

        // Vector3f angular = drone.getAngular().clone();

        Drawer.deleteAllLines(rootNode);
        // Update the thruster vectors
        // Rotate each initial thruster vector by the drone's local rotation
        List<Vector3f> rotatedThrusterVecs = new ArrayList<>();
        Quaternion rotation = drone.getNode().getLocalRotation();
        for (Vector3f vec : drone.getInitialThrusterVecs()) {
            rotatedThrusterVecs.add(rotation.mult(vec));
        }
        drone.setThrusterVecs(rotatedThrusterVecs);

        // Update the thruster global positions based on the drone's position and
        // rotation
        List<Vector3f> updatedThrusterPositions = new ArrayList<>();
        Quaternion droneRotation = drone.getNode().getLocalRotation();
        Vector3f droneTranslation = drone.getNode().getLocalTranslation();
        for (Vector3f initialPos : drone.getInitialThrusterLocalPosition()) {
            Vector3f rotatedPos = droneRotation.mult(initialPos);
            updatedThrusterPositions.add(rotatedPos.add(droneTranslation));
        }
        drone.setThrusterGlobalPositions(updatedThrusterPositions);

        if (forward) {
            // System.out.println("FORWARD");
            int speed1 = -200;
            int speed2 = 200;
            // Use thruster lists instead of individual fields
            Vector3f force1 = drone.getThrusterVecs().get(0).mult(speed1);
            Vector3f force2 = drone.getThrusterVecs().get(1).mult(speed1);
            Vector3f force3 = drone.getThrusterVecs().get(2).mult(speed2);
            Vector3f force4 = drone.getThrusterVecs().get(3).mult(speed2);

            Vector3f position1 = drone.getThrusterGlobalPositions().get(0);
            System.out.println("position1: " + position1);
            System.out.println("Drone pos: " + drone.getNode().getLocalTranslation());
            Vector3f position2 = drone.getThrusterGlobalPositions().get(1);
            Vector3f position3 = drone.getThrusterGlobalPositions().get(2);
            Vector3f position4 = drone.getThrusterGlobalPositions().get(3);

            drone.getControl().applyForce(force1, position1);
            drone.getControl().applyForce(force2, position2);
            drone.getControl().applyForce(force3, position3);
            drone.getControl().applyForce(force4, position4);

            Drawer.drawLineBetweenPoints(
                    position1,
                    position1.add(force1),
                    rootNode, drone.getAssetManager(), ColorRGBA.Black);
            Drawer.drawLineBetweenPoints(
                    position2,
                    position2.add(force2),
                    rootNode, drone.getAssetManager(), ColorRGBA.White);
            Drawer.drawLineBetweenPoints(
                    position3,
                    position3.add(force3),
                    rootNode, drone.getAssetManager(), ColorRGBA.Red);
            Drawer.drawLineBetweenPoints(
                    position4,
                    position4.add(force4),
                    rootNode, drone.getAssetManager(), ColorRGBA.Blue);
        }
        // if (backward)
        // force.subtractLocal(forwardDir);
        // if (right)
        // force.addLocal(rightDir);
        // if (left)
        // force.subtractLocal(rightDir);

        if (descend) {
            // force.subtractLocal(upDir);
            int speed = 100;
            Vector3f position5 = drone.getThrusterGlobalPositions().get(4);
            Vector3f position6 = drone.getThrusterGlobalPositions().get(5);
            drone.getControl().applyForce(
                    drone.getThrusterVecs().get(4).mult(-speed),
                    position5);
            drone.getControl().applyForce(
                    drone.getThrusterVecs().get(5).mult(speed),
                    position6);

            Drawer.drawLineBetweenPoints(
                    position5,
                    position5.add(drone.getThrusterVecs().get(4).mult(-speed)),
                    rootNode, drone.getAssetManager(), ColorRGBA.Green);
            Drawer.drawLineBetweenPoints(
                    position6,
                    position6.add(drone.getThrusterVecs().get(5).mult(speed)),
                    rootNode, drone.getAssetManager(), ColorRGBA.Yellow);
        }

        if (ascend) {
            float currentY = drone.getNode().getWorldTranslation().y;
            if (currentY < waterLevel) {
                int speed = 1000;
                Vector3f position5 = drone.getThrusterGlobalPositions().get(4);
                Vector3f position6 = drone.getThrusterGlobalPositions().get(5);
                drone.getControl().applyForce(
                        drone.getThrusterVecs().get(4).mult(speed),
                        position5);
                drone.getControl().applyForce(
                        drone.getThrusterVecs().get(5).mult(-speed),
                        position6);

                Drawer.drawLineBetweenPoints(
                        drone.getThrusterGlobalPositions().get(4),
                        drone.getThrusterGlobalPositions().get(4).add(drone.getThrusterVecs().get(4).mult(speed)),
                        rootNode, drone.getAssetManager(), ColorRGBA.Green);
                Drawer.drawLineBetweenPoints(
                        drone.getThrusterGlobalPositions().get(5),
                        drone.getThrusterGlobalPositions().get(5).add(drone.getThrusterVecs().get(5).mult(-speed)),
                        rootNode, drone.getAssetManager(), ColorRGBA.Yellow);
            }

        }

        // if (!force.equals(Vector3f.ZERO)) {
        // float PLAYER_FORCE = drone.getSpeed() * PLAYER_ACCEL; // F = M(4m diameter
        // steel ball) * A ( 1m/s²)
        // force.normalizeLocal().multLocal(PLAYER_FORCE); // intensité constante
        // drone.getControl().applyCentralForce(force);
        // }
        // if (rotateLeft)
        // angular.addLocal(0, 1f, 0); // Yaw +
        // if (rotateRight)
        // angular.addLocal(0, -1f, 0); // Yaw -
        // if (pitchUp)
        // angular.addLocal(1f, 0, 0); // Pitch +
        // if (pitchDown)
        // angular.addLocal(-1f, 0, 0); // Pitch -

        // Stabilisation
        // TODO : Fix physique, essayer avec ça
        Vector3f angular = Vector3f.ZERO;
        drone.getControl().setAngularVelocity(angular);

        float rotationSpeed = 1.5f * tpf;
        if (camLeft) {
            cameraYaw += rotationSpeed;
        } else if (camRight) {
            cameraYaw -= rotationSpeed;
        }
        // Clamp l’angle dans les limites autorisées
        cameraYaw = FastMath.clamp(cameraYaw, -maxYaw, maxYaw);

        // Appliquer la rotation sur le cameraNode du drone
        drone.getCameraNode().setLocalRotation(new Quaternion().fromAngles(0, cameraYaw, 0));

    }

    public void setAction(String name, boolean isPressed) {
        switch (name) {
            case "FORWARD" -> forward = isPressed;
            case "BACKWARD" -> backward = isPressed;
            case "LEFT" -> left = isPressed;
            case "RIGHT" -> right = isPressed;
            case "ASCEND" -> ascend = isPressed;
            case "DESCEND" -> descend = isPressed;
            case "ROTATE_LEFT" -> rotateLeft = isPressed;
            case "ROTATE_RIGHT" -> rotateRight = isPressed;
            case "PITCH_UP" -> pitchUp = isPressed;
            case "PITCH_DOWN" -> pitchDown = isPressed;
            case "CAM_LEFT" -> camLeft = isPressed;
            case "CAM_RIGHT" -> camRight = isPressed;

        }
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        setAction(name, isPressed);
    }

}
