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

        // Drawer.drawLineBetweenPoints(drone.getPosition(), drone.getPosition().add(Vector3f.UNIT_Y), rootNode, drone.getAssetManager(), ColorRGBA.Green);
        Drawer.drawLineBetweenPoints(
                drone.getNode().getWorldTranslation().add(drone.getNode().getChild("ThrusterNode1").getLocalTranslation()),
                drone.getNode().getWorldTranslation().add(drone.getNode().getChild("ThrusterNode1").getLocalTranslation().add(new Vector3f(0,2,0))),
                rootNode, drone.getAssetManager(), ColorRGBA.Orange);
        Drawer.drawLineBetweenPoints(
            drone.getNode().getLocalTranslation().add(drone.getNode().getLocalRotation().getRotationColumn(2).mult(-2)),
            drone.getNode().getLocalTranslation().add(drone.getNode().getLocalRotation().getRotationColumn(2).mult(2)),
                drone.getNode(), drone.getAssetManager(), ColorRGBA.Orange);
        Drawer.drawLineBetweenPoints(
                drone.getNode().getWorldTranslation().add(drone.getNode().getChild("ThrusterNode2").getLocalTranslation()),
                drone.getNode().getWorldTranslation().add(drone.getNode().getChild("ThrusterNode2").getLocalTranslation().add(new Vector3f(0,2,0))),
                rootNode, drone.getAssetManager(), ColorRGBA.White);
        Drawer.drawLineBetweenPoints(
                drone.getNode().getWorldTranslation().add(drone.getNode().getChild("ThrusterNode3").getLocalTranslation()),
                drone.getNode().getWorldTranslation().add(drone.getNode().getChild("ThrusterNode3").getLocalTranslation().add(new Vector3f(0,2,0))),
                rootNode, drone.getAssetManager(), ColorRGBA.Red);
        Drawer.drawLineBetweenPoints(
                drone.getNode().getWorldTranslation().add(drone.getNode().getChild("ThrusterNode4").getLocalTranslation()),
                drone.getNode().getWorldTranslation().add(drone.getNode().getChild("ThrusterNode4").getLocalTranslation().add(new Vector3f(0,2,0))),
                rootNode, drone.getAssetManager(), ColorRGBA.Green);
        // Drawer.drawLineBetweenPoints(
        //         drone.getNode().getWorldTranslation(),
        //         drone.getNode().getWorldTranslation().add(new Vector3f(0,2,0)),
        //         rootNode, drone.getAssetManager(), ColorRGBA.Magenta);

        // // Drawer.drawLineBetweenPoints(
        //     drone.getNode().getChild("ForwardMarker").getWorldTranslation(),
        //     drone.getNode().getChild("ForwardMarker").getWorldTranslation().add(new Vector3f(0,2,0)),
        //     rootNode, drone.getAssetManager(), ColorRGBA.White);
        // Drawer.drawLineBetweenPoints(
        //     drone.getNode().getChild("BackwardMarker").getWorldTranslation(),
        //     drone.getNode().getChild("BackwardMarker").getWorldTranslation().add(new Vector3f(0,2,0)),
        //     rootNode, drone.getAssetManager(), ColorRGBA.Green);
        // Drawer.drawLineBetweenPoints(
        //         drone.getNode().getChild("ThrusterNode1").getLocalTranslation(),
        //         drone.getNode().getChild("ThrusterNode1").getLocalTranslation().add(new Vector3f(0,2,0)),
        //         rootNode, drone.getAssetManager(), ColorRGBA.Orange);
        // // Drawer.drawLineBetweenPoints(
        // //         drone.getNode().getChild("ThrusterNode1").localToWorld(new Vector3f(-0.f, 0, 0.5f), null),
        // //         drone.getNode().getChild("ThrusterNode1").localToWorld(new Vector3f(0.f, 0, -0.5f), null),
        // //         rootNode, drone.getAssetManager(), ColorRGBA.Pink);
        // Drawer.drawLineBetweenPoints(
        //         drone.getNode().getChild("ThrusterNode2").getWorldTranslation(),
        //         drone.getNode().getChild("ThrusterNode2").getWorldTranslation().add(new Vector3f(0,2,0)),
        //         rootNode, drone.getAssetManager(), ColorRGBA.White);
        // Drawer.drawLineBetweenPoints(
        //         drone.getNode().getChild("ThrusterNode3").getWorldTranslation(),
        //         drone.getNode().getChild("ThrusterNode3").getWorldTranslation().add(new Vector3f(0,2,0)),
        //         rootNode, drone.getAssetManager(), ColorRGBA.Red);
        // Drawer.drawLineBetweenPoints(
        //         drone.getNode().getChild("ThrusterNode4").getWorldTranslation(),
        //         drone.getNode().getChild("ThrusterNode4").getWorldTranslation().add(new Vector3f(0,2,0)),
        //         rootNode, drone.getAssetManager(), ColorRGBA.Green);
        // Drawer.drawLineBetweenPoints(
        //         drone.getNode().getWorldTranslation(),
        //         drone.getNode().getWorldTranslation().add(new Vector3f(0,2,0)),
        //         rootNode, drone.getAssetManager(), ColorRGBA.Magenta);

        // Update the thruster global positions based on the drone's position and
        // rotation
        // List<Vector3f> updatedThrusterPositions = new ArrayList<>();
        // Quaternion droneRotation = drone.getNode().getWorldRotation();
        // // Vector3f droneTranslation = drone.getNode().getLocalTranslation();
        // //TODO: check if the drone is moving
        // Vector3f droneTranslation = drone.getNode().getWorldTranslation();
        // Vector3f[] axe = new Vector3f[3];
        // droneRotation.toAxes(axe);
        // System.out.println(axe[0]);
        // System.out.println(axe[1]);
        // System.out.println(axe[2]);
        // int i = 0;
        // for (Vector3f initialPos : drone.getInitialThrusterLocalPosition()) {
        //     // Vector3f rotatedPos = droneRotation.mult(initialPos);
        //     Vector3f rotatedPos = axe[2].mult(initialPos);
        //     //updatedThrusterPositions.add(rotatedPos.add(droneTranslation));
        //     //drone.getThrusterGlobalPositions().get(i).set(rotatedPos).add(droneTranslation);
        //     //System.out.println("Thrusterss " + i + " : " + drone.getThrusterGlobalPositions().get(i));

        //     i++;
        // }
        // drone.setThrusterGlobalPositions(updatedThrusterPositions);

        // Update the thruster vectors
        // Rotate each initial thruster vector by the drone's local rotation
        List<Vector3f> rotatedThrusterVecs = new ArrayList<>();
        //Quaternion rotation = drone.getNode().getLocalRotation();
        Quaternion rotation = drone.getNode().getWorldRotation();
        Vector3f[] rotationAxe = new Vector3f[3];
        rotation.toAxes(rotationAxe);
        //System.out.println("Rotation Axe N: " + rotation.mult(Vector3f.UNIT_XYZ));
        // System.out.println("Rotation Axe N: " + rotation.getRotationColumn(2));
        // System.out.println("Rotation Axe 0: " + rotationAxe[0]);
        // System.out.println("Rotation Axe 1: " + rotationAxe[1]);
        // System.out.println("Rotation Axe 2: " + rotationAxe[2]);
        int i=0;
        for (Vector3f vec : drone.getInitialThrusterVecs()) {
            //rotatedThrusterVecs.add(vec.mult(rotationAxe[2]));
            //drone.getThrusterVecs().set(i,rotationAxe[2].mult(drone.getInitialThrusterVecs().get(i)).normalize());
            //drone.getThrusterVecs().set(i, drone.getNode().getChild(i+1).getWorldRotation().getRotationColumn(2));
            // System.out.println("Thruster " + i + " : " + drone.getNode().getChild(i+1).getWorldRotation().getRotationColumn(2));
            i++;

        }
     // System.out.println("Direction of thruster 1: " + drone.getNode().getChild("ThrusterNode1").getLocalRotation());
     // System.out.println("Direction of thruster 2: " + drone.getNode().getChild("ThrusterNode2").getLocalRotation());
     // System.out.println("Direction of thruster 3: " + drone.getNode().getChild("ThrusterNode3").getLocalRotation());
     // System.out.println("Direction of thruster 4: " + drone.getNode().getChild("ThrusterNode4").getLocalRotation());
     // System.out.println("Postion Node" + drone.getNode().getWorldTranslation());
     // System.out.println("Position Control" + drone.getControl().getPhysicsLocation());

        //drone.setThrusterVecs(rotatedThrusterVecs);

        Vector3f pos1 = drone.getCameraNode().getWorldTranslation();
        Vector3f pos2 = drone.getControl().getPhysicsLocation();
        System.out.println("Distance between Node and Control: " + pos1.distance(pos2));
        // System.out.println("Position Node: " + pos1);
        // System.out.println("Position ThrusterNode1: " + pos2);

        drone.getControl().clearForces();
        if (forward) {
            //System.out.println("Thruster1: " + drone.getThrusterGlobalPositions());
            // System.out.println("FORWARD");
            int speed1 = -20;
            int speed2 = 20;
            Vector3f forwardDir1 = drone.getNode().getChild("ThrusterNode1").getWorldRotation().getRotationColumn(2);
            Vector3f force1 = forwardDir1.mult(speed1);
            Vector3f position1 = drone.getNode().getChild("ThrusterNode1").getWorldTranslation();
            drone.getControl().applyForce(force1, position1);

            // Repeat for other thrusters
            Vector3f forwardDir2 = drone.getNode().getChild("ThrusterNode2").getWorldRotation().getRotationColumn(2);
            Vector3f force2 = forwardDir2.mult(speed1);
            Vector3f position2 = drone.getNode().getChild("ThrusterNode2").getWorldTranslation();
            drone.getControl().applyForce(force2, position2);

            // ThrusterNode1 forward direction
            Vector3f forwardDir3 = drone.getNode().getChild("ThrusterNode3").getWorldRotation().getRotationColumn(2);
            Vector3f force3 = forwardDir3.mult(speed2);
            Vector3f position3 = drone.getNode().getChild("ThrusterNode3").getWorldTranslation();
            drone.getControl().applyForce(force3, position3);

            // Repeat for other thrusters
            Vector3f forwardDir4 = drone.getNode().getChild("ThrusterNode4").getWorldRotation().getRotationColumn(2);
            Vector3f force4 = forwardDir4.mult(speed2);
            Vector3f position4 = drone.getNode().getChild("ThrusterNode4").getWorldTranslation();
            drone.getControl().applyForce(force4, position4);


            // Use thruster lists instead of individual fields
            // Vector3f force1 = drone.getThrusterVecs().get(0).mult(speed1);
            // Vector3f force2 = drone.getThrusterVecs().get(1).mult(speed1);
            // Vector3f force3 = drone.getThrusterVecs().get(2).mult(speed2);
            // Vector3f force4 = drone.getThrusterVecs().get(3).mult(speed2);
            // Vector3f force1 = drone.getInitialThrusterVecs().get(0).mult(speed1);
            // Vector3f force2 = drone.getInitialThrusterVecs().get(1).mult(speed1);
            // Vector3f force3 = drone.getInitialThrusterVecs().get(2).mult(speed2);
            // Vector3f force4 = drone.getInitialThrusterVecs().get(3).mult(speed2);
            // Vector3f force1 = drone.getNode().getChild("ThrusterNode1").getWorldRotation().getRotationColumn(2).mult(speed1); // getLocalRotation
            // Vector3f force2 = drone.getNode().getChild("ThrusterNode2").getWorldRotation().getRotationColumn(2).mult(speed1); // getWorldRotation
            // Vector3f force3 = drone.getNode().getChild("ThrusterNode3").getWorldRotation().getRotationColumn(2).mult(speed2);
            // Vector3f force4 = drone.getNode().getChild("ThrusterNode4").getWorldRotation().getRotationColumn(2).mult(speed2);
            // Vector3f force1 = drone.getNode().getChild("ThrusterNode1").localToWorld(new Vector3f(0.f, 0, 1f).mult(speed1), null); // getLocalRotation
            // Vector3f force2 = drone.getNode().getChild("ThrusterNode2").localToWorld(new Vector3f(0.f, 0, 1f).mult(speed1), null); // getWorldRotation
            // Vector3f force3 = drone.getNode().getChild("ThrusterNode3").localToWorld(new Vector3f(0.f, 0, 1f).mult(speed2), null);
            // Vector3f force4 = drone.getNode().getChild("ThrusterNode4").localToWorld(new Vector3f(0.f, 0, 1f).mult(speed2), null);
            // Vector3f force1 = drone.getNode().getChild("ThrusterNode1").getWorldRotation().getRotationColumn(2).mult(speed1); // getLocalRotation
            // Vector3f force2 = drone.getNode().getChild("ThrusterNode2").getWorldRotation().getRotationColumn(2).mult(speed1); // getWorldRotation
            // Vector3f force3 = drone.getNode().getChild("ThrusterNode3").getWorldRotation().getRotationColumn(2).mult(speed2);
            // Vector3f force4 = drone.getNode().getChild("ThrusterNode4").getWorldRotation().getRotationColumn(2).mult(speed2);

            // Vector3f force10 = drone.getNode().getChild("ThrusterNode1").getWorldRotation().getRotationColumn(0).mult(speed1); // getLocalRotation
            // Vector3f force20 = drone.getNode().getChild("ThrusterNode2").getWorldRotation().getRotationColumn(0).mult(speed1); // getWorldRotation
            // Vector3f force30 = drone.getNode().getChild("ThrusterNode3").getWorldRotation().getRotationColumn(0).mult(speed2);
            // Vector3f force40 = drone.getNode().getChild("ThrusterNode4").getWorldRotation().getRotationColumn(0).mult(speed2);
            // Vector3f force1 = drone.getThrusterVecs().get(0).mult(speed1);
            // Vector3f force2 = drone.getThrusterVecs().get(1).mult(speed1);
            // Vector3f force3 = drone.getThrusterVecs().get(2).mult(speed2);
            // Vector3f force4 = drone.getThrusterVecs().get(3).mult(speed2);

            // Vector3f position1 = drone.getInitialThrusterLocalPosition().get(0);
            // //System.out.println("position1: " + position1);
            // //System.out.println("Drone pos: " + drone.getNode().getLocalTranslation());
            // // drone.getThrusterVecs().get(3).mult(speed2);
            // Vector3f position2 = drone.getInitialThrusterLocalPosition().get(1);
            // Vector3f position3 = drone.getInitialThrusterLocalPosition().get(2);
            // Vector3f position4 = drone.getInitialThrusterLocalPosition().get(3);

            // Vector3f position1 = drone.getNode().getChild("ThrusterNode1").getLocalTranslation();
            // Vector3f position2 = drone.getNode().getChild("ThrusterNode2").getLocalTranslation();
            // Vector3f position3 = drone.getNode().getChild("ThrusterNode3").getLocalTranslation();
            // Vector3f position4 = drone.getNode().getChild("ThrusterNode4").getLocalTranslation();



            // System.out.println("Force4 : " + drone.getThrusterGlobalPositions().get(3));
            // drone.getControl().applyForce(drone.getNode().getWorldRotation().mult(force1), drone.getInitialThrusterLocalPosition().get(0));
            // drone.getControl().applyForce(drone.getNode().getWorldRotation().mult(force2), drone.getInitialThrusterLocalPosition().get(1));
            // drone.getControl().applyForce(drone.getNode().getWorldRotation().mult(force3), drone.getInitialThrusterLocalPosition().get(2));
            // drone.getControl().applyForce(drone.getNode().getWorldRotation().mult(force4), drone.getInitialThrusterLocalPosition().get(3));


            // drone.getControl().applyForce(drone.getNode().getLocalRotation().mult(drone.getThrusterVecs().get(0).mult(speed1)), drone.getInitialThrusterLocalPosition().get(0));
            // drone.getControl().applyForce(drone.getNode().getLocalRotation().mult(drone.getThrusterVecs().get(1).mult(speed1)), drone.getInitialThrusterLocalPosition().get(1));
            // drone.getControl().applyForce(drone.getNode().getLocalRotation().mult(drone.getThrusterVecs().get(2).mult(speed2)), drone.getInitialThrusterLocalPosition().get(2));
            // drone.getControl().applyForce(drone.getNode().getLocalRotation().mult(drone.getThrusterVecs().get(3).mult(speed2)), drone.getInitialThrusterLocalPosition().get(3));


            // drone.getControl().applyForce(drone.getThrusterVecs().get(0).mult(speed1), drone.getInitialThrusterLocalPosition().get(0));
            // drone.getControl().applyForce(drone.getThrusterVecs().get(1).mult(speed1), drone.getInitialThrusterLocalPosition().get(1));
            // drone.getControl().applyForce(drone.getThrusterVecs().get(2).mult(speed2), drone.getInitialThrusterLocalPosition().get(2));
            // drone.getControl().applyForce(drone.getThrusterVecs().get(3).mult(speed2), drone.getInitialThrusterLocalPosition().get(3));


            // drone.getControl().applyForce(force1, drone.getNode().getWorldRotation().mult(Vector3f.ZERO.add(1, 0, -1)));
            // drone.getControl().applyForce(force2, drone.getNode().getWorldRotation().mult(Vector3f.ZERO.add(-1, 0, -1)));
            // drone.getControl().applyForce(force3, drone.getNode().getWorldRotation().mult(Vector3f.ZERO.add(1, 0, 1)));
            // drone.getControl().applyForce(force4, drone.getNode().getWorldRotation().mult(Vector3f.ZERO.add(-1,0,1)));

            // Vector3f position1 = Vector3f.ZERO.add(-1f, -3, -1f).add(drone.getThrusterGlobalPositions().get(0));
            // Vector3f position2 = Vector3f.ZERO.add(1f, -3, -1f).add(drone.getThrusterGlobalPositions().get(1));
            // Vector3f position3 = Vector3f.ZERO.add(-1f, -3, 1f).add(drone.getThrusterGlobalPositions().get(2));
            // Vector3f position4 = Vector3f.ZERO.add(1f, -3, 1f).add(drone.getThrusterGlobalPositions().get(3));

            // Vector3f force1 = Vector3f.ZERO.add(-1, 0, -1).mult(speed1);
            // Vector3f force2 = Vector3f.ZERO.add(1, 0, -1).mult(speed1);
            // Vector3f force3 = Vector3f.ZERO.add(-1, 0, 1).mult(speed2);
            // Vector3f force4 = Vector3f.ZERO.add(1, 0, 1).mult(speed2);

            //drone.getControl().clearForces();
            // drone.getControl().applyForce(force1, position1);
            // drone.getControl().applyForce(force2, position2);
            // drone.getControl().applyForce(force3, position3);
            // drone.getControl().applyForce(force4, position4);
            // drone.getNode().getChild("ThrusterNode1").lookAt(drone.getNode().getChild("ForwardMarker").getLocalTranslation(),Vector3f.UNIT_Y);
            // drone.getNode().getChild("ThrusterNode2").lookAt(drone.getNode().getChild("ForwardMarker").getLocalTranslation(),Vector3f.UNIT_Y);
            // drone.getNode().getChild("ThrusterNode3").lookAt(drone.getNode().getChild("BackwardMarker").getLocalTranslation(),Vector3f.UNIT_Y);
            // drone.getNode().getChild("ThrusterNode4").lookAt(drone.getNode().getChild("BackwardMarker").getLocalTranslation(),Vector3f.UNIT_Y);
            // drone.getControl().applyForce(drone.getNode().getChild("ThrusterNode1").getWorldRotation().getRotationColumn(2).mult(speed1), position1);
            // drone.getControl().applyForce(drone.getNode().getChild("ThrusterNode2").getWorldRotation().getRotationColumn(2).mult(speed1), position2);
            // drone.getControl().applyForce(drone.getNode().getChild("ThrusterNode3").getWorldRotation().getRotationColumn(2).mult(speed2), position3);
            // drone.getControl().applyForce(drone.getNode().getChild("ThrusterNode4").getWorldRotation().getRotationColumn(2).mult(speed2), position4);
            //drone.getControl().applyForce(drone.getNode().getWorldRotation().mult(drone.getNode().getChild("ThrusterNode2").getLocalRotation()).mult(speed1).getRotationColumn(2), drone.getNode().localToWorld(drone.getNode().getChild("ThrusterNode2").getLocalTranslation(), null));
            //drone.getControl().applyForce(drone.getNode().getWorldRotation().mult(drone.getNode().getChild("ThrusterNode3").getLocalRotation()).mult(speed2).getRotationColumn(2), drone.getNode().localToWorld(drone.getNode().getChild("ThrusterNode3").getLocalTranslation(), null));
            //drone.getControl().applyForce(drone.getNode().getWorldRotation().mult(drone.getNode().getChild("ThrusterNode4").getLocalRotation()).mult(speed2).getRotationColumn(2), drone.getNode().localToWorld(drone.getNode().getChild("ThrusterNode4").getLocalTranslation(), null));

            //drone.getControl().applyForce(drone.getNode().getChild("ThrusterNode1").getWorldRotation().getRotationColumn(2).mult(speed1), drone.getNode().localToWorld(drone.getNode().getChild("ThrusterNode2").getLocalTranslation(), null));
            //drone.getControl().applyForce(drone.getNode().getChild("ThrusterNode2").getWorldRotation().getRotationColumn(2).mult(speed1), drone.getNode().localToWorld(drone.getNode().getChild("ThrusterNode2").getLocalTranslation(), null));
            //drone.getControl().applyForce(drone.getNode().getChild("ThrusterNode3").getWorldRotation().getRotationColumn(2).mult(speed2), drone.getNode().localToWorld(drone.getNode().getChild("ThrusterNode3").getLocalTranslation(), null));
            //drone.getControl().applyForce(drone.getNode().getChild("ThrusterNode4").getWorldRotation().getRotationColumn(2).mult(speed2), drone.getNode().localToWorld(drone.getNode().getChild("ThrusterNode4").getLocalTranslation(), null));
            // System.out.println("force 1 : " + drone.getThrusterVecs().get(0));
            // System.out.println("force 2 : " + drone.getThrusterVecs().get(1));
            // System.out.println("force 3 : " + drone.getThrusterVecs().get(2));
            // System.out.println("force 4 : " + drone.getThrusterVecs().get(3));

            // System.out.println("force 1 : " + drone.getNode().getChild("ThrusterNode1").getWorldRotation().getRotationColumn(2));
            // System.out.println("force 2 : " + drone.getNode().getChild("ThrusterNode2").getWorldRotation().getRotationColumn(2));
            // System.out.println("force 3 : " + drone.getNode().getChild("ThrusterNode3").getWorldRotation().getRotationColumn(2));
            // System.out.println("force 4 : " + drone.getNode().getChild("ThrusterNode4").getWorldRotation().getRotationColumn(2));

            // System.out.println("Thruster1: " + drone.getInitialThrusterLocalPosition().get(0));
            // System.out.println("Thruster1: " + drone.getThrusterGlobalPositions().get(0));
            // System.out.println("Thruster2: " + drone.getInitialThrusterLocalPosition().get(1));
            // System.out.println("Thruster2: " + drone.getThrusterGlobalPositions().get(1));
            // System.out.println("Thruster3: " + drone.getInitialThrusterLocalPosition().get(2));
            // System.out.println("Thruster3: " + drone.getThrusterGlobalPositions().get(2));
            // System.out.println("Thruster4: " + drone.getInitialThrusterLocalPosition().get(3));
            // System.out.println("Thruster4: " + drone.getThrusterGlobalPositions().get(3));

         // System.out.println("Thruster1: " + drone.getNode().getChild("ThrusterNode1").getLocalTranslation());
         // System.out.println("Thruster2: " + drone.getNode().getChild("ThrusterNode2").getLocalTranslation());
         // System.out.println("Thruster3: " + drone.getNode().getChild("ThrusterNode3").getLocalTranslation());
         // System.out.println("Thruster4: " + drone.getNode().getChild("ThrusterNode4").getLocalTranslation());



            // drone.getControl().applyForce(force1, position1);
            // drone.getControl().applyForce(force2, position2);
            // drone.getControl().applyForce(force3, position3);
            // drone.getControl().applyForce(force4, position4);
            //drone.setThrusterGlobalPositions(new ArrayList<>(List.of(position1.add(force1).normalize(), position2.add(force2).normalize(), position3.add(force3).normalize(), position4.add(force4).normalize())));

            // Drawer.drawLineBetweenPoints(
            //         position1,
            //         position1.add(force1),
            //         rootNode, drone.getAssetManager(), ColorRGBA.Black);
            // Drawer.drawLineBetweenPoints(
            //         position2,
            //         position2.add(force2),
            //         rootNode, drone.getAssetManager(), ColorRGBA.White);
            // Drawer.drawLineBetweenPoints(
            //         position3,
            //         position3.add(force3),
            //         rootNode, drone.getAssetManager(), ColorRGBA.Red);
            // Drawer.drawLineBetweenPoints(
            //         position4,
            //         position4.add(force4),
            //         rootNode, drone.getAssetManager(), ColorRGBA.Yellow);

            Drawer.drawLineBetweenPoints(
                    position1,
                    position1.add(new Vector3f(0,2,0)),
                    rootNode, drone.getAssetManager(), ColorRGBA.Orange);
            Drawer.drawLineBetweenPoints(
                    position2,
                    position2.add(new Vector3f(0,2,0)),
                    rootNode, drone.getAssetManager(), ColorRGBA.White);
            Drawer.drawLineBetweenPoints(
                    position3,
                    position3.add(new Vector3f(0,2,0)),
                    rootNode, drone.getAssetManager(), ColorRGBA.Red);
            Drawer.drawLineBetweenPoints(
                    position4,
                    position4.add(new Vector3f(0,2,0)),
                    rootNode, drone.getAssetManager(), ColorRGBA.Green);
            Drawer.drawLineBetweenPoints(
                    drone.getNode().getWorldTranslation(),
                    drone.getNode().getWorldTranslation().add(new Vector3f(0,2,0)),
                    rootNode, drone.getAssetManager(), ColorRGBA.Magenta);

            Drawer.drawLineBetweenPoints(
                    position1,
                    position1.add(force1),
                    rootNode, drone.getAssetManager(), ColorRGBA.Orange);
            Drawer.drawLineBetweenPoints(
                    position2,
                    position2.add(force2),
                  rootNode, drone.getAssetManager(), ColorRGBA.White);
            Drawer.drawLineBetweenPoints(
                    position3,
                    position4.add(force3),
                  rootNode, drone.getAssetManager(), ColorRGBA.Red);
            Drawer.drawLineBetweenPoints(
                    position4,
                    position4.add(force4),
                    rootNode, drone.getAssetManager(), ColorRGBA.Green);
        }

        if (backward) {
         // System.out.println("BACKWARD");
            int speed1 = 200;
            int speed2 = -200;
            // Use thruster lists instead of individual fields
            Vector3f force1 = drone.getThrusterVecs().get(0).mult(speed1);
            Vector3f force2 = drone.getThrusterVecs().get(1).mult(speed1);
            Vector3f force3 = drone.getThrusterVecs().get(2).mult(speed2);
            Vector3f force4 = drone.getThrusterVecs().get(3).mult(speed2);

            Vector3f position1 = drone.getThrusterGlobalPositions().get(0);
            //System.out.println("position1: " + position1);
            //System.out.println("Drone pos: " + drone.getNode().getLocalTranslation());
            // drone.getThrusterVecs().get(3).mult(speed2);
            Vector3f position2 = drone.getThrusterGlobalPositions().get(1);
            Vector3f position3 = drone.getThrusterGlobalPositions().get(2);
            Vector3f position4 = drone.getThrusterGlobalPositions().get(3);

            // System.out.println("Force4 : " + drone.getThrusterGlobalPositions().get(3));
            // drone.getControl().applyForce(force1, drone.getInitialThrusterLocalPosition().get(0));
            // drone.getControl().applyForce(force2, drone.getInitialThrusterLocalPosition().get(1));
            // drone.getControl().applyForce(force3, drone.getInitialThrusterLocalPosition().get(2));
            // drone.getControl().applyForce(force4, drone.getInitialThrusterLocalPosition().get(3));
            // drone.getControl().applyForce(force1, position1);
            // drone.getControl().applyForce(force3, position3);
            // drone.getControl().applyForce(force2, position2);
            // drone.getControl().applyForce(force4, position4);
            drone.getControl().applyForce(drone.getNode().getChild("ThrusterNode1").localToWorld(force1,null), drone.getNode().localToWorld(drone.getNode().getChild("ThrusterNode1").getLocalTranslation(), null));
            drone.getControl().applyForce(drone.getNode().getChild("ThrusterNode2").localToWorld(force2,null), drone.getNode().localToWorld(drone.getNode().getChild("ThrusterNode2").getLocalTranslation(), null));
            drone.getControl().applyForce(drone.getNode().getChild("ThrusterNode3").localToWorld(force3,null), drone.getNode().localToWorld(drone.getNode().getChild("ThrusterNode3").getLocalTranslation(), null));
            drone.getControl().applyForce(drone.getNode().getChild("ThrusterNode4").localToWorld(force4,null), drone.getNode().localToWorld(drone.getNode().getChild("ThrusterNode4").getLocalTranslation(), null));
            //drone.setThrusterGlobalPositions(new ArrayList<>(List.of(position1.add(force1).normalize(), position2.add(force2).normalize(), position3.add(force3).normalize(), position4.add(force4).normalize())));

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
         // System.out.println("Force42 : " + drone.getThrusterGlobalPositions().get(3));

        }
        // if (backward)
        // force.subtractLocal(forwardDir);
        // if (right)
        // force.addLocal(rightDir);
        // if (left)
        // force.subtractLocal(rightDir);

        if (descend) {
            // force.subtractLocal(upDir);
            int speed = 200;
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

        if(rotateLeft) {
            // System.out.println("FORWARD");
            int speed1 = -20;
            int speed2 = 20;
            // ThrusterNode1 forward direction
            Vector3f forwardDir1 = drone.getNode().getChild("ThrusterNode1").getWorldRotation().getRotationColumn(2);
            Vector3f force1 = forwardDir1.mult(speed1);
            Vector3f position1 = drone.getNode().getChild("ThrusterNode1").getWorldTranslation();
            drone.getControl().applyForce(force1, position1);

            // Repeat for other thrusters
            Vector3f forwardDir2 = drone.getNode().getChild("ThrusterNode2").getWorldRotation().getRotationColumn(2);
            Vector3f force2 = forwardDir2.mult(speed2);
            Vector3f position2 = drone.getNode().getChild("ThrusterNode2").getWorldTranslation();
            drone.getControl().applyForce(force2, position2);

            // ThrusterNode1 forward direction
            Vector3f forwardDir3 = drone.getNode().getChild("ThrusterNode3").getWorldRotation().getRotationColumn(2);
            Vector3f force3 = forwardDir3.mult(speed1);
            Vector3f position3 = drone.getNode().getChild("ThrusterNode3").getWorldTranslation();
            drone.getControl().applyForce(force3, position3);

            // Repeat for other thrusters
            Vector3f forwardDir4 = drone.getNode().getChild("ThrusterNode4").getWorldRotation().getRotationColumn(2);
            Vector3f force4 = forwardDir4.mult(speed2);
            Vector3f position4 = drone.getNode().getChild("ThrusterNode4").getWorldTranslation();
            drone.getControl().applyForce(force4, position4);
            // Use thruster lists instead of individual fields
            // Vector3f force1 = drone.getThrusterVecs().get(0).mult(speed1);
            // Vector3f force2 = drone.getThrusterVecs().get(1).mult(speed2);
            // Vector3f force3 = drone.getThrusterVecs().get(2).mult(speed1);
            // Vector3f force4 = drone.getThrusterVecs().get(3).mult(speed2);

            // Vector3f position1 = drone.getInitialThrusterLocalPosition().get(0);
            // //System.out.println("position1: " + position1);
            // //System.out.println("Drone pos: " + drone.getNode().getLocalTranslation());
            // Vector3f position2 = drone.getInitialThrusterLocalPosition().get(1);
            // Vector3f position3 = drone.getInitialThrusterLocalPosition().get(2);
            // Vector3f position4 = drone.getInitialThrusterLocalPosition().get(3);

            // Vector3f position1 = drone.getNode().getChild("ThrusterNode1").getLocalTranslation();
            // Vector3f position2 = drone.getNode().getChild("ThrusterNode2").getLocalTranslation();
            // Vector3f position3 = drone.getNode().getChild("ThrusterNode3").getLocalTranslation();
            // Vector3f position4 = drone.getNode().getChild("ThrusterNode4").getLocalTranslation();




            // Vector3f force1 = drone.getNode().getChild("ThrusterNode1").getWorldRotation().getRotationColumn(2).mult(speed1); //getLocalRotation
            // Vector3f force2 = drone.getNode().getChild("ThrusterNode2").getWorldRotation().getRotationColumn(2).mult(speed2); //getWorldRotation
            // Vector3f force3 = drone.getNode().getChild("ThrusterNode3").getWorldRotation().getRotationColumn(2).mult(speed1);
            // Vector3f force4 = drone.getNode().getChild("ThrusterNode4").getWorldRotation().getRotationColumn(2).mult(speed2);
            // Vector3f force1 = drone.getNode().getChild("ThrusterNode1").localToWorld(new Vector3f(0.f, 0, 1f).mult(speed1), null); // getLocalRotation
            // Vector3f force2 = drone.getNode().getChild("ThrusterNode2").localToWorld(new Vector3f(0.f, 0, 1f).mult(speed2), null); // getWorldRotation
            // Vector3f force3 = drone.getNode().getChild("ThrusterNode3").localToWorld(new Vector3f(0.f, 0, 1f).mult(speed1), null);
            // Vector3f force4 = drone.getNode().getChild("ThrusterNode4").localToWorld(new Vector3f(0.f, 0, 1f).mult(speed2), null);
            // Vector3f force1 = drone.getNode().getChild("ThrusterNode1").getWorldRotation().getRotationColumn(2).mult(speed1); // getLocalRotation
            // Vector3f force2 = drone.getNode().getChild("ThrusterNode2").getWorldRotation().getRotationColumn(2).mult(speed2); // getWorldRotation
            // Vector3f force3 = drone.getNode().getChild("ThrusterNode3").getWorldRotation().getRotationColumn(2).mult(speed1);
            // Vector3f force4 = drone.getNode().getChild("ThrusterNode4").getWorldRotation().getRotationColumn(2).mult(speed2);
            // System.out.println("translation node1 World : " + drone.getNode().getChild("ThrusterNode1").getWorldTranslation());
            // System.out.println("translation node1 Local : " + drone.getNode().getChild("ThrusterNode1").getLocalTranslation());
            // System.out.println("translation node2 Local : " + drone.getNode().getChild("ThrusterNode2").getLocalTranslation());
            // System.out.println("translation node3 Local : " + drone.getNode().getChild("ThrusterNode3").getLocalTranslation());
            // System.out.println("translation node4 Local : " + drone.getNode().getChild("ThrusterNode4").getLocalTranslation());

            // System.out.println("translation node1 Local2 : " + position1);
            // System.out.println("translation node2 Local2 : " + position2);
            // System.out.println("translation node3 Local2 : " + position3);
            // System.out.println("translation node4 Local2 : " + position4);
            // //System.out.println("translation node4 Local2 : " + drone.getNode().getControl(0));
            // System.out.println("translation physic : " + drone.getControl().isApplyPhysicsLocal());

            // Vector3f force1 = position1..mult(speed1); //getLocalRotation
            // Vector3f force2 = position2.mult(speed2); //getWorldRotation
            // Vector3f force3 = position3.mult(speed1);
            // Vector3f force4 = position4.mult(speed2);
            // System.out.println("Rotation node World : " + drone.getNode().getChild("ThrusterNode1").getWorldRotation().getRotationColumn(2));
            // System.out.println("Rotation node Local : " + drone.getNode().getChild("ThrusterNode1").getWorldRotation().getRotationColumn(2));
            // System.out.println("Rotation physic : " + drone.getControl().isApplyPhysicsLocal());


            // System.out.println("Force4 : " + drone.getThrusterGlobalPositions().get(3));
            // drone.getControl().applyForce(force1, drone.getThrusterGlobalPositions().get(0));
            // drone.getControl().applyForce(force2, drone.getThrusterGlobalPositions().get(1));
            // drone.getControl().applyForce(force3, drone.getThrusterGlobalPositions().get(2));
            // drone.getControl().applyForce(force4, drone.getThrusterGlobalPositions().get(3));

            // drone.getControl().applyForce(force1, Vector3f.ZERO.add(-1,0,1));
            // drone.getControl().applyForce(force2, Vector3f.ZERO.add(1,0,1));
            // drone.getControl().applyForce(force3, Vector3f.ZERO.add(-1,0,-1));
            // drone.getControl().applyForce(force4, Vector3f.ZERO.add(1,0,-1));

            // Vector3f force1 = drone.getNode().getWorldTranslation().mult(drone.getNode().getWorldRotation().mult(Vector3f.UNIT_XYZ)).mult(drone.getThrusterVecs().get(0)).add(drone.getInitialThrusterLocalPosition().get(0)).mult(speed1);
            // Vector3f force2 = drone.getNode().getWorldTranslation().mult(drone.getNode().getWorldRotation().mult(Vector3f.UNIT_XYZ)).mult(drone.getThrusterVecs().get(1)).add(drone.getInitialThrusterLocalPosition().get(1)).mult(speed2);
            // Vector3f force3 = drone.getNode().getWorldTranslation().mult(drone.getNode().getWorldRotation().mult(Vector3f.UNIT_XYZ)).mult(drone.getThrusterVecs().get(2)).add(drone.getInitialThrusterLocalPosition().get(2)).mult(speed1);
            // Vector3f force4 = drone.getNode().getWorldTranslation().mult(drone.getNode().getWorldRotation().mult(Vector3f.UNIT_XYZ)).mult(drone.getThrusterVecs().get(3)).add(drone.getInitialThrusterLocalPosition().get(3)).mult(speed2);

            // drone.getControl().applyForce(force1, drone.getInitialThrusterLocalPosition().get(0));
            // drone.getControl().applyForce(force2, drone.getInitialThrusterLocalPosition().get(1));
            // drone.getControl().applyForce(force3, drone.getInitialThrusterLocalPosition().get(2));
            // drone.getControl().applyForce(force4, drone.getInitialThrusterLocalPosition().get(3));


            // drone.getControl().applyForce(force1, drone.getInitialThrusterLocalPosition().get(0));
            // drone.getControl().applyForce(force2, drone.getInitialThrusterLocalPosition().get(1));
            // drone.getControl().applyForce(force3, drone.getInitialThrusterLocalPosition().get(2));
            // drone.getControl().applyForce(force4, drone.getInitialThrusterLocalPosition().get(3));

            // System.out.println("force 1 : " + drone.getThrusterVecs().get(0).mult(speed1));
            // System.out.println("force 2 : " + drone.getThrusterVecs().get(1).mult(speed2));
            // System.out.println("force 3 : " + drone.getThrusterVecs().get(2).mult(speed1));
            // System.out.println("force 4 : " + drone.getThrusterVecs().get(3).mult(speed2));



            // System.out.println("force 1 : " + drone.getThrusterVecs().get(0));
            // System.out.println("force 2 : " + drone.getThrusterVecs().get(1));
            // System.out.println("force 3 : " + drone.getThrusterVecs().get(2));
            // System.out.println("force 4 : " + drone.getThrusterVecs().get(3));


            // System.out.println("Thruster1: " + drone.getInitialThrusterLocalPosition().get(0));
            // System.out.println("Thruster1: " + drone.getThrusterGlobalPositions().get(0));
            // System.out.println("Thruster2: " + drone.getInitialThrusterLocalPosition().get(1));
            // System.out.println("Thruster2: " + drone.getThrusterGlobalPositions().get(1));
            // System.out.println("Thruster3: " + drone.getInitialThrusterLocalPosition().get(2));
            // System.out.println("Thruster3: " + drone.getThrusterGlobalPositions().get(2));
            // System.out.println("Thruster4: " + drone.getInitialThrusterLocalPosition().get(3));
            // System.out.println("Thruster4: " + drone.getThrusterGlobalPositions().get(3));
            // drone.getControl().clearForces();
            // drone.getControl().applyForce(force1, position1);
            // drone.getControl().applyForce(force3, position3);
            // drone.getControl().applyForce(force2, position2);
            // drone.getControl().applyForce(force4, position4);
            //drone.getControl().applyForce(force1, drone.getNode().localToWorld(position1,null));
            // drone.getControl().applyForce(position1.mult(speed1), position1);
            // drone.getControl().applyForce(position2.mult(speed2), position2);
            // drone.getControl().applyForce(position3.mult(speed1), position3);
            // drone.getControl().applyForce(position4.mult(speed2), position4);

            // drone.getNode().getChild("ThrusterNode1").lookAt(drone.getNode().getChild("ForwardMarker").getLocalTranslation(),Vector3f.UNIT_Y);
            // drone.getNode().getChild("ThrusterNode2").lookAt(drone.getNode().getChild("ForwardMarker").getLocalTranslation(),Vector3f.UNIT_Y);
            // drone.getNode().getChild("ThrusterNode3").lookAt(drone.getNode().getChild("BackwardMarker").getLocalTranslation(),Vector3f.UNIT_Y);
            // drone.getNode().getChild("ThrusterNode4").lookAt(drone.getNode().getChild("BackwardMarker").getLocalTranslation(),Vector3f.UNIT_Y);
            // drone.getControl().applyForce(drone.getNode().getChild("ThrusterNode1").getWorldRotation().getRotationColumn(2).mult(speed1), position1);
            // drone.getControl().applyForce(drone.getNode().getChild("ThrusterNode2").getWorldRotation().getRotationColumn(2).mult(speed2), position2);
            // drone.getControl().applyForce(drone.getNode().getChild("ThrusterNode3").getWorldRotation().getRotationColumn(2).mult(speed1), position3);
            // drone.getControl().applyForce(drone.getNode().getChild("ThrusterNode4").getWorldRotation().getRotationColumn(2).mult(speed2), position4);

         // System.out.println("Forward pos" + drone.getNode().getChild("ForwardMarker").getLocalTranslation());
            // drone.getControl().applyForce(drone.getNode().getChild("ThrusterNode1").localToWorld(force1,null), drone.getNode().localToWorld(drone.getNode().getChild("ThrusterNode1").getLocalTranslation(), null));
            // drone.getControl().applyForce(drone.getNode().getChild("ThrusterNode2").localToWorld(force2,null), drone.getNode().localToWorld(drone.getNode().getChild("ThrusterNode2").getLocalTranslation(), null));
            // drone.getControl().applyForce(drone.getNode().getChild("ThrusterNode3").localToWorld(force3,null), drone.getNode().localToWorld(drone.getNode().getChild("ThrusterNode3").getLocalTranslation(), null));
            // drone.getControl().applyForce(drone.getNode().getChild("ThrusterNode4").localToWorld(force4,null), drone.getNode().localToWorld(drone.getNode().getChild("ThrusterNode4").getLocalTranslation(), null));
            //drone.setThrusterGlobalPositions(new ArrayList<>(List.of(position1.add(force1).normalize(), position2.add(force2).normalize(), position3.add(force3).normalize(), position4.add(force4).normalize())));

            Drawer.drawLineBetweenPoints(
                    position1,
                    position1.add(new Vector3f(0,2,0)),
                    drone.getNode(), drone.getAssetManager(), ColorRGBA.Orange);
            Drawer.drawLineBetweenPoints(
                    position2,
                    position2.add(new Vector3f(0,2,0)),
                    drone.getNode(), drone.getAssetManager(), ColorRGBA.White);
            Drawer.drawLineBetweenPoints(
                    position3,
                    position3.add(new Vector3f(0,2,0)),
                    drone.getNode(), drone.getAssetManager(), ColorRGBA.Red);
            Drawer.drawLineBetweenPoints(
                    position4,
                    position4.add(new Vector3f(0,2,0)),
                    drone.getNode(), drone.getAssetManager(), ColorRGBA.Green);
            Drawer.drawLineBetweenPoints(
                    drone.getNode().getWorldTranslation(),
                    drone.getNode().getWorldTranslation().add(new Vector3f(0,2,0)),
                    drone.getNode(), drone.getAssetManager(), ColorRGBA.Magenta);

            Drawer.drawLineBetweenPoints(
                    drone.getNode().getWorldTranslation().add(position1),
                    drone.getNode().getWorldTranslation().add(position1.add(force1)),
                    rootNode, drone.getAssetManager(), ColorRGBA.Orange);
            Drawer.drawLineBetweenPoints(
                drone.getNode().getWorldTranslation().add(position2),
                drone.getNode().getWorldTranslation().add(position2.add(force2)),
                    rootNode, drone.getAssetManager(), ColorRGBA.White);
            Drawer.drawLineBetweenPoints(
                drone.getNode().getWorldTranslation().add(position3),
                drone.getNode().getWorldTranslation().add(position4.add(force3)),
                    rootNode, drone.getAssetManager(), ColorRGBA.Red);
            Drawer.drawLineBetweenPoints(
                drone.getNode().getWorldTranslation().add(position4),
                drone.getNode().getWorldTranslation().add(position4.add(force4)),
                    rootNode, drone.getAssetManager(), ColorRGBA.Green);
            // System.out.println("Force42 : " + drone.getThrusterGlobalPositions().get(3));
        }

        if(rotateRight) {
            // System.out.println("FORWARD");
            int speed1 = 200;
            int speed2 = -200;
            // Use thruster lists instead of individual fields
            Vector3f force1 = drone.getThrusterVecs().get(0).mult(speed1);
            Vector3f force2 = drone.getThrusterVecs().get(1).mult(speed2);
            Vector3f force3 = drone.getThrusterVecs().get(2).mult(speed1);
            Vector3f force4 = drone.getThrusterVecs().get(3).mult(speed2);

            Vector3f position1 = drone.getThrusterGlobalPositions().get(0);
            //System.out.println("position1: " + position1);
            //System.out.println("Drone pos: " + drone.getNode().getLocalTranslation());
            Vector3f position2 = drone.getThrusterGlobalPositions().get(1);
            Vector3f position3 = drone.getThrusterGlobalPositions().get(2);
            Vector3f position4 = drone.getThrusterGlobalPositions().get(3);


            // System.out.println("Force4 : " + drone.getThrusterGlobalPositions().get(3));
            // drone.getControl().applyForce(force1, drone.getThrusterGlobalPositions().get(0));
            // drone.getControl().applyForce(force2, drone.getThrusterGlobalPositions().get(1));
            // drone.getControl().applyForce(force3, drone.getThrusterGlobalPositions().get(2));
            // drone.getControl().applyForce(force4, drone.getThrusterGlobalPositions().get(3));

            // drone.getControl().applyForce(force1, Vector3f.ZERO.add(-1,0,1));
            // drone.getControl().applyForce(force2, Vector3f.ZERO.add(1,0,1));
            // drone.getControl().applyForce(force3, Vector3f.ZERO.add(-1,0,-1));
            // drone.getControl().applyForce(force4, Vector3f.ZERO.add(1,0,-1));

            // Vector3f force1 = drone.getNode().getWorldTranslation().mult(drone.getNode().getWorldRotation().mult(Vector3f.UNIT_XYZ)).mult(drone.getThrusterVecs().get(0)).add(drone.getInitialThrusterLocalPosition().get(0)).mult(speed1);
            // Vector3f force2 = drone.getNode().getWorldTranslation().mult(drone.getNode().getWorldRotation().mult(Vector3f.UNIT_XYZ)).mult(drone.getThrusterVecs().get(1)).add(drone.getInitialThrusterLocalPosition().get(1)).mult(speed2);
            // Vector3f force3 = drone.getNode().getWorldTranslation().mult(drone.getNode().getWorldRotation().mult(Vector3f.UNIT_XYZ)).mult(drone.getThrusterVecs().get(2)).add(drone.getInitialThrusterLocalPosition().get(2)).mult(speed1);
            // Vector3f force4 = drone.getNode().getWorldTranslation().mult(drone.getNode().getWorldRotation().mult(Vector3f.UNIT_XYZ)).mult(drone.getThrusterVecs().get(3)).add(drone.getInitialThrusterLocalPosition().get(3)).mult(speed2);

            // drone.getControl().applyForce(force1, drone.getInitialThrusterLocalPosition().get(0));
            // drone.getControl().applyForce(force2, drone.getInitialThrusterLocalPosition().get(1));
            // drone.getControl().applyForce(force3, drone.getInitialThrusterLocalPosition().get(2));
            // drone.getControl().applyForce(force4, drone.getInitialThrusterLocalPosition().get(3));


            // drone.getControl().applyForce(force1, drone.getInitialThrusterLocalPosition().get(0));
            // drone.getControl().applyForce(force2, drone.getInitialThrusterLocalPosition().get(1));
            // drone.getControl().applyForce(force3, drone.getInitialThrusterLocalPosition().get(2));
            // drone.getControl().applyForce(force4, drone.getInitialThrusterLocalPosition().get(3));

            // System.out.println("force 1 : " + drone.getThrusterVecs().get(0).mult(speed1));
            // System.out.println("force 2 : " + drone.getThrusterVecs().get(1).mult(speed2));
            // System.out.println("force 3 : " + drone.getThrusterVecs().get(2).mult(speed1));
            // System.out.println("force 4 : " + drone.getThrusterVecs().get(3).mult(speed2));



         // System.out.println("force 1 : " + drone.getThrusterVecs().get(0));
         // System.out.println("force 2 : " + drone.getThrusterVecs().get(1));
         // System.out.println("force 3 : " + drone.getThrusterVecs().get(2));
         // System.out.println("force 4 : " + drone.getThrusterVecs().get(3));


            // System.out.println("Thruster1: " + drone.getInitialThrusterLocalPosition().get(0));
            // System.out.println("Thruster1: " + drone.getThrusterGlobalPositions().get(0));
            // System.out.println("Thruster2: " + drone.getInitialThrusterLocalPosition().get(1));
            // System.out.println("Thruster2: " + drone.getThrusterGlobalPositions().get(1));
            // System.out.println("Thruster3: " + drone.getInitialThrusterLocalPosition().get(2));
            // System.out.println("Thruster3: " + drone.getThrusterGlobalPositions().get(2));
            // System.out.println("Thruster4: " + drone.getInitialThrusterLocalPosition().get(3));
            // System.out.println("Thruster4: " + drone.getThrusterGlobalPositions().get(3));
            drone.getControl().clearForces();
            drone.getControl().applyForce(force1, position1);
            drone.getControl().applyForce(force2, position2);
            drone.getControl().applyForce(force3, position3);
            drone.getControl().applyForce(force4, position4);
            //drone.setThrusterGlobalPositions(new ArrayList<>(List.of(position1.add(force1).normalize(), position2.add(force2).normalize(), position3.add(force3).normalize(), position4.add(force4).normalize())));

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
            // System.out.println("Force42 : " + drone.getThrusterGlobalPositions().get(3));
        }


        if (ascend) {
            float currentY = drone.getNode().getWorldTranslation().y;
            if (currentY < waterLevel) {
                int speed = 200;
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
        // Vector3f angular = Vector3f.ZERO;
        // drone.getControl().setAngularVelocity(angular);

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
