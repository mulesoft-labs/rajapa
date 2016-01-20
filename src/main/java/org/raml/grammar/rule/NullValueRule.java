package org.raml.grammar.rule;

import org.raml.nodes.Node;
import org.raml.nodes.NullNode;

public class NullValueRule extends Rule
{
    @Override
    public boolean matches(Node node)
    {
        return node instanceof NullNode;
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
            return node;
        }
    }

    @Override
    public String getDescription()
    {
        return "null";
    }
}
