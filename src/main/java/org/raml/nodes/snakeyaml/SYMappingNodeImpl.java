package org.raml.nodes.snakeyaml;

import org.raml.nodes.RamlMappingNode;
import org.yaml.snakeyaml.nodes.MappingNode;

public class SYMappingNodeImpl extends SYBaseRamlNode implements RamlMappingNode
{
    public SYMappingNodeImpl(MappingNode mappingNode)
    {
        super(mappingNode);
    }
}
