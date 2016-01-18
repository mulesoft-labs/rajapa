package org.raml.nodes;

import java.util.Collection;

public class RamlRootNode extends RamlNodeHandler
{

    private RamlMappingNode decoratee;

    public RamlRootNode(RamlMappingNode mappingNode)
    {
        decoratee = mappingNode;
    }

    @Override
    public RamlNode getParent()
    {
        return null;
    }

    @Override
    public Collection<RamlNode> getChildren()
    {
        return decoratee.getChildren();
    }

}
