package se.lth.cs;

public class TrainingSetValue {

    private final Long runningTime;

    private final Application application;

    private final String dataStructure;

    public TrainingSetValue(long runningTime, Application application) {
        this.runningTime = runningTime;
        this.application = application;
        this.dataStructure = application.dataStructure.getClass().getName();
    }

    public long getRunningTime() {
        return runningTime;
    }

    public Application getApplication() {
        return application;
    }

    public String getDataStructure() { return dataStructure; }
}
