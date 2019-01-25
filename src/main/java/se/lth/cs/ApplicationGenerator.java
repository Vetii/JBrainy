package se.lth.cs;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArraySet;

public class ApplicationGenerator {
    /**
     * Creates a list of list applications
     * For each type of list, we create an application
     * @param number
     * @param size
     * @return
     */
    public static List<ListApplication> createListApplications(int number, int size) {
        List<ListApplication> listApplications = new ArrayList<>();
        for (int i = 0; i < number; ++i) {
            listApplications.add(new ListApplication(i, size, new ArrayList<>()));
            listApplications.add(new ListApplication(i, size, new LinkedList()));
            listApplications.add(new ListApplication(i, size, new Vector<>()));
        }

        return listApplications;
    }

    public static List<SetApplication> createSetApplications(int number, int size) {
        List<SetApplication> setApplications = new ArrayList<>();
        for (int i = 0; i < number; ++i) {
            setApplications.add(new SetApplication(i, size, new HashSet<>()));
            setApplications.add(new SetApplication(i, size, new TreeSet<>()));
            setApplications.add(new SetApplication(i, size, new LinkedHashSet<>()));
            setApplications.add(new SetApplication(i, size, new ConcurrentSkipListSet<>()));
            setApplications.add(new SetApplication(i, size, new CopyOnWriteArraySet<>()));
        }

        return setApplications;
    }

    public static List<MapApplication> createMapApplications(int number, int size) {
        List<MapApplication> mapApplications = new ArrayList<>();

        for (int i = 0; i < number; ++i) {
            mapApplications.add(new MapApplication(i, size, new HashMap<>()));
            mapApplications.add(new MapApplication(i, size, new TreeMap<>()));
            mapApplications.add(new MapApplication(i, size, new IdentityHashMap<>()));
            mapApplications.add(new MapApplication(i, size, new LinkedHashMap<>()));
            mapApplications.add(new MapApplication(i, size, new WeakHashMap<>()));
        }

        return mapApplications;
    }
}
