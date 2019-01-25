package se.lth.cs;

public class TrainingSetValue {

    private Double runningTime;

    private final Application application;

    private final String dataStructure;

    public TrainingSetValue(double runningTime, Application application) {
        this.runningTime = runningTime;
        this.application = application;
        this.dataStructure = application.dataStructure.getClass().getName();
    }

    public double getRunningTime() {
        return runningTime;
    }
    public void setRunningTime(double x) { runningTime = x; }

    public Application getApplication() {
        return application;
    }

    public String getDataStructure() { return dataStructure; }
}
