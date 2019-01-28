package se.lth.cs.ApplicationGeneration;

import se.lth.cs.Application;
import se.lth.cs.SetApplication;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArraySet;

public class SetApplicationGenerator implements ApplicationGenerator {
    @Override
    public List<Application<?>> createApplications(int seed, int number, int size) {
        List<Application<?>> setApplications = new ArrayList<>();
        for (int i = 0; i < number; ++i) {
            setApplications.add(new SetApplication(seed + i, size, new HashSet<>()));
            setApplications.add(new SetApplication(seed + i, size, new TreeSet<>()));
            setApplications.add(new SetApplication(seed + i, size, new LinkedHashSet<>()));
            setApplications.add(new SetApplication(seed + i, size, new ConcurrentSkipListSet<>()));
            setApplications.add(new SetApplication(seed + i, size, new CopyOnWriteArraySet<>()));
        }

        return setApplications;
    }
}
