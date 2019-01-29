package se.lth.cs;

import java.util.Map;

public class MapApplication  extends Application<Map<Integer, Integer>> {

    public MapApplication(int seed, int applicationSize, Map<Integer, Integer> structure) {
        super(seed, applicationSize, structure);
    }

    public void runClear() { dataStructure.clear(); }

    public void runContainsKey() { dataStructure.containsKey(randomGenerator.nextInt()); }

    public void runContainsValue() { dataStructure.containsValue(randomGenerator.nextInt()); }

    public void runEntrySet() { dataStructure.entrySet(); }

    public void runEquals() { dataStructure.equals(argument); }

    public void runGet() { dataStructure.get(randomGenerator.nextInt()); }

    public void runHashCode() { dataStructure.hashCode(); }

    public void runIsEmpty() { dataStructure.isEmpty(); }

    public void runKeySet() { dataStructure.keySet(); }

    public void runPut() {
        dataStructure.put(
                randomGenerator.nextInt(),
                randomGenerator.nextInt());
    }

    public void runPutAll() {
        dataStructure.putAll(argument);
    }

    public void runRemove() { dataStructure.remove(randomGenerator.nextInt()); }

    public void runSize() { dataStructure.size(); }

    public void runValues() { dataStructure.values(); }
}
