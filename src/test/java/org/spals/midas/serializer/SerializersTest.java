package org.spals.midas.serializer;

import org.spals.midas.util.Tests;
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
