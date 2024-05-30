# errorprone-logstash-logback-encoder

Detect misusages when you use slf4j and logstash-logback-encoder.

## Rule: PlaceholderShouldNotContainStructuredArgument

PlaceholderShouldNotContainStructuredArgument rule prevents placeholder contain StructuredArgument.

```java
import org.slf4j.Logger;
import net.logstash.logback.argument.StructuredArguments;
public class Test {
  private static final Logger logger = org.slf4j.LoggerFactory.getLogger(Test.class);

  public void test() {
    // BUG: Diagnostic contains: placeholder should not contain StructuredArgument
    logger.info("{}", StructuredArguments.keyValue("key", "value"));
  }
}
```

PlaceholderShouldNotContainStructuredArgument also detects placeholder mismatch.

```java
import org.slf4j.Logger;
import net.logstash.logback.argument.StructuredArguments;
public class Test {
  private static final Logger logger = org.slf4j.LoggerFactory.getLogger(Test.class);

  public void test() {
    // BUG: Diagnostic contains: count of placeholders does not match with the count of arguments
    logger.info("{}");
  }
}
```