#![shaved wookiee](http://www.gravatar.com/avatar/62cf8eb12029b66dfa837efa365f12b4) swookiee* JVM REST Service Runtime 

[![Build Status](https://travis-ci.org/swookiee/com.swookiee.runtime.png?branch=develop)](https://travis-ci.org/swookiee/com.swookiee.runtime)

## What is swookiee*
swookiee is a shaved wookiee and looks like this: ![shaved wookiee](http://www.gravatar.com/avatar/62cf8eb12029b66dfa837efa365f12b4?s=40)

It basically is a JVM Runtime for REST Services. It is lightweight (~13 MB) and includes libraries like guava, joda-time, metrics, etc. It also supports service implementation written in Groovy and Scala (+7MB each). It uses a Jersey 2.5, Jetty, Jackson stack on top of the Equinox OSGi runtime to serve REST Services. swookiee also provides multiple REST APIs to deploy and control services and components via REST.

Our main goals are:
* Simplify exposing REST Services in the JVM World
* Polyglot: Solve problems in best fit language
* Reduce mandatory infrastructure and architectural knowledge from developers
* Define boundaries and APIs
* Enable deployment on artifact level (very simple OSGi [RFC-182 implementation](https://github.com/osgi/design/tree/master/rfcs/rfc0182))
* Increase transparency through direct metrics, graphite and JSON logging support

## Build \& start

Build & Start
```shell
cd com.swookiee.runtime
mvn clean install
cd com.swookiee.runtime
mvn exec:exec
```

## REST Services

To expose a REST service in swookiee you can describe the REST API in a simple interface:
```java
@Path("/hello")
@Produces(APPLICATION_JSON)
public interface HelloWorld {
    @GET
    String hello();
}
```

Through using the given [`@Component`](http://www.osgi.org/javadoc/r5/cmpn/org/osgi/service/component/annotations/Component.html) annotation the implementation of the interface will be exposed.
```java
@Component
public class HelloWorldService implements HelloWorld {
    @Override
    public String hello() {
        return "Hola!";
    }
}
```

A REST service will be exposed using the [OSGi - JAX-RS Connector](https://github.com/hstaudacher/osgi-jax-rs-connector). So every JAX-RS component exposed as a OSGi service will also be published via Jersey. A Jersey user guide can be found [here](https://jersey.java.net/documentation/latest)

## Filters, Exception Mapping, ...

In addition to exposing services you might need to register Filter (e.g. for CORS). This can be done via `ContainerResponseFilter` and `ContainerRequestFilter` implementations:

```java
@Component
@Provider
public class MyFilter implements ContainerResponseFilter {

    @Override
    public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext) {
    }
}
```

```java
@Component
@Provider
public class MyFilter implements ContainerRequestFilter {

    @Override
    public void filter(final ContainerRequestContext requestContext) {
    }
}
```

In order to register a exception mapper you have to implement the `ExceptionMapper`:
```java
@Component
@Provider
public class MyExceptionMapper implements ExceptionMapper<MyException> {

    @Override
    public Response toResponse(final MyException exception) {
        return Response.status(Status.INTERNAL_SERVER_ERROR).build();
    }
}

```

## JSON logging output
In case you have a logstash and kibana stack to analyze your logs you might want to use JSON as output format. To enable this you can set the property `productionLogging` to `true`.

## Pushing metrics to graphite
(Insert screenshots here)
Every published REST interface will be monitored via metrics. In addition basic JVM statistics will be published. In order to configure the graphite end point you can set the properties `graphiteHost` and `graphitePort`. By default port `2003` will be used for `graphitePort`.

## Deployment and runtime management via REST
[RFC-182](https://github.com/osgi/design/tree/master/rfcs/rfc0182)

## Configuration
Configuration is a crucial feature and we are working on it. Ideas are welcome!

## Archetype(s) & Tooling

### Eclipse

### Netbeans

## Todos
 * provide rpm, deb and other packages
 * Introduce Simple Configuration API
 * Support more JVM languages

## License
The code is published under the terms of the [Eclipse Public License, version 1.0](http://www.eclipse.org/legal/epl-v10.html).

\* derived from "shaved wookiee"
