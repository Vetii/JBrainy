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
public abstract class Application<T extends Collection> {

    Random randomGenerator;

    T dataStructure;

    T argument;

    // All methods which might be called.
    List<Method> methodsToCall;

    Application(int seed, String configuration, T structure) {
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

        dataStructure = structure;

        // Argument becomes a new data structure of same class as
        // the data structure to test
        try {
            argument = (T) dataStructure.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        // We add random numbers to it.
        randomGenerator.ints(100).forEach((i) -> argument.add(i));
    }

    protected int generateIndex() {
        return randomGenerator.nextInt(dataStructure.size());
    }

    public void runAdd() { dataStructure.add(randomGenerator.nextInt()); }

    public void runAddAll() { dataStructure.addAll(argument); }

    public void runClear() { dataStructure.clear(); }

    public void runContains() {
        dataStructure.contains(randomGenerator.nextInt());
    }

    public void runContainsAll() {
        dataStructure.containsAll(argument);
    }

    public void runEquals() {
        dataStructure.equals(randomGenerator.ints(100)); // TODO: Read from configuration
    }

    public void runHashCode() { dataStructure.hashCode(); }

    public void runIsEmpty() { dataStructure.isEmpty(); }

    public void runIterator() { dataStructure.iterator(); }

    public void runRemove() {
        if (dataStructure.isEmpty()) { return; }
        if (randomGenerator.nextBoolean()) {
            dataStructure.remove(generateIndex());
        } else {
            dataStructure.remove((Object) randomGenerator.nextInt());
        }
    }

    public void runRemoveAll() {
        dataStructure.removeAll(argument);
    }

    public void retainAll() {
        dataStructure.retainAll(argument);
    }

    public void runSize() { dataStructure.size(); }

    public void runToArray() {
        if (randomGenerator.nextBoolean()) {
            dataStructure.toArray();
        } else {
            Integer[] array = new Integer[3];
            dataStructure.toArray(array);
        }
    }

    public void runMethod() throws InvocationTargetException, IllegalAccessException {
        // We select a random method to call.
        int i = randomGenerator.nextInt(methodsToCall.size());
        Method m = methodsToCall.get(i);
        // And call it
        m.invoke(this);
    }

    public void benchmark() throws InvocationTargetException, IllegalAccessException {
        int NUM_CALLS = 10000; // TODO: Read from configuration
        for(int i = 0; i < NUM_CALLS; ++i) {
            runMethod();
        }
    }

    public T getDataStructure() { return dataStructure; }
}
