package fr.univtln.infomath.dronsim.client;

public class Launcher {
    public static void main(String[] argv) {
        for (int i = 3; i > 0; --i) {
            System.out.println(String.valueOf(i) + "...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Abort! Abort mission!");
                System.exit(1);
            }
        }
        System.out.println("WOOSH!!!");
    }
}
