/*
 *
 */
package org.raml.grammar.rule;

import org.raml.grammar.GrammarContext;
import org.raml.nodes.Node;

public class ReferenceRule extends Rule
{


    private GrammarContext context;
    private final String name;
    private Rule ref;

    public ReferenceRule(GrammarContext context, String name)
    {
        this.context = context;
        this.name = name;
    }

    public Rule getRef()
    {
        if (ref == null)
        {
            final Rule ruleByName = context.getRuleByName(name);
            if (ruleByName != null)
            {
                ref = ruleByName;
            }
            else
            {
                throw new RuntimeException("Invalid grammar rule reference name " + name);
            }
        }
        return ref;
    }


    @Override
    public boolean matches(Node node)
    {
        return getRef().matches(node);
    }

    @Override
    public Node transform(Node node)
    {
        return getRef().transform(node);
    }

    @Override
    public String getDescription()
    {
        return getRef().getDescription();
    }
}
