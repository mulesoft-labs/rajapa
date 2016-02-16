package org.raml.types.builtin;

import org.raml.nodes.Node;
import org.raml.nodes.NodeType;
import org.raml.nodes.ObjectNode;
import org.raml.nodes.impl.AbstractRamlNode;

public class ObjectTypeNode extends AbstractRamlNode implements ObjectNode
{

    public ObjectTypeNode()
    {
    }

    private ObjectTypeNode(ObjectTypeNode node)
    {
        super(node);
    }

    @Override
    public Node copy()
    {
        return new ObjectTypeNode(this);
    }

    @Override
    public NodeType getType()
    {
        return NodeType.Object;
    }
}
