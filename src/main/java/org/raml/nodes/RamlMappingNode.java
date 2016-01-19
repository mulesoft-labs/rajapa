package org.raml.nodes;

import org.yaml.snakeyaml.nodes.MappingNode;

public class RamlMappingNode extends RamlAbstractNode
{

    public RamlMappingNode(RamlMappingNode mappingNode)
    {
        this((MappingNode) mappingNode.getYamlNode());
    }

    public RamlMappingNode(MappingNode mappingNode)
    {
        super(mappingNode);
    }
}
