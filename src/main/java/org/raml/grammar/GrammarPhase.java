/*
 *
 */
package org.raml.grammar;

import org.raml.grammar.rule.ErrorNodeFactory;
import org.raml.grammar.rule.Rule;
import org.raml.nodes.ErrorNode;
import org.raml.nodes.Node;
import org.raml.phase.Phase;

public class GrammarPhase implements Phase
{

    private Rule rootRule;

    public GrammarPhase(Rule rootRule)
    {
        this.rootRule = rootRule;
    }

    @Override
    public Node apply(Node node)
    {
        if (rootRule.matches(node))
        {
            final Node result = rootRule.transform(node);
            node.replaceWith(result);
            return result;
        }
        else
        {
            final ErrorNode errorNode = ErrorNodeFactory.createInvalidRootElement(node, rootRule.getDescription());
            node.replaceWith(errorNode);
            return errorNode;
        }
    }

}
