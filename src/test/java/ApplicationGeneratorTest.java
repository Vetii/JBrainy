import org.junit.Assert;
import org.junit.Test;
import se.lth.cs.Application;
import se.lth.cs.ApplicationGeneration.ApplicationGenerator;
import se.lth.cs.ApplicationGeneration.ListApplicationGenerator;
import se.lth.cs.ApplicationGeneration.MapApplicationGenerator;
import se.lth.cs.ApplicationGeneration.SetApplicationGenerator;

import java.util.List;

public class ApplicationGeneratorTest {

    @Test
    public void testListApplicationGenerator() {
        ApplicationGenerator g = new ListApplicationGenerator();
        List<Application<?>> apps = g.createApplications(0, 1, 10);
        // Test all generated applications have the same seed
        for (Application<?> app : apps) {
            Assert.assertEquals(0, app.getSeed());
            Assert.assertEquals(10, app.getSize());
        }
        // Tests every type of lists is represented in the list of applications
        Assert.assertEquals("java.util.ArrayList", apps.get(0).getDataStructureName());
        Assert.assertEquals("java.util.LinkedList", apps.get(1).getDataStructureName());
        Assert.assertEquals("java.util.Vector", apps.get(2).getDataStructureName());
    }

    @Test
    public void testMapApplicationGenerator() {
        ApplicationGenerator g = new MapApplicationGenerator();
        List<Application<?>> apps = g.createApplications(0, 1, 10);
        // Test all generated applications have the same seed
        for (Application<?> app : apps) {
            Assert.assertEquals(0, app.getSeed());
            Assert.assertEquals(10, app.getSize());
        }
        Assert.assertEquals("java.util.HashMap", apps.get(0).getDataStructureName());
        Assert.assertEquals("java.util.TreeMap", apps.get(1).getDataStructureName());
        Assert.assertEquals("java.util.IdentityHashMap", apps.get(2).getDataStructureName());
        Assert.assertEquals("java.util.LinkedHashMap", apps.get(3).getDataStructureName());
        Assert.assertEquals("java.util.WeakHashMap", apps.get(4).getDataStructureName());
    }

    @Test
    public void testSetApplicationGenerator() {
        ApplicationGenerator g = new SetApplicationGenerator();
        List<Application<?>> apps = g.createApplications(0, 1, 10);
        // Test all generated applications have the same seed
        for (Application<?> app : apps) {
            Assert.assertEquals(0, app.getSeed());
            Assert.assertEquals(10, app.getSize());
        }
        Assert.assertEquals("java.util.HashSet", apps.get(0).getDataStructureName());
        Assert.assertEquals("java.util.TreeSet", apps.get(1).getDataStructureName());
        Assert.assertEquals("java.util.LinkedHashSet", apps.get(2).getDataStructureName());
        Assert.assertEquals("java.util.concurrent.ConcurrentSkipListSet", apps.get(3).getDataStructureName());
        Assert.assertEquals("java.util.concurrent.CopyOnWriteArraySet", apps.get(4).getDataStructureName());
    }
}
