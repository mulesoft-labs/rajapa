/*
 *
 */
package org.raml.nodes.snakeyaml;

import org.raml.nodes.ArrayNode;

public class SYArrayNode extends SYBaseRamlNode implements ArrayNode
{

    public SYArrayNode(org.yaml.snakeyaml.nodes.SequenceNode sequenceNode)
    {
        super(sequenceNode);
    }

}
