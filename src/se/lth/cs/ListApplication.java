package se.lth.cs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class ListApplication extends Application {
    private List<Integer> dataStructure;

    // Data structure to use for addAll,...
    private List argument;

    public ListApplication(int seed,
                           String configuration,
                           List<Integer> init) {
        super(seed, configuration);

        dataStructure = init;

        // Argument becomes a new data structure of same class as
        // the data structure to test
        try {
            argument = dataStructure.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        // We add random numbers to it.
        randomGenerator.ints(100).forEach((i) -> argument.add(i));
    }

    public List<Integer> getDataStructure() { return dataStructure; }

    @Override
    protected int generateIndex() {
        return randomGenerator.nextInt(dataStructure.size());
    }

    public void runAdd() {
        dataStructure.add(randomGenerator.nextInt());
    }

    public void runAddAll() {
        dataStructure.addAll(argument);
    }

    public void runClear() {
        dataStructure.clear();
    }

    public void runContains() {
        dataStructure.contains(randomGenerator.nextInt());
    }

    public void runContainsAll() {
        dataStructure.containsAll(argument);
    }

    public void runEquals() {
        dataStructure.equals(randomGenerator.ints(100)); // TODO: Read from configuration
    }

    public void runGet() {
        if (dataStructure.isEmpty()) { return; }
        dataStructure.get(generateIndex());
    }

    public void runHashCode() {
        dataStructure.hashCode();
    }

    public void runIndexOf() {
        dataStructure.indexOf(randomGenerator.nextInt());
    }

    public void runIsEmpty() {
        dataStructure.isEmpty();
    }

    public void runIterator() {
        dataStructure.iterator();
    }

    public void runLastIndexOf() {
        dataStructure.lastIndexOf(randomGenerator.nextInt());
    }

    public void runListIterator() {
        if (randomGenerator.nextBoolean()) {
            dataStructure.listIterator();
        } else {
            if (dataStructure.isEmpty()) { return; }
            dataStructure.listIterator(generateIndex());
        }
    }

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

    public void runSet() {
        if (dataStructure.isEmpty()) { return; }
        dataStructure.set(generateIndex(), randomGenerator.nextInt());
    }

    public void runSize() {
        dataStructure.size();
    }

    public void runSubList() {
        if (dataStructure.isEmpty()) { return; }
        int a = generateIndex();
        int b = generateIndex();
        dataStructure.subList(Math.min(a, b), Math.max(a, b));
    }

    public void runToArray() {
        if (randomGenerator.nextBoolean()) {
            dataStructure.toArray();
        } else {
            Integer[] array = new Integer[3];
            dataStructure.toArray(array);
        }
    }

    @Override
    public void runMethod() throws InvocationTargetException, IllegalAccessException {
        // We select a random method to call.
        int i = randomGenerator.nextInt(methodsToCall.size());
        Method m = methodsToCall.get(i);
        // And call it
        m.invoke(this);
    }
}
