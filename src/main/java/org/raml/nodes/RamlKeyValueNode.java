package org.raml.nodes;

import java.util.Iterator;

public class RamlKeyValueNode extends RamlAbstractNode
{

    public RamlKeyValueNode(RamlKeyValueNode node)
    {
        super(node.getYamlNode());
    }

    public RamlKeyValueNode(RamlNode keyNode, RamlNode valueNode)
    {
        super(keyNode.getYamlNode());
        addChild(keyNode);
        addChild(valueNode);
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
        return ((RamlScalarNode) getChildren().iterator().next()).getValue();
    }

    public RamlNode getKeyNode()
    {
        return getChildren().iterator().next();
    }

    public RamlNode getValueNode()
    {
        Iterator<RamlNode> iterator = getChildren().iterator();
        iterator.next();
        return iterator.next();
    }
}

