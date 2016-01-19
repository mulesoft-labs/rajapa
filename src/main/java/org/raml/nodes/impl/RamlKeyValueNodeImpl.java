package org.raml.nodes.impl;

import org.raml.nodes.BaseRamlNode;
import org.raml.nodes.Position;
import org.raml.nodes.RamlKeyValueNode;
import org.raml.nodes.RamlNode;
import org.raml.nodes.snakeyaml.SYScalarNode;

public class RamlKeyValueNodeImpl extends BaseRamlNode implements RamlKeyValueNode
{

    public RamlKeyValueNodeImpl(RamlNode keyNode, RamlNode valueNode)
    {
        addChild(keyNode);
        addChild(valueNode);
    }

    @Override
    public Position getStartMark() {
        return null;
    }

    @Override
    public Position getEndMark() {
        return null;
    }

    @Override
    public void addChild(RamlNode node)
    {
        if (getChildren().size() >= 2)
        {
            throw new IllegalStateException();
        }
        super.addChild(node);
    }

    public String getKeyNodeValue()
    {
        return ((SYScalarNode) getChildren().iterator().next()).getValue();
    }

    @Override
    public RamlNode getKey() {
        return getChildren().get(0);
    }

    @Override
    public RamlNode getValue() {
        return getChildren().get(1);
    }
}

