package org.raml.types.builtin;

import org.raml.nodes.Node;
import org.raml.nodes.NodeType;
import org.raml.nodes.ObjectNode;
import org.raml.nodes.impl.AbstractRamlNode;
import org.raml.utils.NodeSelector;

public class NumericTypeNode extends AbstractRamlNode implements ObjectNode
{

    public NumericTypeNode()
    {
    }

    private NumericTypeNode(NumericTypeNode node)
    {
        super(node);
    }

    public Number getMinimum()
    {
        return NodeSelector.selectIntValue("minimum", getSource());
    }

    public Number getMaximum()
    {
        return NodeSelector.selectIntValue("maximum", getSource());
    }

    public Number getMultiple()
    {
        return NodeSelector.selectIntValue("multipleOf", getSource());
    }

    public String getFormat()
    {
        return NodeSelector.selectStringValue("format", getSource());
    }

    @Override
    public Node copy()
    {
        return new NumericTypeNode(this);
    }

    @Override
    public NodeType getType()
    {
        return NodeType.Object;
    }
}
