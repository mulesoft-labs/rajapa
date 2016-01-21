/*
 *
 */
package org.raml.grammar.rule;

import org.raml.nodes.BooleanNode;
import org.raml.nodes.Node;
import org.raml.nodes.StringNode;

public class BooleanTypeRule extends Rule
{
    @Override
    public boolean matches(Node node)
    {
        return node instanceof BooleanNode;
    }

    @Override
    public Node transform(Node node)
    {
        if (getFactory() != null)
        {
            return getFactory().create(((StringNode) node).getValue());
        }
        else
        {
            return node;
        }
    }

    @Override
    public String getDescription()
    {
        return "Boolean";
    }
}
