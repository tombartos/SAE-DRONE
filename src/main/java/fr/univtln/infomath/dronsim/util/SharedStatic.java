package fr.univtln.infomath.dronsim.util;

import java.lang.ref.Cleaner;

public abstract class SharedStatic {
    public static final Cleaner cleaner = Cleaner.create();
}
