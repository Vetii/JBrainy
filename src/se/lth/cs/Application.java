package se.lth.cs;

import java.util.Random;

/**
 * An application, meaning "a generated application"
 */
public abstract class Application {

    private Random randomGenerator;

    public Application(int seed, String configuration) {
        randomGenerator = new Random(seed);
    }

    public abstract void runMethod();

    public void run() {
        int NUM_CALLS = 100; // TODO: Read from configuration
        for(int i = 0; i < NUM_CALLS; ++i) {
            runMethod();
        }
    }
}
