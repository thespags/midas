package org.spals.midas.serializers;

public class PerfTest {

    public void testFoo() {
        final Integer[] i = new Integer[10000000];
        for (int j = 0; j < i.length; j++) {
            i[j] = j;
        }
        long total = 0;
        long ptotal = 0;
        for (int k = 0; k < 10; k++) {
            final SerializerMap map = SerializerMap.make().putJava();

            long start;
            start = System.currentTimeMillis();
            new ArraySerializer<>(map).serialize(i);
            long finish;
            finish = System.currentTimeMillis() - start;
            total += finish;
            System.err.println("ARRAY:" + finish);

            start = System.currentTimeMillis();
            new PrimitiveArraySerializer(map).serialize(i);
            finish = System.currentTimeMillis() - start;
            ptotal += finish;
            System.err.println("PARRAY:" + finish);
        }
        System.err.println("a= " + (total / 10));
        System.err.println("p= " + (ptotal / 10));
    }
}
