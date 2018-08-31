package se.lth.cs;

import java.lang.reflect.InvocationTargetException;
import java.util.Random;

/**
 * An application, meaning "a generated application"
 */
public abstract class Application {

    Random randomGenerator;

    Application(int seed, String configuration) {
        randomGenerator = new Random(seed);
    }

    protected abstract int generateIndex();

    /**
     * Randomly selects a method generator and runs the resulting method.
     */
    public abstract void runMethod() throws InvocationTargetException, IllegalAccessException;

    public void benchmark() throws InvocationTargetException, IllegalAccessException {
        int NUM_CALLS = 10000; // TODO: Read from configuration
        for(int i = 0; i < NUM_CALLS; ++i) {
            runMethod();
        }
    }
}
