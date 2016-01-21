/*
 *
 */
package org.raml.nodes.snakeyaml;

import org.raml.nodes.ObjectNode;

public class SYObjectNode extends SYBaseRamlNode implements ObjectNode
{
    public SYObjectNode(org.yaml.snakeyaml.nodes.MappingNode mappingNode)
    {
        super(mappingNode);
    }
}
