package se.lth.cs.ApplicationGeneration;

import se.lth.cs.Application;
import se.lth.cs.MapApplication;

import java.util.*;

public class MapApplicationGenerator implements ApplicationGenerator {
    @Override
    public List<Application<?>> createApplications(int seed, int number, int size) {
        List<Application<?>> mapApplications = new ArrayList<>();

        for (int i = 0; i < number; ++i) {
            mapApplications.add(new MapApplication(seed + i, size, new HashMap<>()));
            mapApplications.add(new MapApplication(seed + i, size, new TreeMap<>()));
            mapApplications.add(new MapApplication(seed + i, size, new IdentityHashMap<>()));
            mapApplications.add(new MapApplication(seed + i, size, new LinkedHashMap<>()));
            mapApplications.add(new MapApplication(seed + i, size, new WeakHashMap<>()));
        }

        return mapApplications;
    }
}
