package se.lth.cs;

import java.util.List;

public class ListApplication extends CollectionApplication<List<Integer>> {

    public ListApplication(int seed,
                           int applicationSize,
                           List<Integer> init) {
        super(seed, applicationSize, init);
    }

    public void runAdd2() {
        if (dataStructure.isEmpty()) {
            dataStructure.add(0, randomGenerator.nextInt());
            return;
        }
        dataStructure.add(generateIndex(), randomGenerator.nextInt());
    }

    public void runAddAll2() {
        if (dataStructure.isEmpty()) {
            dataStructure.addAll(0, argument);
            return;
        }
        dataStructure.addAll(generateIndex(), argument);
    }

    public void runRemove2() {
        if (dataStructure.isEmpty()) { return; }
        dataStructure.remove(generateIndex());
    }

    public void runSort() {
        dataStructure.sort(Integer::compareTo);
    }

    public void runGet() {
        if (dataStructure.isEmpty()) { return; }
        dataStructure.get(generateIndex());
    }

    public void runIndexOf() {
        dataStructure.indexOf(randomGenerator.nextInt());
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

    public void runSet() {
        if (dataStructure.isEmpty()) { return; }
        dataStructure.set(generateIndex(), randomGenerator.nextInt());
    }

    public void runSubList() {
        if (dataStructure.isEmpty()) { return; }
        int a = generateIndex();
        int b = generateIndex();
        dataStructure.subList(Math.min(a, b), Math.max(a, b));
    }
}
