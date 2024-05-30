package com.github.wreulicke.errorprone.logstash;

import static com.google.errorprone.matchers.Matchers.isSubtypeOf;

import com.google.auto.service.AutoService;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.matchers.Matcher;
import com.google.errorprone.matchers.method.MethodMatchers;
import com.google.errorprone.util.ASTHelpers;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import java.util.List;
import java.util.regex.Pattern;

@AutoService(BugChecker.class)
@BugPattern(
    summary = "placeholder should not contain structured argument",
    severity = BugPattern.SeverityLevel.ERROR,
    link = "github.com/wreulicke/errorprone-logback-logstash-encoder",
    linkType = BugPattern.LinkType.CUSTOM)
public class PlaceholderShouldNotContainStructuredArgument extends BugChecker
    implements BugChecker.MethodInvocationTreeMatcher {

  private static final Matcher<ExpressionTree> IS_MARKER = isSubtypeOf("org.slf4j.Marker");
  private static final Matcher<ExpressionTree> IS_THROWABLE = isSubtypeOf("java.lang.Throwable");
  private static final String FQCN_SLF4J_LOGGER = "org.slf4j.Logger";
  private static final String FQCN_STRUCTURED_ARGUMENT =
      "net.logstash.logback.argument.StructuredArgument";

  // TODO: support fluent API
  private static final Matcher<ExpressionTree> LOGGING_METHOD =
      MethodMatchers.instanceMethod()
          .onDescendantOf(FQCN_SLF4J_LOGGER)
          .withNameMatching(Pattern.compile("info|debug|trace|warn|error"));

  private static final Matcher<ExpressionTree> STRUCTURED_ARGUMENT =
      com.google.errorprone.matchers.Matchers.isSubtypeOf(FQCN_STRUCTURED_ARGUMENT);

  @Override
  public Description matchMethodInvocation(MethodInvocationTree tree, VisitorState state) {
    if (!LOGGING_METHOD.matches(tree, state)) {
      return Description.NO_MATCH;
    }
    List<? extends ExpressionTree> arguments = tree.getArguments();
    int argumentSize = arguments.size() - 1;

    int formatIndex = 0;
    if (IS_MARKER.matches(arguments.get(0), state)) {
      argumentSize--;
      formatIndex = 1;
    }
    if (IS_THROWABLE.matches(arguments.get(arguments.size() - 1), state)) {
      argumentSize--;
    }
    if (argumentSize < 0) {
      return Description.NO_MATCH;
    }
    Object constant = ASTHelpers.constValue(tree.getArguments().get(formatIndex));
    if (constant == null) {
      return Description.NO_MATCH;
    }
    String format = constant.toString();
    if (!format.contains("{}")) {
      return Description.NO_MATCH;
    }

    int i = formatIndex + 1;
    int placeholderCount = 0;
    Pattern pattern = Pattern.compile("\\{\\}");
    java.util.regex.Matcher matcher = pattern.matcher(format);
    while (matcher.find()) {
      placeholderCount++;
      if (i >= arguments.size()) {
        break;
      }
      ExpressionTree argument = arguments.get(i);
      if (STRUCTURED_ARGUMENT.matches(argument, state)) {
        return buildDescription(tree)
            .setMessage("placeholder should not contain StructuredArgument")
            .build();
      }
      i++;
    }

    int structuredArgumentCount = 0;
    for (int j = formatIndex + 1; j <= formatIndex + argumentSize; j++) {
      ExpressionTree argument = arguments.get(j);
      if (STRUCTURED_ARGUMENT.matches(argument, state)) {
        structuredArgumentCount++;
      }
    }
    if (placeholderCount != argumentSize - structuredArgumentCount) {
      return buildDescription(tree)
          .setMessage("count of placeholders does not match with the count of arguments")
          .build();
    }

    return Description.NO_MATCH;
  }
}
