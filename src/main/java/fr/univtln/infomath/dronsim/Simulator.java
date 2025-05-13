package fr.univtln.infomath.dronsim;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Simulator {
    private static final Logger log = LoggerFactory.getLogger(Simulator.class);

    public static void main(String[] args) {
        log.error("Error: Refusing to run simulator. You are already in the matrix.");
        System.exit(1);
    }
}
