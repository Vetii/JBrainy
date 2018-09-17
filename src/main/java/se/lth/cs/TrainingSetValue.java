package se.lth.cs;

public class TrainingSetValue {

    private final Long runningTime;

    private final Application application;

    public TrainingSetValue(long runningTime, Application dataStructure) {
        this.runningTime = runningTime;
        this.application = dataStructure;
    }

    public long getRunningTime() {
        return runningTime;
    }

    public Application getApplication() {
        return application;
    }
}
