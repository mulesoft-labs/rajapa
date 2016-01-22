/*
 *
 */
package org.raml.nodes.impl;

import org.raml.nodes.*;

import java.util.List;

public class StringTemplateNode extends StringNodeImpl implements ExecutableNode
{

    public StringTemplateNode(String value)
    {
        super(value);
    }

    @Override
    public void addChild(Node node)
    {
        if (!(node instanceof StringNode))
        {
            throw new IllegalArgumentException("Only String nodes are valid as children");
        }
        super.addChild(node);
    }

    public Node execute(ExecutionContext context)
    {
        final List<Node> children = getChildren();
        StringBuilder content = new StringBuilder();
        for (Node child : children)
        {
            if (child instanceof ExecutableNode)
            {
                final Node result = ((ExecutableNode) child).execute(context);
                if (result instanceof ErrorNode)
                {
                    return result;
                }
                else
                {
                    content.append(((StringNode) result).getValue());
                }
            }
            else
            {
                content.append(((StringNode) child).getValue());
            }
        }
        return new StringNodeImpl(content.toString());
    }
}
