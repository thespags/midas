package org.spals.midas.serializers;

import com.google.common.collect.ImmutableMap;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;


import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

// TODO make this better...
public class ReflectionSerializerTest {
    @Test
    public void foo() {
        String bytes = Converter.fromUtf8(
            ReflectionSerializer.builder()
                .register(int[].class, Serializers.INTEGER_ARRAY)
                .registerPrimitives()
                .build()
                .serialize(new Foo())
        );
        String temp = "i = [1, 3, 5]\n" +
            "i2 = 5\n" +
            "i3 = 6\n" +
            "map = (foo->1)\n";
        assertThat(bytes, is(temp));
    }

    private static class Foo {
        //private String foo = "null";
        private int[] i = new int[] {1,3,5};
        private int i2 = 5;
        private Integer i3 = 6;
        private Map<String, Integer> map = ImmutableMap.of("foo", 1);

        public Foo() {
        }

    }
}