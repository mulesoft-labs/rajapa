/*
 *
 */
package org.raml.grammar.rule;

import org.raml.nodes.Node;

public class AnyValueRule extends Rule
{
    @Override
    public boolean matches(Node node)
    {
        return true;
    }

    @Override
    public Node transform(Node node)
    {
        return node;
    }

    @Override
    public String getDescription()
    {
        return "any";
    }
}
