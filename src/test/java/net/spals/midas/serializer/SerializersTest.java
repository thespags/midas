package net.spals.midas.serializer;

import net.spals.midas.util.Tests;
import org.testng.annotations.Test;

/**
 * @author spags
 */
public class SerializersTest {

    @Test
    public void testPrivate() throws Exception {
        Tests.testPrivate(Serializers.class);
    }
}
