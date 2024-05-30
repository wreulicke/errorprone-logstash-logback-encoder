package com.github.wreulicke.errorprone.logstash;

import com.google.errorprone.CompilationTestHelper;
import org.junit.jupiter.api.Test;

class PlaceholderShouldNotContainStructuredArgumentTest {

  @Test
  void testSimple() {
    CompilationTestHelper helper =
        CompilationTestHelper.newInstance(
            PlaceholderShouldNotContainStructuredArgument.class, getClass());
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
            PlaceholderShouldNotContainStructuredArgument.class, getClass());
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
            PlaceholderShouldNotContainStructuredArgument.class, getClass());
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
            PlaceholderShouldNotContainStructuredArgument.class, getClass());
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
            PlaceholderShouldNotContainStructuredArgument.class, getClass());
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
            PlaceholderShouldNotContainStructuredArgument.class, getClass());
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
            PlaceholderShouldNotContainStructuredArgument.class, getClass());
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
            PlaceholderShouldNotContainStructuredArgument.class, getClass());
    helper
        .addSourceLines(
            "Test.java",
            """
               import org.slf4j.Logger;
               import net.logstash.logback.argument.StructuredArguments;
               public class Test {
                 private static final Logger logger = org.slf4j.LoggerFactory.getLogger(Test.class);

                 public void test() {
                   // BUG: Diagnostic contains: count of placeholders does not match with the count of arguments
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
            PlaceholderShouldNotContainStructuredArgument.class, getClass());
    helper
        .addSourceLines(
            "Test.java",
            """
               import org.slf4j.Logger;
               import net.logstash.logback.argument.StructuredArguments;
               public class Test {
                 private static final Logger logger = org.slf4j.LoggerFactory.getLogger(Test.class);

                 public void test() {
                   // BUG: Diagnostic contains: count of placeholders does not match with the count of arguments
                   logger.info("", StructuredArguments.keyValue("key", "value"), "safe", StructuredArguments.keyValue("key", "value"));
                 }
               }
               """)
        .doTest();
  }
}
