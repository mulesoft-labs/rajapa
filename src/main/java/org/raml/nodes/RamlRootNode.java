package org.raml.nodes;

public class RamlRootNode extends RamlMappingNode
{

    public RamlRootNode(RamlMappingNode mappingNode)
    {
        super(mappingNode);
        mappingNode.setAsSourceOf(this);
    }

    @Override
    public RamlNode getParent()
    {
        return null;
    }

}
