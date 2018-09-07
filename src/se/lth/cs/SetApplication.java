package se.lth.cs;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public class SetApplication  extends Application<Set<Integer>> {

    public SetApplication(int seed, String configuration, Set<Integer> set) {
        super(seed, configuration, set);
    }
}
