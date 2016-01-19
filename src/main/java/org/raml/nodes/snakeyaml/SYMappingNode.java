package org.raml.nodes.snakeyaml;

import org.raml.nodes.MappingNode;

public class SYMappingNode extends SYBaseRamlNode implements MappingNode
{
    public SYMappingNode(org.yaml.snakeyaml.nodes.MappingNode mappingNode)
    {
        super(mappingNode);
    }
}
