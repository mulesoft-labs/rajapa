package org.raml.grammar.rule;

import org.raml.nodes.ArrayNode;
import org.raml.nodes.Node;

import java.util.List;

public class ArrayValueRule extends Rule
{

    private Rule of;

    public ArrayValueRule(Rule of)
    {
        this.of = of;
    }

    @Override
    public boolean matches(Node node)
    {
        return node instanceof ArrayNode;
    }

    @Override
    public Node transform(Node node)
    {
        Node result = node;
        if (getFactory() != null)
        {
            result = getFactory().create();
        }
        final List<Node> children = node.getChildren();
        for (Node child : children)
        {
            if (of.matches(child))
            {
                final Node transform = of.transform(child);
                child.replaceWith(transform);
            }
            else
            {
                child.replaceWith(ErrorNodeFactory.createInvalidElement(child));
            }
        }
        return result;
    }

    @Override
    public String getDescription()
    {
        return "Array[" + of.getDescription() + "]";
    }
}
