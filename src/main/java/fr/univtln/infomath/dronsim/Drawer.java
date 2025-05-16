package fr.univtln.infomath.dronsim;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Line;

import java.util.ArrayList;
import java.util.List;

public class Drawer {
    // Store references to created line geometries
    private static final List<Geometry> createdLines = new ArrayList<>();

    public static void drawLineBetweenPoints(Vector3f start, Vector3f end, Node parentNode, AssetManager assetManager,
            ColorRGBA color) {
        // Create the line mesh
        Line line = new Line(start, end);
        line.setLineWidth(2); // optional: set line thickness

        // Create the geometry
        Geometry lineGeom = new Geometry("Line", line);

        // Create a material for the line
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        lineGeom.setMaterial(mat);

        // Attach the line to the parent node
        parentNode.attachChild(lineGeom);

        // Store the geometry for later removal
        createdLines.add(lineGeom);
    }

    // Method to remove all previously created lines from the parent node
    public static void deleteAllLines(Node parentNode) {
        for (Geometry lineGeom : createdLines) {
            parentNode.detachChild(lineGeom);
        }
        createdLines.clear();
    }
}
