package se.lth.cs.ApplicationGeneration;

import se.lth.cs.Application;
import se.lth.cs.ListApplication;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class ListApplicationGenerator implements ApplicationGenerator {
    @Override
    public List<Application<?>> createApplications(int seed, int number, int size) {
        List<Application<?>> listApplications = new ArrayList<>();
        for (int i = 0; i < number; ++i) {
            listApplications.add(new ListApplication(seed + i, size, new ArrayList<>()));
            listApplications.add(new ListApplication(seed + i, size, new LinkedList()));
            listApplications.add(new ListApplication(seed + i, size, new Vector<>()));
        }

        return listApplications;
    }
}
