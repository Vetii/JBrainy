package se.lth.cs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public T benchmark() throws InvocationTargetException, IllegalAccessException, InstantiationException {
        for(int i = 0; i < applicationSize; ++i) {
            methodsToCall[i].invoke(this);
        }
        return dataStructure;
    }

    abstract public void populate(int numberElements);

    abstract public int generateIndex();

    public T getDataStructure() { return dataStructure; }

    public String getDataStructureName() { return dataStructure.getClass().getCanonicalName(); }

    public int getSeed() { return seed; }

    public String getSeedString() { return Integer.toString(seed); }

    public String getIdentifier() { return seed + ":" + getDataStructureName(); }

    public Map<String, Long> methodHistogram() {
        Stream<Method> l = Arrays.stream(methodsToCall);
        return l.collect(Collectors.groupingBy(x -> x.getName(), Collectors.counting()));
    }

    public int getSize() { return methodsToCall.length; }
}
