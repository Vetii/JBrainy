import org.junit.Assert;
import org.junit.Test;
import se.lth.cs.ListApplication;
import se.lth.cs.SetApplication;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ApplicationTest {

    @Test
    public void TestListApplication() {
        try {
            for (int seed = 0; seed < 100; ++seed) {
                ListApplication arrayListBench = new ListApplication(
                        seed,
                        "",
                        new ArrayList<>());
                ListApplication linkedListBench = new ListApplication(
                        seed,
                        "",
                        new LinkedList<>()
                );
                ListApplication vectorBench = new ListApplication(
                        seed,
                        "",
                        new Vector<>()
                );
                arrayListBench.benchmark();
                linkedListBench.benchmark();
                vectorBench.benchmark();
                Assert.assertEquals(
                        arrayListBench.getDataStructure(),
                        linkedListBench.getDataStructure()
                );
                Assert.assertEquals(
                        arrayListBench.getDataStructure(),
                        vectorBench.getDataStructure()
                );
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestSetApplication() {
        try {
            for (int seed = 0; seed < 100; ++seed) {
                SetApplication hashSetBench = new SetApplication(seed, "", new HashSet<>());

                SetApplication treeSetBench = new SetApplication(seed, "", new TreeSet<>());

                hashSetBench.benchmark();
                treeSetBench.benchmark();
                Assert.assertEquals(
                        hashSetBench.getDataStructure(),
                        treeSetBench.getDataStructure()
                );
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

    }
}
