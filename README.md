# errorprone-logstash-logback-encoder

Detect misuses when you use slf4j and logstash-logback-encoder.

This module provides custom Error Prone checks for slf4j and logstash-logback-encoder.
This module doesn't cover all situations in slf4j misuses.
So, also use [errorprone-slf4j] to detect more slf4j misuses.

## Install

### Maven

TBD

### Gradle

```ruby
dependencies {
  annotationProcessor 'io.github.wreulicke.errorprone.logstash:errorprone-logback-logstash-encoder:0.0.1'
  # or you can write below when you use net.ltgt.errorprone plugin
  errorprone 'io.github.wreulicke.errorprone.logstash:errorprone-logback-logstash-encoder:0.0.1'
}
```

## Rules

- [Slf4jPlaceholderShouldNotContainStructuredArgument](#slf4jplaceholdershouldnotcontainstructuredargument)
- [Slf4jDoNotUsePlaceholder](#slf4jdonotuseplaceholder)
- [Slf4jConstantMarkerMutation](#slf4jconstantmarkermutation)
- [Slf4jFluentApiFormatShouldBeConst](#slf4jfluentapiformatshouldbeconst)

### Slf4jPlaceholderShouldNotContainStructuredArgument

Slf4jPlaceholderShouldNotContainStructuredArgument rule prevents placeholder contain StructuredArgument.
This also detects placeholder mismatch.

This rule conflicts with Slf4jPlaceholderMismatch in [errorprone-slf4j].
So you should disable the Slf4jPlaceholderMismatch rule if you use this rule.

```java
import org.slf4j.Logger;
import net.logstash.logback.argument.StructuredArguments;
public class Test {
  private static final Logger logger = org.slf4j.LoggerFactory.getLogger(Test.class);

  public void test() {
    // invalid: placeholder should not contain StructuredArgument
    logger.info("{}", StructuredArguments.keyValue("key", "value"));

    // invalid: count of placeholders does not match with the count of arguments
    logger.info("{}");

    // valid
    logger.info("{}", "safe", StructuredArguments.keyValue("key", "value"));
  }
}
```

### Slf4jDoNotUsePlaceholder

Slf4jDoNotUsePlaceholder rule prevents using placeholder in log message.
This rule is stricter than Slf4jSignOnlyFormat in [errorprone-slf4j].

```java
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import net.logstash.logback.argument.StructuredArguments;
public class Test {
 private static final Logger logger = org.slf4j.LoggerFactory.getLogger(Test.class);
 private static final Marker MARKER = MarkerFactory.getMarker("marker");

 public void test() {
   // invalid: format should not contain placeholder. use structured argument instead.
   logger.info("some message. userId:{}", "foo");

   // valid
   logger.info("some message", StructuredArguments.keyValue("userId", "foo"));
 }
}
```

### Slf4jConstantMarkerMutation

Slf4jConstantMarkerMutation rule prevents mutation of constant marker.

```java
import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;
public class Test {
 private static final LogstashMarker MARKER = Markers.append("key", "value");

 public void test() {
   // invalid: Constant marker should not mutate, or use net.logstash.logback.marker.Markers.aggregate instead
   MARKER.add(Markers.append("key", "value"));
   
   // valid
   Markers.aggregate(MARKER, Markers.append("key", "value"));
 }
}
```

### Slf4jFluentApiFormatShouldBeConst

Slf4jFluentApiFormatShouldBeConst rule prevents using non-constant format in fluent API.
This rule covers Slf4jFormatShouldBeConst in [errorprone-slf4j].

```java
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import net.logstash.logback.argument.StructuredArguments;
public class Test {
 private static final Logger logger = org.slf4j.LoggerFactory.getLogger(Test.class);
 private static final Marker MARKER = MarkerFactory.getMarker("marker");

 public void test() {
   // invalid: format should be constant
   logger.atInfo().setMessage(() -> "test").log();

   // invalid: format should be constant
   logger.atInfo().log(() -> "test");

   var message = "test";
   // invalid: format should be constant
   logger.atInfo().log(message);

   // invalid: format should be constant
   logger.atInfo().setMessage(message).log();

   // valid
   logger.atInfo().setMessage("safe").log();

   // valid
   logger.atInfo().log("safe");
 }
}
```

## TODO

- Suggested Fix for Slf4jConstantMarkerMutation


## License

MIT License

[errorprone-slf4j]: https://github.com/KengoTODA/errorprone-slf4j