import org.junit.Assert;
import org.junit.Test;
import se.lth.cs.Application;
import se.lth.cs.ApplicationGeneration.ApplicationGenerator;
import se.lth.cs.ApplicationGeneration.ListApplicationGenerator;

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
        Assert.assertEquals(apps.get(0).getDataStructureName(), "java.util.ArrayList");
        Assert.assertEquals(apps.get(1).getDataStructureName(), "java.util.LinkedList");
        Assert.assertEquals(apps.get(2).getDataStructureName(), "java.util.Vector");
    }
}
