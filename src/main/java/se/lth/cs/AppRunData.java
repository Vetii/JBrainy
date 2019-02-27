package se.lth.cs;

import java.util.List;
import java.util.stream.Collectors;

public class AppRunData {
    private List<Double> samples;
    protected Application<?> application;
    private Integer numberSamples;

    public AppRunData(Application<?> application, List<Double> samples) {
        this.application = application;
        this.samples = samples;
        this.numberSamples = samples.size();
    }

    public List<Double> getSamples() {
        return samples;
    }

    public Double getAverage() {
        return UtilsKt.average(samples);
    }

    public Double getVariance() {
        return UtilsKt.variance(samples);
    }

    public Double getStandardDeviation() {
        return Math.sqrt(getVariance());
    }

    public Application<?> getApplication() {
        return application;
    }

    public Integer getNumberSamples() {
        return numberSamples;
    }

    public Double getMedian() {
        return UtilsKt.median(samples);
    }

    public List<Double> cleanSamples() {
        List<Double> sorted = samples.stream().sorted().collect(Collectors.toList());
        Boolean even = numberSamples % 2 == 0;
        Double q1, q3, iqr, k;
        int i, j, h, l;
        i = numberSamples / 10;
        h = 9 * numberSamples / 10;
        if (even) {
            j = i + 1;
            l = h + 1;
            q1 = 0.5 * sorted.get(i) + sorted.get(j);
            q3 = 0.5 * sorted.get(h) + sorted.get(l);
        } else {
            q1 = sorted.get(i);
            q3 = sorted.get(h);
        }
        iqr = q3 - q1;
        k = 10.0;
        return samples.stream().filter(
                x -> x >= (q1) && x <= (q3)
        ).collect(Collectors.toList());
    }

    public List<Double> cleanSamples2() {
        Double average = getAverage();
        Double up = average + 2 * getStandardDeviation();
        Double down = average - 2 * getStandardDeviation();

        return samples.stream().filter(
                x -> x >= down && x <= up
        ).collect(Collectors.toList());
    }
}
