/*
 *
 */
package org.raml.grammar.rule;

import org.raml.nodes.Node;
import org.raml.nodes.StringNode;

public class StringTypeRule extends Rule
{
    @Override
    public boolean matches(Node node)
    {
        return node instanceof StringNode;
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
        return "String";
    }
}
