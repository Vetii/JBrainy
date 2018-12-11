package test.java;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

public class ParsingTest {

    @Test
    public void TestTokenizingLine() {
        Assert.assertEquals(
                new HashMap<>(),
            se.lth.cs.perf.Parser.tokenizeLine("# Commented")
        );
    }

    @Test
    public void TestTokenizingLine2() {
        Assert.assertEquals(
                new HashMap<>(),
                se.lth.cs.perf.Parser.tokenizeLine("# Commented 3040")
        );
    }

    @Test
    public void TestTokenizingLine3() {
        Assert.assertEquals(
                new HashMap<>(),
                se.lth.cs.perf.Parser.tokenizeLine("Commented 3040")
        );
    }

    @Test
    public void TestTokenizingLine4() {
        HashMap<String, String> expected = new HashMap<>();
        expected.put("label", "L1-cache-misses");
        expected.put("value", "3040");
        Assert.assertEquals(
                expected,
                se.lth.cs.perf.Parser.tokenizeLine("3040 L1-cache-misses")
        );
    }

    @Test
    public void TestTokenizingLine5() {
        HashMap<String, String> expected = new HashMap<>();
        expected.put("label", "task-clock");
        expected.put("value", "764.589321");
        Assert.assertEquals(
                expected,
                se.lth.cs.perf.Parser.tokenizeLine("        764.589321      task-clock (msec)         #    1.942 CPUs utilized          \n")
        );
    }

    @Test
    public void TestSubDivide0() {
        ArrayList<ArrayList<String>> expected = new ArrayList<>();
        Assert.assertEquals(
                expected,
                se.lth.cs.perf.Parser.subDivide(new ArrayList<>())
        );
    }

    @Test
    public void TestSubDivide1() {
        ArrayList<String> data = new ArrayList<>();
        data.add("        764.589321      task-clock (msec)         #    1.942 CPUs utilized          \n");
        data.add("       0.382101616 seconds time elapsed\n");
        Assert.assertEquals(
                data,
                se.lth.cs.perf.Parser.subDivide(data).get(0)
        );
    }

    @Test
    public void TestSubDivide2() {
        ArrayList<String> data = new ArrayList<>();
        data.add("        764.589321      task-clock (msec)         #    1.942 CPUs utilized          \n");
        data.add("              1756      context-switches          #    0.002 M/sec                  \n");
        data.add("       0.382101616 seconds time elapsed\n");
        Assert.assertEquals(
                data,
                se.lth.cs.perf.Parser.subDivide(data).get(0)
        );
    }

    @Test
    public void TestSubDivide3() {
        ArrayList<String> data = new ArrayList<>();
        data.add("        764.589321      task-clock (msec)         #    1.942 CPUs utilized          \n");
        data.add("              1756      context-switches          #    0.002 M/sec                  \n");
        data.add("  ");
        data.add("       0.382101616 seconds time elapsed\n");
        data.add("  ");
        data.add("        764.589321      task-clock (msec)         #    1.942 CPUs utilized          \n");
        data.add("              1756      context-switches          #    0.002 M/sec                  \n");
        data.add("       0.382101616 seconds time elapsed\n");

        ArrayList<ArrayList<String>> expected = new ArrayList<>();
        expected.add(new ArrayList<>());
        expected.get(0).add("        764.589321      task-clock (msec)         #    1.942 CPUs utilized          \n");
        expected.get(0).add("              1756      context-switches          #    0.002 M/sec                  \n");
        expected.get(0).add("       0.382101616 seconds time elapsed\n");

        expected.add(new ArrayList<>());
        expected.get(1).add("        764.589321      task-clock (msec)         #    1.942 CPUs utilized          \n");
        expected.get(1).add("              1756      context-switches          #    0.002 M/sec                  \n");
        expected.get(1).add("       0.382101616 seconds time elapsed\n");

        Assert.assertEquals(
                expected,
                se.lth.cs.perf.Parser.subDivide(data)
        );
    }
}
