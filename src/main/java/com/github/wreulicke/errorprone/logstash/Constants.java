package com.github.wreulicke.errorprone.logstash;

import com.google.errorprone.matchers.Matcher;
import com.google.errorprone.matchers.Matchers;
import com.google.errorprone.matchers.method.MethodMatchers;
import com.sun.source.tree.ExpressionTree;

import java.util.regex.Pattern;

import static com.google.errorprone.matchers.Matchers.isSubtypeOf;

final class Constants {

  static final Matcher<ExpressionTree> IS_MARKER = isSubtypeOf("org.slf4j.Marker");
  private static final String FQCN_SLF4J_LOGGER = "org.slf4j.Logger";

  private static final String FQCN_SLF4J_FLUENT_API = "org.slf4j.spi.LoggingEventBuilder";
  private static final String FQCN_STRUCTURED_ARGUMENT =
      "net.logstash.logback.argument.StructuredArgument";

  static final Matcher<ExpressionTree> LOGGING_METHOD = Matchers.anyOf(
      MethodMatchers.instanceMethod()
          .onDescendantOf(FQCN_SLF4J_FLUENT_API)
          .named("log"),
      MethodMatchers.instanceMethod()
          .onDescendantOf(FQCN_SLF4J_LOGGER)
          .withNameMatching(Pattern.compile("info|debug|trace|warn|error")));

  static final Matcher<ExpressionTree> STRUCTURED_ARGUMENT =
      com.google.errorprone.matchers.Matchers.isSubtypeOf(FQCN_STRUCTURED_ARGUMENT);
  static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{\\}");

  private Constants() {
    throw new UnsupportedOperationException();
  }
}
