package io.github.wreulicke.errorprone.logstash;

import com.google.auto.service.AutoService;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.matchers.Matcher;
import com.google.errorprone.matchers.Matchers;
import com.google.errorprone.util.ASTHelpers;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import java.util.List;

@AutoService(BugChecker.class)
@BugPattern(
    summary = "format should be constant",
    severity = BugPattern.SeverityLevel.ERROR,
    link = "github.com/wreulicke/errorprone-logback-logstash-encoder",
    linkType = BugPattern.LinkType.CUSTOM)
public class Slf4jFluentApiFormatShouldBeConst extends BugChecker
    implements BugChecker.MethodInvocationTreeMatcher {

  private static final Matcher<MethodInvocationTree> MATCHER_USING_SUPPLIER =
      Matchers.argument(0, Matchers.isSubtypeOf("java.util.function.Supplier"));

  @Override
  public Description matchMethodInvocation(MethodInvocationTree tree, VisitorState state) {
    if (!Matchers.anyOf(Constants.FLUENT_API_LOG, Constants.FLUENT_API_SET_MESSAGE)
        .matches(tree, state)) {
      return Description.NO_MATCH;
    }
    List<? extends ExpressionTree> arguments = tree.getArguments();
    if (arguments.isEmpty()) {
      return Description.NO_MATCH;
    }

    if (MATCHER_USING_SUPPLIER.matches(tree, state)) {
      return buildDescription(tree).setMessage("format should be constant").build();
    }

    Object argument = ASTHelpers.constValue(arguments.get(0));
    if (argument == null) { // argument is not constant
      return buildDescription(tree).setMessage("format should be constant").build();
    }

    return Description.NO_MATCH;
  }
}
