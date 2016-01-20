package org.raml.grammar.rule;

import org.raml.nodes.IntegerNode;
import org.raml.nodes.Node;
import org.raml.nodes.StringNode;

import java.math.BigInteger;

public class IntegerValueRule extends Rule
{

    private BigInteger number;

    public IntegerValueRule(BigInteger number)
    {
        this.number = number;
    }

    @Override
    public boolean matches(Node node)
    {
        if (node instanceof IntegerNode)
        {
            return ((IntegerNode) node).getValue().equals(number);
        }
        else
        {
            return false;
        }
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
        return number.toString();
    }
}
