package org.spals.midas.serializers;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.spals.midas.serializers.ByteArrayMatcher.isBytes;

/**
 * TODO test for default serializer
 * TODO test for null
 */
public class ReflectionSerializerTest {
    @Test
    public void testSerialize() {
        final byte[] actual = ReflectionSerializer.builder()
            .registerJava()
            .build()
            .serialize(new Foo());
        final String expected =
            "littleInt = 0\n" +
                "bigInt = 1\n" +
                "littleChar = \u0002\n" +
                "bigChar = \u0003\n" +
                "littleLong = 4\n" +
                "bigLong = 5\n" +
                "littleDouble = 6.0\n" +
                "bigDouble = 6.0\n" +
                "littleFloat = 7.0\n" +
                "bigFloat = 8.0\n" +
                "littleShort = 9\n" +
                "bigShort = 10\n" +
                "littleBoolean = true\n" +
                "bigBoolean = false\n" +
                "string = foo\n" +
                "intArray = [1, 3, 5]\n" +
                "stringSet = [a, b, c]\n" +
                "intSet = {4, 2, 6}\n" +
                "map = (foo->1)\n";
        assertThat(actual, isBytes(expected));
    }

    @SuppressWarnings({"unused", "MismatchedReadAndWriteOfArray"})
    private static class Foo {
        private int littleInt = 0;
        private Integer bigInt = 1;
        private char littleChar = 2;
        private Character bigChar = 3;
        private long littleLong = 4;
        private Long bigLong = 5L;
        private double littleDouble = 6;
        private Double bigDouble = 6.0;
        private float littleFloat = 7;
        private Float bigFloat = 8F;
        private short littleShort = 9;
        private Short bigShort = 10;
        private boolean littleBoolean = true;
        private Boolean bigBoolean = false;
        private String string = "foo";

        private int[] intArray = new int[]{1, 3, 5};
        private List<String> stringSet = Lists.newArrayList("a", "b", "c");
        private Set<Integer> intSet = Sets.newHashSet(2, 4, 6);
        private Map<String, Integer> map = ImmutableMap.of("foo", 1);
    }
}