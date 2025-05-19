package fr.univtln.infomath.dronsim.simulation;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.scene.Spatial;
import java.io.File;

/**
 * Convertit un modèle GLB en J3O pour une utilisation dans JMonkeyEngine.
 * Assurez-vous que le modèle GLB est placé dans le dossier "assets/Models".
 */

public class ConvertGLBtoJ3O extends SimpleApplication {
    public static void main(String[] args) {
        ConvertGLBtoJ3O app = new ConvertGLBtoJ3O();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        try {
            assetManager.registerLocator("data/asset", FileLocator.class);
            Spatial model = assetManager.loadModel("bateau/speedboat_n2.glb");

            BinaryExporter.getInstance().save(model,
                    new File("data/asset/bateau/speedboat_n2.j3o"));
            System.out.println(" Conversion terminée.");
        } catch (Exception e) {
            System.err.println(" Erreur lors de l’export : " + e.getMessage());
            e.printStackTrace();
        } finally {
            stop();
        }
    }

}
