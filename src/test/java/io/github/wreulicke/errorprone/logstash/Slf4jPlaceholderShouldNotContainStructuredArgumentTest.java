package io.github.wreulicke.errorprone.logstash;

import com.google.errorprone.CompilationTestHelper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class Slf4jPlaceholderShouldNotContainStructuredArgumentTest {

  @Test
  void testValid() {
    CompilationTestHelper helper =
        CompilationTestHelper.newInstance(
            Slf4jPlaceholderShouldNotContainStructuredArgument.class, getClass());
    helper
        .addSourceLines(
            "Test.java",
            """
               import org.slf4j.Logger;
               import net.logstash.logback.argument.StructuredArguments;
               public class Test {
                 private static final Logger logger = org.slf4j.LoggerFactory.getLogger(Test.class);

                 public void test() {
                   // false positive but it's ok because it covers Slf4jFormatShouldBeConst
                   var message = "{}";
                   logger.info(message, StructuredArguments.keyValue("key", "value"));
                   // valid
                   logger.info("safe");
                   // valid
                   logger.info("safe", new Exception());
                 }
               }
               """)
        .doTest();
  }

  @Test
  void testSimple() {
    CompilationTestHelper helper =
        CompilationTestHelper.newInstance(
            Slf4jPlaceholderShouldNotContainStructuredArgument.class, getClass());
    helper
        .addSourceLines(
            "Test.java",
            """
               import org.slf4j.Logger;
               import net.logstash.logback.argument.StructuredArguments;
               public class Test {
                 private static final Logger logger = org.slf4j.LoggerFactory.getLogger(Test.class);

                 public void test() {
                   // BUG: Diagnostic contains: placeholder should not contain StructuredArgument
                   logger.info("{}", StructuredArguments.keyValue("key", "value"));
                 }
               }
               """)
        .doTest();
  }

  @Test
  void testCorrect() {
    CompilationTestHelper helper =
        CompilationTestHelper.newInstance(
            Slf4jPlaceholderShouldNotContainStructuredArgument.class, getClass());
    helper
        .addSourceLines(
            "Test.java",
            """
               import org.slf4j.Logger;
               import net.logstash.logback.argument.StructuredArguments;
               public class Test {
                 private static final Logger logger = org.slf4j.LoggerFactory.getLogger(Test.class);

                 public void test() {
                   logger.info("{}", "safe", StructuredArguments.keyValue("key", "value"));
                 }
               }
               """)
        .doTest();
  }

  @Test
  void testLastArgumentIsStructuredArgument() {
    CompilationTestHelper helper =
        CompilationTestHelper.newInstance(
            Slf4jPlaceholderShouldNotContainStructuredArgument.class, getClass());
    helper
        .addSourceLines(
            "Test.java",
            """
               import org.slf4j.Logger;
               import net.logstash.logback.argument.StructuredArguments;
               public class Test {
                 private static final Logger logger = org.slf4j.LoggerFactory.getLogger(Test.class);

                 public void test() {
                   // BUG: Diagnostic contains: placeholder should not contain StructuredArgument
                   logger.info("{} {}", "safe", StructuredArguments.keyValue("key", "value"));
                 }
               }
               """)
        .doTest();
  }

  @Test
  void testMiddleArgumentIsStructuredArgument() {
    CompilationTestHelper helper =
        CompilationTestHelper.newInstance(
            Slf4jPlaceholderShouldNotContainStructuredArgument.class, getClass());
    helper
        .addSourceLines(
            "Test.java",
            """
               import org.slf4j.Logger;
               import net.logstash.logback.argument.StructuredArguments;
               public class Test {
                 private static final Logger logger = org.slf4j.LoggerFactory.getLogger(Test.class);

                 public void test() {
                   // BUG: Diagnostic contains: placeholder should not contain StructuredArgument
                   logger.info("{} {} {}", "safe", StructuredArguments.keyValue("key", "value"), "safe");
                 }
               }
               """)
        .doTest();
  }

  @Test
  void testMarker() {
    CompilationTestHelper helper =
        CompilationTestHelper.newInstance(
            Slf4jPlaceholderShouldNotContainStructuredArgument.class, getClass());
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
                   // BUG: Diagnostic contains: placeholder should not contain StructuredArgument
                   logger.info(MARKER, "{}", StructuredArguments.keyValue("key", "value"));
                 }
               }
               """)
        .doTest();
  }

  @Test
  void testThrowable() {
    CompilationTestHelper helper =
        CompilationTestHelper.newInstance(
            Slf4jPlaceholderShouldNotContainStructuredArgument.class, getClass());
    helper
        .addSourceLines(
            "Test.java",
            """
               import org.slf4j.Logger;
               import net.logstash.logback.argument.StructuredArguments;
               public class Test {
                 private static final Logger logger = org.slf4j.LoggerFactory.getLogger(Test.class);

                 public void test() {
                   logger.info("{}", "safe", StructuredArguments.keyValue("key", "value"), new Exception());
                 }
               }
               """)
        .doTest();
  }

  @Test
  void testMarkerAndThrowable() {
    CompilationTestHelper helper =
        CompilationTestHelper.newInstance(
            Slf4jPlaceholderShouldNotContainStructuredArgument.class, getClass());
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
                   logger.info(MARKER, "{}", "safe", StructuredArguments.keyValue("key", "value"), new Exception());
                 }
               }
               """)
        .doTest();
  }

  @Test
  void testMismatch() {
    CompilationTestHelper helper =
        CompilationTestHelper.newInstance(
            Slf4jPlaceholderShouldNotContainStructuredArgument.class, getClass());
    helper
        .addSourceLines(
            "Test.java",
            """
               import org.slf4j.Logger;
               import net.logstash.logback.argument.StructuredArguments;
               public class Test {
                 private static final Logger logger = org.slf4j.LoggerFactory.getLogger(Test.class);

                 public void test() {
                   // BUG: Diagnostic contains: count of placeholders does not match with the count of arguments without StructuredArgument
                   logger.info("{}");
                 }
               }
               """)
        .doTest();
  }

  @Test
  void testMismatch2() {
    CompilationTestHelper helper =
        CompilationTestHelper.newInstance(
            Slf4jPlaceholderShouldNotContainStructuredArgument.class, getClass());
    helper
        .addSourceLines(
            "Test.java",
            """
               import org.slf4j.Logger;
               import net.logstash.logback.argument.StructuredArguments;
               public class Test {
                 private static final Logger logger = org.slf4j.LoggerFactory.getLogger(Test.class);

                 public void test() {
                   // BUG: Diagnostic contains: count of placeholders does not match with the count of arguments without StructuredArgument
                   logger.info("", StructuredArguments.keyValue("key", "value"), "safe", StructuredArguments.keyValue("key", "value"));
                 }
               }
               """)
        .doTest();
  }

  @Nested
  class Fluent {

    @Test
    void test() {
      CompilationTestHelper helper =
          CompilationTestHelper.newInstance(
              Slf4jPlaceholderShouldNotContainStructuredArgument.class, getClass());
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
                       // BUG: Diagnostic contains: placeholder should not contain StructuredArgument
                       logger.atInfo().log("{}", StructuredArguments.keyValue("key", "value"));

                       // false positive
                       logger.atInfo().setMessage("{}").addArgument(StructuredArguments.keyValue("key", "value")).log();
                     }
                 }
                 """)
          .doTest();
    }
  }
}
