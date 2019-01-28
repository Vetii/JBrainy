package se.lth.cs.ApplicationGeneration;

import se.lth.cs.Application;

import java.util.List;

public interface ApplicationGenerator {

    /**
     * Creates a list of list applications
     * For each type of list, we create an application
     * @param number
     * @param size
     * @return
     */
    List<Application<?>> createApplications(int seed, int number, int size);
}
