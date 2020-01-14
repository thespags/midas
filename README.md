[![Build Status](https://travis-ci.org/spals/midas.svg?branch=master)](https://travis-ci.org/spals/midas)

# midas

A gold file testing/approval testing/golden master framework for testing your code. Gold file testing is a simple approach to testing with expected output in a version controlled file. Gold file testing is a light weight approach to contract testing. 

Midas is a framework that does the dirty work of file management for your tests while you can focus on writing your code. Midas is powerful and allows you to write custom serialization against objects, or use passed in Strings.

### Installation

midas requires Java 8 and is available via Maven. You may also [build it from source](#building_from_source).

```xml
<dependency>
    <groupId>net.spals.midas</groupId>
    <artifactId>midas</artifactId>
</dependency>
```

## Quick Start

In a maven build you could write
```java
GoldFile.builder()
    .withPath(GoldPaths.simpleClass(GoldPaths.MAVEN, MyTestClass.class))
    .build()
    .run(myObject, "MyGoldFileTest");
```
which would compare the toString otuput of myObject with the results of MyGoldFileTest. 
The results would be stored in src/test/resource/MyGoldFileTest.midas.

If you were running testng you could specify your path based on the test name
```java
private Path methodPath;

@BeforeMethod
public void setUp(final Method method) {
    methodPath = Paths.get(method.getName());
}

@Test
public void testMyObject() {
    GoldFile.builder()
        .withPath(GoldPaths.simpleClass(GoldPaths.MAVEN, MyTestClass.class))
        .build()
        .run(myObject, methodPath);
}
```

If your object was some non trivial type without a well defined toString, i.e. not serializable
you can you use the built in reflection serializer.
```java
GoldFile.builder()
    .withSerializer(input -> "foobar")
    .build()
```

If you need to change a specific serialization embedded deep inside you can use the serializer registry with typed serializer.
```java
SerializerRegistry registry = SerializerRegistry.newDefault();
registry.put(Foobar.class, input -> "foobar");
GoldFile.builder()
    .withSerializer(Serializers.newToString(registry))
    .build()
```

## <a name="building_from_source"></a> Building From Source

Prerequisites:

- Java 8+
- Maven 3.3.x+

midas follows standard Maven practices:

- Run tests and build local JAR: `mvn package`
- Build local JAR without tests: `mvn package -DskipTests`
- Install JAR into local Maven repository: `mvn install`

## License

midas is licensed under the BSD-3 License. See
[LICENSE](https://github.com/spals/midas/blob/master/LICENSE) for the full
license text.
