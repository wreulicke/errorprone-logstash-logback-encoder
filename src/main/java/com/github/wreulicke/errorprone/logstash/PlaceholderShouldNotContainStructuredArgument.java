package com.github.wreulicke.errorprone.logstash;

import static com.github.wreulicke.errorprone.logstash.Constants.*;
import static com.google.errorprone.matchers.Matchers.isSubtypeOf;

import com.google.auto.service.AutoService;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.matchers.Matcher;
import com.google.errorprone.util.ASTHelpers;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import java.util.List;

@AutoService(BugChecker.class)
@BugPattern(
    summary = "placeholder should not contain structured argument",
    severity = BugPattern.SeverityLevel.ERROR,
    link = "github.com/wreulicke/errorprone-logback-logstash-encoder",
    linkType = BugPattern.LinkType.CUSTOM)
public class PlaceholderShouldNotContainStructuredArgument extends BugChecker
    implements BugChecker.MethodInvocationTreeMatcher {

  private static final String FQCN_STRUCTURED_ARGUMENT =
      "net.logstash.logback.argument.StructuredArgument";

  private static final Matcher<ExpressionTree> IS_THROWABLE = isSubtypeOf("java.lang.Throwable");

  private static final Matcher<ExpressionTree> STRUCTURED_ARGUMENT =
      com.google.errorprone.matchers.Matchers.isSubtypeOf(FQCN_STRUCTURED_ARGUMENT);

  @Override
  public Description matchMethodInvocation(MethodInvocationTree tree, VisitorState state) {
    if (!LOGGING_METHOD.matches(tree, state)) {
      return Description.NO_MATCH;
    }
    List<? extends ExpressionTree> arguments = tree.getArguments();
    if (arguments.isEmpty()) { // case fluent api. ex. logger.atInfo().setMessage("message").log()
      return Description.NO_MATCH;
    }
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
    int i = formatIndex + 1;
    int placeholderCount = 0;
    java.util.regex.Matcher matcher = PLACEHOLDER_PATTERN.matcher(format);
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
          .setMessage(
              "count of placeholders does not match with the count of arguments without StructuredArgument")
          .build();
    }

    return Description.NO_MATCH;
  }
}
