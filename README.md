# java-base-image
Java language support for Dispatch

Latest image [on Docker Hub](https://hub.docker.com/r/dispatchframework/java-base/): `dispatchframework/java-base:0.0.7`

## Usage

You need a recent version of Dispatch [installed in your Kubernetes cluster, Dispatch CLI configured](https://vmware.github.io/dispatch/documentation/guides/quickstart) to use it.

### Adding the Base Image

To add the base-image to Dispatch:
```bash
$ dispatch create base-image java-base dispatchframework/java-base:0.0.7
```

Make sure the base-image status is `READY` (it normally goes from `INITIALIZED` to `READY`):
```bash
$ dispatch get base-image java-base
```

### Adding Runtime Dependencies

Library dependencies listed in `pom.xml` ([maven dependency manifest](https://maven.apache.org/pom.html)) need to be wrapped into a Dispatch image. The `pom.xml` file must include the [minimal pom properties](https://maven.apache.org/guides/introduction/introduction-to-the-pom.html#Minimal_POM). For example, suppose we need a time library:

```bash
$ cat ./pom.xml
```
```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>io.dispatchframework.examples</groupId>
    <artifactId>hello-with-deps</artifactId>
    <version>1.0.0</version>

    <properties>
         <joda.version>2.3</joda.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>joda-time</groupId>
            <artifactId>joda-time</artifactId>
            <version>${joda.version}</version>
        </dependency>
    </dependencies>
</project>
```
```bash
$ dispatch create image java-mylibs java-base --runtime-deps ./pom.xml
```

Make sure the image status is `READY` (it normally goes from `INITIALIZED` to `READY`):
```bash
$ dispatch get image java-mylibs
```


### Creating Functions

Using the Java base-image, you can create Dispatch functions from Java source files. The file can require any libraries from the image (see above).

The only requirement is: a public class must be declared implementing the [**BiFunction interface**](https://docs.oracle.com/javase/8/docs/api/java/util/function/BiFunction.html) and must override the **`apply`** method, which accepts two arguments (`context` and `payload`), for example:  
```bash
$ cat ./Hello.java
```
```java
package io.dispatchframework.examples;

import java.util.Map;
import java.util.function.BiFunction;

import org.joda.time.DateTimeZone;

public class Hello implements BiFunction<Map<String, Object>, Map<String, Object>, String> {
    @Override
    public String apply(Map<String, Object> context, Map<String, Object> payload) {
        final Object name = payload.getOrDefault("name", "Someone");
        return String.format("Hello, %s from timezone %s", name, DateTimeZone.UTC);
    }
}
```

```bash
$ dispatch create function hello ./Hello.java --image=java-mylibs
    --handler=io.dispatchframework.examples.Hello
```

Make sure the function status is `READY` (it normally goes from `INITIALIZED` to `READY`):
```bash
$ dispatch get function hello
```

### Running Functions

As usual:

```bash
$ dispatch exec --json --input '{"name": "Jon"}' --wait hello
```
```json
{
    "blocking": true,
    "executedTime": 1524786004,
    "faasId": "f8287990-dec1-41d9-9d5a-fdfddffd53aa",
    "finishedTime": 1524786005,
    "functionId": "9e6f77cc-e345-4fcf-b33d-2075b8b122b2",
    "functionName": "hello",
    "input": {
        "name": "Jon"
    },
    "logs": {
        "stderr": null,
        "stdout": null
    },
    "name": "d9705a66-da41-4fb5-ac0c-418c1253ccbf",
    "output": "Hello, Jon from timezone UTC",
    "reason": null,
    "secrets": [],
    "services": null,
    "status": "READY",
    "tags": []
}
```

## Error Handling

There are three types of errors that can be thrown when invoking a function:
* `InputError`
* `FunctionError`
* `SystemError`

`SystemError` represents an error in the Dispatch infrastructure. `InputError` represents an error in the input detected either early in the function itself or through input schema validation. `FunctionError` represents an error in the function logic or an output schema validation error.

Functions themselves can either throw `InputError` or `FunctionError`

### Input Validation

For Java, the following exceptions thrown from the function are considered `InputError`:
* **`IllegalArgumentException`**

All other exceptions thrown from the function are considered `FunctionError`.

To validate input in the function body:
```java
package io.dispatchframework.javabaseimage;

import java.util.Map;
import java.util.function.BiFunction;

public class Lower implements BiFunction<Map<String, Object>, Map<String, Object>, String> {
    @Override
    public String apply(Map<String, Object> context, Map<String, Object> payload) {
        final Object name = payload.getOrDefault("name", "SOMEONE");

        if (name instanceof String) {
            return ((String) name).toLowerCase();
        } else {
            throw new IllegalArgumentException("name is not of type string");
        }
    }
}
```

### Note

Since **`IllegalArgumentException`** is considered an `InputError`, functions should not throw it unless explicitly thrown due to an input validation error. Functions should catch and handle **`IllegalArgumentException`** accordingly if it should not be classified as an `InputError`. 

## Building from source directory

To build a function from a source directory, the directory should follow the [maven directory structure](https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html):

```
/src/main/java/
/target/lib/
```

`/src/main/java` will hold the source code and `/target/lib` will hold the dependencies. For example, if you had a Hello class with a gson dependency:
```
/src/main/java/io/dispatchframework/examples/Hello.java
/target/lib/gson-2.8.2.jar
```

Suppose your project was contained in the directory `/my-project`:
```
/my-project/src/main/java/io/dispatchframework/examples/Hello.java
/my-project/target/lib/gson-2.8.2.jar
```

To create the function from the source directory:
```bash
dispatch create function hello /my-project --image=java 
  --handler=io.dispatchframework.examples.Hello
```

## Spring Support
The Java language base image now supports initialization of a Spring application context to support functions that rely on Spring framework components. For now this support relies on as few Spring components as possible to remain compatibile with as many Spring versions as possible. Further the base image supports choosing whether to start the application context based on the presence of Spring classes on the classpath. As long as the `org.springframework.beans.factory.BeanFactory` class is on the classpath, the function image will start an `AnnotationConfigApplicationContext`.

### Writing a function with Spring
First we will need to include the Spring dependencies when we create the image. For our example we will use this simple pom.xml

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>dispatchframework.examples</groupId>
  <artifactId>dispatch-spring</artifactId>
  <version>1.0.0</version>

  <properties>
    <spring.version>5.0.5.RELEASE</spring.version>
  </properties>

  <dependencies>
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-core</artifactId>
      <version>${spring.version}</version>
    </dependency>
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-context</artifactId>
      <version>${spring.version}</version>
    </dependency>
  </dependencies>
</project>
```

Let's pass these runtime dependencies to the creation of our image.
```bash
dispatch create image java-spring java-base --runtime-deps ./pom.xml
```

Now let's take a look at example Java function that uses Spring to wire dependencies into our function.
```java
package io.dispatchframework.examples;

import java.util.Map;
import java.util.function.BiFunction;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HelloSpring {


    @Bean(name = "noone")
    Person noone() {
        return new Person("Noone", "Nowhere");
    }

    @Bean
    BiFunction function(@Qualifier("noone") Person noone) {
        return new HelloSpringFunction(noone);
    }

    public class HelloSpringFunction implements BiFunction<Map<Object, Object>, Person, Result> {
        private Person defaultPerson;

        HelloSpringFunction(Person defaultPerson) {
            this.defaultPerson = defaultPerson;
        }

        @Override
        public Result apply(Map<Object, Object> context, Person person) {
            final String name = person.getName() == null ? defaultPerson.getName() : person.getName();
            final String place = person.getPlace() == null ? defaultPerson.getPlace() : person.getPlace();
            return new Result("Hello, " + name + " from " + place);
        }
    }

    private class Person {
        private String name;
        private String place;

        public Person(String name, String place) {
            this.name = name;
            this.place = place;
        }

        public String getName() {
            return this.name;
        }

        public String getPlace() {
            return this.place;
        }
    }

    private class Result {
        private String myField;

        public Result(String myField) {
            this.myField = myField;
        }

        public String getMyField() {
            return this.myField;
        }
    }
}
```

The important thing to note about this function file is that the top level class is annotated with @Configuration. This is required for Dispatch to register the beans defined within this file. The other important thing to note is that our support for Spring based functions expects a single bean of type `BiFunction` to be registered as a bean in the application context. In this above example this can be seen here:

```java
    @Bean
    BiFunction function(@Qualifier("noone") Person noone) {
        return new HelloSpringFunction(noone);
    }
```

To create this function we run
```bash
dispatch create function java-spring spring-fn ./HelloSpring.java
```

Again wait for the function status to show as `READY`
```bash
dispatch get function spring-fn
```

Finally we can execute this function the same way as above
```bash
$ dispatch exec --json --input '{"name": "Jon"}' --wait spring-fn
```