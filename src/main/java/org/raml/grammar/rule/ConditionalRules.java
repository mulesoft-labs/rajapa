package org.raml.grammar.rule;

import org.raml.nodes.Node;
import org.raml.utils.NodeSelector;

import javax.annotation.Nonnull;
import java.util.*;

public class ConditionalRules
{

    private String selectorExpression;
    private List<ConditionalRule> options;

    public ConditionalRules(String selectorExpression, ConditionalRule... cases)
    {
        this.selectorExpression = selectorExpression;
        this.options = Arrays.asList(cases);
    }

    @Nonnull
    public List<KeyValueRule> getRulesNode(Node node)
    {
        final Node from = NodeSelector.selectFrom(selectorExpression, node);
        if (from != null)
        {
            for (ConditionalRule option : options)
            {
                if (option.matches(from))
                {
                    return option.getRules();
                }
            }
        }

        return Collections.emptyList();
    }


    public ConditionalRule is(Rule condition)
    {
        final ConditionalRule conditionalRule = new ConditionalRule(condition);
        this.options.add(conditionalRule);
        return conditionalRule;
    }

}
