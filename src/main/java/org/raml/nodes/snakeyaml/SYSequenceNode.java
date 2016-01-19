package org.raml.nodes.snakeyaml;

import org.raml.nodes.SequenceNode;

public class SYSequenceNode extends SYBaseRamlNode implements SequenceNode
{

    public SYSequenceNode(org.yaml.snakeyaml.nodes.SequenceNode sequenceNode)
    {
        super(sequenceNode);
    }

}
