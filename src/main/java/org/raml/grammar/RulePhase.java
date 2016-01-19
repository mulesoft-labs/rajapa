package org.raml.grammar;

import org.raml.nodes.ErrorNode;
import org.raml.nodes.Node;
import org.raml.phase.Phase;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class RulePhase implements Phase {

    private Rule rootRule;

    public RulePhase(Rule rootRule) {
        this.rootRule = rootRule;
    }

    @Override
    public Node apply(Node node) {
        return doApply(Arrays.asList(rootRule), node);
    }

    private Node doApply(List<Rule> rootRule, Node node) {
        final Rule matchingRule = findMatchingRule(rootRule, node);
        Node result;
        if (matchingRule != null) {
            result = matchingRule.transform(node);
            if (node != result) {
                node.replaceWith(result);
            }
            final List<Rule> subRules = matchingRule.getSubRules();
            final List<Node> children = result.getChildren();
            for (Node child : children) {
                doApply(subRules, child);
            }
        } else {
            StringBuilder options = new StringBuilder();
            int i = 0;
            for (Rule rule : rootRule) {
                if (i > 0) {
                    options.append(" or ");
                }
                options.append(rule.getDescription());
                i++;
            }
            result = new ErrorNode("Unknown node. Expected : " + options + " at \n" + node.getStartMark().getSourceCode());
            node.replaceWith(result);
        }

        return result;
    }

    @Nullable
    private Rule findMatchingRule(List<Rule> rootRule, Node node) {
        for (Rule rule : rootRule) {
            if (rule.matches(node)) {
                return rule;
            }
        }
        return null;
    }


}
