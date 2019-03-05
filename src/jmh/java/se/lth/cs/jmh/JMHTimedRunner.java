package se.lth.cs.jmh;

import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;

public class JMHTimedRunner extends Runner {

    private Duration duration;

    public JMHTimedRunner(Options options) {
        super(options);
    }

    public Collection<RunResult> runWithTime() throws RunnerException {
        Instant before = Instant.now();
        var result = run();
        Instant after = Instant.now();
        duration = Duration.between(before, after);
        return result;
    }

    public Duration getDuration() {
        return duration;
    }
}