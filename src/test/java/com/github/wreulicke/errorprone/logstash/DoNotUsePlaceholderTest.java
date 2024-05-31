package com.github.wreulicke.errorprone.logstash;

import com.google.errorprone.CompilationTestHelper;
import org.junit.jupiter.api.Test;

class DoNotUsePlaceholderTest {

  @Test
  public void test() {
    CompilationTestHelper helper =
        CompilationTestHelper.newInstance(
            DoNotUsePlaceholder.class, getClass());
    helper
        .addSourceLines(
            "Test.java",
            """
               import org.slf4j.Logger;
               import org.slf4j.Marker;
               import org.slf4j.MarkerFactory;
               import net.logstash.logback.argument.StructuredArguments;
               public class Test {
                 private static final Logger logger = org.slf4j.LoggerFactory.getLogger(Test.class);
                 private static final Marker MARKER = MarkerFactory.getMarker("marker");

                 public void test() {
                   // BUG: Diagnostic contains: logging message should not contain placeholder. use structured argument instead.
                   logger.info(MARKER, "{}");
                   
                   // BUG: Diagnostic contains: logging message should not contain placeholder. use structured argument instead.
                   logger.info("{}");
                   
                   // BUG: Diagnostic contains: logging message should not contain placeholder. use structured argument instead.
                   logger.info("{}", "test");
                   
                   // BUG: Diagnostic contains: logging message should not contain placeholder. use structured argument instead.
                   logger.info("{}", StructuredArguments.keyValue("key", "value"));
                   
                   // BUG: Diagnostic contains: logging message should not contain placeholder. use structured argument instead.
                   logger.info("{}", "test", new Exception());
                   
                   logger.info(MARKER, "safe", "{}");
                 }
               }
               """)
        .doTest();
  }
}
