package org.raml.types.builtin;

import org.raml.nodes.Node;
import org.raml.nodes.NodeType;
import org.raml.nodes.ObjectNode;
import org.raml.nodes.impl.AbstractRamlNode;
import org.raml.utils.NodeSelector;

public class StringTypeNode extends AbstractRamlNode implements ObjectNode
{

    public StringTypeNode()
    {
    }

    private StringTypeNode(StringTypeNode node)
    {
        super(node);
    }

    public int getMinLength()
    {
        return NodeSelector.selectIntValue("minLength", getSource());
    }

    public int getMaxLength()
    {
        return NodeSelector.selectIntValue("maxLength", getSource());
    }

    public String getPattern()
    {
        return NodeSelector.selectStringValue("pattern", getSource());
    }

    @Override
    public Node copy()
    {
        return new StringTypeNode(this);
    }

    @Override
    public NodeType getType()
    {
        return NodeType.Object;
    }
}
