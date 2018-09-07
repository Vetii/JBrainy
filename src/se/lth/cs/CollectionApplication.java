package se.lth.cs;

import java.util.Collection;

public class CollectionApplication<T extends Collection> extends Application<T> {

    CollectionApplication(int seed, String configuration, Collection structure) {
        super(seed, configuration, (T) structure);

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
}
