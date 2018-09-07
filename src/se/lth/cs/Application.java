package se.lth.cs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * An application, meaning "a generated application"
 */
public abstract class Application {

    Random randomGenerator;

    // All methods which might be called.
    List<Method> methodsToCall;

    Application(int seed, String configuration) {
        randomGenerator = new Random(seed);

        // We get the list of methods to run.
        // TODO: Probably moveable to Application class.
        Method[] ms = this.getClass().getMethods();
        methodsToCall = new ArrayList<>();
        for (Method m : ms) {
            if (m.getName().startsWith("run") && !m.getName().equals("runMethod")) {
                methodsToCall.add(m);
            }
        }
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
