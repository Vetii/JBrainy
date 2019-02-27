package se.lth.cs;

import java.util.List;

public class TrainingSetValue extends AppRunData {

    private final String bestDataStructure;

    public TrainingSetValue(Application<?> app, List<Double> samples, String bestDataStructure) {
        super(app, samples);
        this.bestDataStructure = bestDataStructure;
    }

    public String getDataStructure() { return application.getDataStructureName(); }

    public String getBestDataStructure() { return bestDataStructure; }
}
