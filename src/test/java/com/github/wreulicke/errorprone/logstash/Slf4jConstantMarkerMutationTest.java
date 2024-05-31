package com.github.wreulicke.errorprone.logstash;

import com.google.errorprone.CompilationTestHelper;
import org.junit.jupiter.api.Test;

class Slf4jConstantMarkerMutationTest {

  @Test
  void testAdd() {
    CompilationTestHelper helper =
        CompilationTestHelper.newInstance(Slf4jConstantMarkerMutation.class, getClass());
    helper
        .addSourceLines(
            "Test.java",
            """
               import net.logstash.logback.marker.LogstashMarker;
               import net.logstash.logback.marker.Markers;
               public class Test {
                 private static final LogstashMarker marker = Markers.append("key", "value");

                 public void test() {
                   // BUG: Diagnostic contains: Constant marker should not mutate, or use net.logstash.logback.marker.Markers.aggregate instead
                   marker.add(Markers.append("key", "value"));
                 }
               }
               """)
        .doTest();
  }

  @Test
  void testWith() {
    CompilationTestHelper helper =
        CompilationTestHelper.newInstance(Slf4jConstantMarkerMutation.class, getClass());
    helper
        .addSourceLines(
            "Test.java",
            """
               import net.logstash.logback.marker.LogstashMarker;
               import net.logstash.logback.marker.Markers;
               public class Test {
                 private static final LogstashMarker marker = Markers.append("key", "value");

                 public void test() {
                   // BUG: Diagnostic contains: Constant marker should not mutate, or use net.logstash.logback.marker.Markers.aggregate instead
                   marker.with(Markers.append("key", "value"));
                 }
               }
               """)
        .doTest();
  }

  @Test
  void testInitialization() {
    CompilationTestHelper helper =
        CompilationTestHelper.newInstance(Slf4jConstantMarkerMutation.class, getClass());
    helper
        .addSourceLines(
            "Test.java",
            """
               import net.logstash.logback.marker.LogstashMarker;
               import net.logstash.logback.marker.Markers;
               public class Test {
                 private static final LogstashMarker marker = Markers.append("key", "value").with(Markers.append("key", "value"));
               }
               """)
        .doTest();
  }
}
