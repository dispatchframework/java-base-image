# java-base-image
Java language support for Dispatch

Latest image [on Docker Hub](https://hub.docker.com/r/dispatchframework/java8-base/): `dispatchframework/java8-base:0.0.2`

## Usage

You need a recent version of Dispatch [installed in your Kubernetes cluster, Dispatch CLI configured](https://vmware.github.io/dispatch/documentation/guides/quickstart) to use it.

### Adding the Base Image

To add the base-image to Dispatch:
```bash
$ dispatch create base-image java8-base dispatchframework/java8-base:0.0.2
```

Make sure the base-image status is `READY` (it normally goes from `INITIALIZED` to `READY`):
```bash
$ dispatch get base-image java8-base
```

### Adding Runtime Dependencies

Library dependencies listed in `pom.xml` ([maven dependency manifest](https://maven.apache.org/pom.html)) need to be wrapped into a Dispatch image. The `pom.xml` file must include the [minimal pom properties](https://maven.apache.org/guides/introduction/introduction-to-the-pom.html#Minimal_POM). For example, suppose we need a time library:

```bash
$ cat ./pom.xml
```
```xml
<project>
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
$ dispatch create image java8-mylibs java8-base --runtime-deps ./pom.xml
```

Make sure the image status is `READY` (it normally goes from `INITIALIZED` to `READY`):
```bash
$ dispatch get image java8-mylibs
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
$ dispatch create function java8-mylibs hello ./Hello.java
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
