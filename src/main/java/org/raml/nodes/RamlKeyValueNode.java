package org.raml.nodes;

public class RamlKeyValueNode extends RamlAbstractNode
{

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
}

