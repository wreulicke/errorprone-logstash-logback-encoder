# errorprone-logstash-logback-encoder

Detect misusages when you use slf4j and logstash-logback-encoder.

## Rule: PlaceholderShouldNotContainStructuredArgument

PlaceholderShouldNotContainStructuredArgument rule prevents placeholder contain StructuredArgument.
This also detects placeholder mismatch.

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

## License

MIT License
