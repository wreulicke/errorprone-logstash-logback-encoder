package com.github.wreulicke.errorprone.logstash;

import com.google.errorprone.CompilationTestHelper;
import org.junit.jupiter.api.Test;

class Slf4jDoNotUsePlaceholderTest {

  @Test
  void testValid() {
    CompilationTestHelper helper =
        CompilationTestHelper.newInstance(Slf4jDoNotUsePlaceholder.class, getClass());
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
                   // valid
                   logger.info("test");

                   // valid
                   logger.info(MARKER, "test");

                   // false positive but it's ok because this covers Slf4jFormatShouldBeConst
                   var message = "{}";
                   logger.info(message);
                 }
               }
               """)
        .doTest();
  }

  @Test
  public void test() {
    CompilationTestHelper helper =
        CompilationTestHelper.newInstance(Slf4jDoNotUsePlaceholder.class, getClass());
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
                   // BUG: Diagnostic contains: format should not contain placeholder. use structured argument instead.
                   logger.info(MARKER, "{}");

                   // BUG: Diagnostic contains: format should not contain placeholder. use structured argument instead.
                   logger.info("{}");

                   // BUG: Diagnostic contains: format should not contain placeholder. use structured argument instead.
                   logger.info("{}", "test");

                   // BUG: Diagnostic contains: format should not contain placeholder. use structured argument instead.
                   logger.info("{}", StructuredArguments.keyValue("key", "value"));

                   // BUG: Diagnostic contains: format should not contain placeholder. use structured argument instead.
                   logger.info("{}", "test", new Exception());
                 }
               }
               """)
        .doTest();
  }

  @Test
  void testFluentAPI() {
    CompilationTestHelper helper =
        CompilationTestHelper.newInstance(Slf4jDoNotUsePlaceholder.class, getClass());
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
                   // BUG: Diagnostic contains: format should not contain placeholder. use structured argument instead.
                   logger.atInfo().setMessage("{}").log();

                   // BUG: Diagnostic contains: format should not contain placeholder. use structured argument instead.
                   logger.atInfo().log("{}");
                 }
               }
               """)
        .doTest();
  }
}
