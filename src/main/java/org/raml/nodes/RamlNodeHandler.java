package org.raml.nodes;

import java.util.Collection;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.nodes.Node;

public class RamlNodeHandler implements RamlNode
{

    @Override
    public Node getYamlNode()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Mark getStartMark()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Mark getEndMark()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public RamlNode getParent()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<RamlNode> getChildren()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addChild(RamlNode node)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void replaceChildWith(RamlNode oldNode, RamlNode newNode)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setParent(RamlNode parent)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSource(RamlNode source)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public RamlNode getSource()
    {
        return null;
    }

}
