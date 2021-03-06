package se.lth.cs;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Random;

public class CollectionApplication<T extends Collection> extends Application<T> {

    CollectionApplication(int seed, int applicationSize, Collection structure) {
        super(seed, applicationSize, (T) structure);

        // We add random numbers to it.
        randomGenerator.ints(100).forEach((i) -> argument.add(i));
    }

    @Override
    public void populate(int numberElements) {
        randomGenerator = new Random(seed);
        randomGenerator.ints(numberElements).forEach((i) -> dataStructure.add(i));
    }

    @Override
    protected void clearDataStructure() {
        dataStructure.clear();
    }

    public int generateIndex() {
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
        dataStructure.remove((Object) randomGenerator.nextInt());
    }

    public void runRemoveAll() { dataStructure.removeAll(argument); }

    public void runRetainAll() {
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
}
