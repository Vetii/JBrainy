package se.lth.cs;

public class TrainingSetValue {

    private Double runningTime;

    private final Application application;

    private final String dataStructure;

    private final String bestDataStructure;

    public TrainingSetValue(double runningTime, Application application, String bestDataStructure) {
        this.runningTime = runningTime;
        this.application = application;
        this.dataStructure = application.dataStructure.getClass().getName();
        this.bestDataStructure = bestDataStructure;
    }

    public double getRunningTime() {
        return runningTime;
    }
    public void setRunningTime(double x) { runningTime = x; }

    public Application getApplication() {
        return application;
    }

    public String getDataStructure() { return dataStructure; }

    public String getBestDataStructure() {
        return bestDataStructure;
    }
}
