/*
 *
 */
package org.raml.grammar.rule;

import com.google.common.collect.Range;
import org.raml.nodes.IntegerNode;
import org.raml.nodes.Node;
import org.raml.nodes.StringNode;

import javax.annotation.Nullable;
import java.math.BigInteger;

public class IntegerTypeRule extends Rule
{

    @Nullable
    private Range<BigInteger> range;

    public IntegerTypeRule(@Nullable Range<BigInteger> range)
    {
        this.range = range;
    }

    @Override
    public boolean matches(Node node)
    {
        if (node instanceof IntegerNode)
        {
            if (range != null)
            {
                return range.contains(((IntegerNode) node).getValue());
            }
            else
            {
                return true;
            }
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
        return "Integer";
    }
}
