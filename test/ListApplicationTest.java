import org.junit.Assert;
import org.junit.Test;
import se.lth.cs.ListApplication;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;

public class ListApplicationTest {

    @Test
    public void TestApplication() {
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
}
