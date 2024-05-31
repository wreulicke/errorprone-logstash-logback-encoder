package io.github.wreulicke.errorprone.logstash;

import com.google.errorprone.CompilationTestHelper;
import org.junit.jupiter.api.Test;

class Slf4jFluentApiFormatShouldBeConstTest {

  @Test
  void test() {

    CompilationTestHelper helper =
        CompilationTestHelper.newInstance(Slf4jFluentApiFormatShouldBeConst.class, getClass());
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
                   // BUG: Diagnostic contains: format should be constant
                   logger.atInfo().setMessage(() -> "test").log();

                   // BUG: Diagnostic contains: format should be constant
                   logger.atInfo().log(() -> "test");

                   var message = "test";

                   // BUG: Diagnostic contains: format should be constant
                   logger.atInfo().log(message);

                   // BUG: Diagnostic contains: format should be constant
                   logger.atInfo().setMessage(message).log();

                   // valid
                   logger.atInfo().setMessage("safe").log();
                   // valid
                   logger.atInfo().log("safe");
                 }
               }
               """)
        .doTest();
  }
}
