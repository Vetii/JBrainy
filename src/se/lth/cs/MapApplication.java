package se.lth.cs;

import java.util.Map;

public class MapApplication  extends Application<Map<Integer, Integer>> {

    public MapApplication(int seed, String configuration, Map<Integer, Integer> structure) {
        super(seed, configuration, structure);
    }

    void runClear() { dataStructure.clear(); }

    void runContainsKey() { dataStructure.containsKey(randomGenerator.nextInt()); }

    void runContainsValue() { dataStructure.containsValue(randomGenerator.nextInt()); }

    void runEntrySet() { dataStructure.entrySet(); }

    void runEquals() { dataStructure.equals(argument); }

    void runGet() { dataStructure.get(randomGenerator.nextInt()); }

    void runHashCode() { dataStructure.hashCode(); }

    void runIsEmpty() { dataStructure.isEmpty(); }

    void runKeySet() { dataStructure.keySet(); }

    void runPut() {
        dataStructure.put(
                randomGenerator.nextInt(),
                randomGenerator.nextInt());
    }

    void runPutAll() {
        dataStructure.putAll(argument);
    }

    void runRemove() { dataStructure.remove(randomGenerator.nextInt()); }

    void runSize() { dataStructure.size(); }

    void runValues() { dataStructure.values(); }
}
