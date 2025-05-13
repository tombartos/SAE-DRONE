package fr.univtln.infomath.dronsim.testing;

import fr.univtln.infomath.dronsim.controlers.Controler;
import fr.univtln.infomath.dronsim.controlers.ArduSubControler;

public class ControlerTester {
    public static boolean sleep(long milis) {
        try {
            Thread.sleep(milis);
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }

    public static void main(String[] argv) {
        Controler ctrl = ArduSubControler.create(123);
        while (sleep(1000)) {
            System.out.print('\r');
            for (int i = 0; i < 16; ++i) {
                System.out.print(ctrl.getMotorThrottle(i));
                System.out.print(' ');
            }
        }
        ctrl.destroy();
    }
}
