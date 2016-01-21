/*
 *
 */
package org.raml.grammar.rule;

import org.raml.nodes.Node;
import org.raml.nodes.StringNode;

public class StringValueRule extends Rule
{

    private String value;

    public StringValueRule(String value)
    {
        this.value = value;
    }

    @Override
    public boolean matches(Node node)
    {
        return node instanceof StringNode && ((StringNode) node).getValue().equals(value);
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
        return "\"" + value + "\"";
    }
}
