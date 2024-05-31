package com.github.wreulicke.errorprone.logstash;

import static com.github.wreulicke.errorprone.logstash.Constants.*;

import com.google.auto.service.AutoService;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.matchers.Matchers;
import com.google.errorprone.util.ASTHelpers;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import java.util.List;

@AutoService(BugChecker.class)
@BugPattern(
    summary = "format should not contain placeholder. use structured argument instead.",
    severity = BugPattern.SeverityLevel.ERROR,
    link = "github.com/wreulicke/errorprone-logback-logstash-encoder",
    linkType = BugPattern.LinkType.CUSTOM)
public class Slf4jDoNotUsePlaceholder extends BugChecker
    implements BugChecker.MethodInvocationTreeMatcher {

  @Override
  public Description matchMethodInvocation(MethodInvocationTree tree, VisitorState state) {
    if (!Matchers.anyOf(LOGGING_METHOD, FLUENT_API_SET_MESSAGE).matches(tree, state)) {
      return Description.NO_MATCH;
    }
    List<? extends ExpressionTree> arguments = tree.getArguments();
    if (arguments.isEmpty()) {
      return Description.NO_MATCH;
    }

    int formatIndex = 0;
    if (IS_MARKER.matches(arguments.get(0), state)) {
      formatIndex = 1;
    }
    Object constant = ASTHelpers.constValue(tree.getArguments().get(formatIndex));
    if (constant == null) {
      return Description.NO_MATCH;
    }

    String format = constant.toString();
    java.util.regex.Matcher matcher = PLACEHOLDER_PATTERN.matcher(format);
    if (matcher.find()) {
      return buildDescription(tree)
          .setMessage("format should not contain placeholder. use structured argument instead.")
          .build();
    }

    return Description.NO_MATCH;
  }
}
