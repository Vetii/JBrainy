package se.lth.cs;

public class TrainingSetValue {

    private AppRunData runningData;

    private final Application application;

    private final String dataStructure;

    private final String bestDataStructure;

    public TrainingSetValue(AppRunData runningData, Application application, String bestDataStructure) {
        this.runningData = runningData;
        this.application = application;
        this.dataStructure = application.dataStructure.getClass().getName();
        this.bestDataStructure = bestDataStructure;
    }

    public Application getApplication() {
        return application;
    }

    public String getDataStructure() { return dataStructure; }

    public String getBestDataStructure() {
        return bestDataStructure;
    }

    public AppRunData getRunningData() {
        return runningData;
    }
}
