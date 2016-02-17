package org.raml.types.builtin;

import org.raml.nodes.Node;
import org.raml.nodes.NodeType;
import org.raml.nodes.ObjectNode;
import org.raml.nodes.impl.AbstractRamlNode;

public class BooleanTypeNode extends AbstractRamlNode implements ObjectNode
{

    public BooleanTypeNode()
    {
    }

    private BooleanTypeNode(BooleanTypeNode node)
    {
        super(node);
    }

    @Override
    public Node copy()
    {
        return new BooleanTypeNode(this);
    }

    @Override
    public NodeType getType()
    {
        return NodeType.Object;
    }
}
