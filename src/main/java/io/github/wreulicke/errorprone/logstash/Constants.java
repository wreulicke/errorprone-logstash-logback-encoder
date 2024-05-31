package io.github.wreulicke.errorprone.logstash;

import static com.google.errorprone.matchers.Matchers.isSubtypeOf;

import com.google.errorprone.matchers.Matcher;
import com.google.errorprone.matchers.Matchers;
import com.google.errorprone.matchers.method.MethodMatchers;
import com.sun.source.tree.ExpressionTree;
import java.util.regex.Pattern;

final class Constants {

  static final Matcher<ExpressionTree> IS_MARKER = isSubtypeOf("org.slf4j.Marker");
  private static final String FQCN_SLF4J_LOGGER = "org.slf4j.Logger";

  private static final String FQCN_SLF4J_FLUENT_API = "org.slf4j.spi.LoggingEventBuilder";

  static final Matcher<ExpressionTree> FLUENT_API_LOG =
      MethodMatchers.instanceMethod().onDescendantOf(FQCN_SLF4J_FLUENT_API).named("log");

  static final Matcher<ExpressionTree> LOGGING_METHOD =
      Matchers.anyOf(
          FLUENT_API_LOG,
          MethodMatchers.instanceMethod()
              .onDescendantOf(FQCN_SLF4J_LOGGER)
              .withNameMatching(Pattern.compile("info|debug|trace|warn|error")));

  static final Matcher<ExpressionTree> FLUENT_API_SET_MESSAGE =
      MethodMatchers.instanceMethod().onDescendantOf(FQCN_SLF4J_FLUENT_API).named("setMessage");

  static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{\\}");

  private Constants() {
    throw new UnsupportedOperationException();
  }
}
