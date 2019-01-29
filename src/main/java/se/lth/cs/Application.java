package se.lth.cs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
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
    List<Method> allCallableMethods;

    private Method[] methodsToCall;

    int seed;

    int applicationSize;

    Application(int seed, int applicationSize, T structure) {
        this.seed = seed;
        this.applicationSize = applicationSize;
        randomGenerator = new Random(seed);

        // We get the list of methods to run.
        Method[] ms = this.getClass().getMethods();
        allCallableMethods = new ArrayList<>();
        for (Method m : ms) {
            if (m.getName().startsWith("run") && !m.getName().equals("runMethod")) {
                allCallableMethods.add(m);
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

        if (allCallableMethods.isEmpty()) { return; }

        methodsToCall = new Method[applicationSize];
        for(int i = 0; i < applicationSize; ++i) {
            methodsToCall[i] = fetchMethod();
        }
    }

    protected Method fetchMethod() {
        // We select a random method to call.
        int i = randomGenerator.nextInt(allCallableMethods.size());
        Method selected = allCallableMethods.get(i);
        allCallableMethods.add(selected); // We add it back to the list of methods, to make it more likely to be selected!
        return selected;
    }

    public void runMethod() throws InvocationTargetException, IllegalAccessException {

    }

    public void benchmark() throws InvocationTargetException, IllegalAccessException {
        for(int i = 0; i < applicationSize; ++i) {
            methodsToCall[i].invoke(this);
        }
    }

    public T getDataStructure() { return dataStructure; }

    public int getSeed() { return seed; }
}
