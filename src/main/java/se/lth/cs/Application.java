package se.lth.cs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * An application, meaning "a generated application"
 */
public abstract class Application<T> {

    Random randomGenerator;

    T dataStructure;

    T argument;

    // All methods which might be called.
    List<Method> methodsToCall;

    int seed;

    int applicationSize;

    Application(int seed, int applicationSize, T structure) {
        this.seed = seed;
        this.applicationSize = applicationSize;
        randomGenerator = new Random(seed);

        // We get the list of methods to run.
        Method[] ms = this.getClass().getMethods();
        methodsToCall = new ArrayList<>();
        for (Method m : ms) {
            if (m.getName().startsWith("run") && !m.getName().equals("runMethod")) {
                methodsToCall.add(m);
            }
        }

        dataStructure = structure;

        // Argument becomes a new data structure of same class as
        // the data structure to test
        try {
            argument = (T) dataStructure.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void runMethod() throws InvocationTargetException, IllegalAccessException {
        // We select a random method to call.
        if (methodsToCall.isEmpty()) { return; }
        int i = randomGenerator.nextInt(methodsToCall.size());
        Method m = methodsToCall.get(i);
        // And call it
        m.invoke(this);
    }

    public void benchmark() throws InvocationTargetException, IllegalAccessException {
        for(int i = 0; i < applicationSize; ++i) {
            runMethod();
        }
    }

    public T getDataStructure() { return dataStructure; }

    public int getSeed() { return seed; }
}