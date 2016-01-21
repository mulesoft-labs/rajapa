/*
 *
 */
package org.raml.grammar.rule;

import org.raml.nodes.Node;

import java.util.List;

public class AnyOfRule extends Rule
{

    private List<Rule> rules;

    public AnyOfRule(List<Rule> rules)
    {
        this.rules = rules;
    }

    @Override
    public boolean matches(Node node)
    {
        for (Rule rule : rules)
        {
            if (rule.matches(node))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public Node transform(Node node)
    {
        if (getFactory() != null)
        {
            return getFactory().create();
        }
        else
        {
            for (Rule rule : rules)
            {
                if (rule.matches(node))
                {
                    return rule.transform(node);
                }
            }
        }
        return node;
    }

    @Override
    public String getDescription()
    {
        final StringBuilder desc = new StringBuilder();
        desc.append("Any of :");
        int i = 0;
        for (Rule rule : rules)
        {
            if (i > 0)
            {
                desc.append(",");
            }
            desc.append(rule.getDescription());
            i++;
        }
        return desc.toString();
    }
}
