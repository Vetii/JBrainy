package se.lth.cs;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class MapApplication  extends Application<Map<Integer, Integer>> {

    public MapApplication(int seed, int applicationSize, Map<Integer, Integer> structure) {
        super(seed, applicationSize, structure);
    }

    public void runClear() { dataStructure.clear(); }

    public void runContainsKey() { dataStructure.containsKey(generateIndex()); }

    public void runContainsValue() { dataStructure.containsValue(randomGenerator.nextInt()); }

    public void runEntrySet() { dataStructure.entrySet(); }

    public void runEquals() { dataStructure.equals(argument); }

    public void runGet() { dataStructure.get(generateIndex()); }

    public void runHashCode() { dataStructure.hashCode(); }

    public void runIsEmpty() { dataStructure.isEmpty(); }

    public void runKeySet() { dataStructure.keySet(); }

    public void runPut() {
        dataStructure.put(
                generateIndex(),
                randomGenerator.nextInt());
    }

    public void runPutAll() {
        dataStructure.putAll(argument);
    }

    public void runRemove() { dataStructure.remove(generateIndex()); }

    public void runSize() { dataStructure.size(); }

    public void runValues() { dataStructure.values(); }

    @Override
    public int generateIndex() {
        return randomGenerator.nextInt(2 * dataStructure.size() + 1);
    }
}
