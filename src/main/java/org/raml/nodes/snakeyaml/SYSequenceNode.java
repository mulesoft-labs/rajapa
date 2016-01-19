package org.raml.nodes.snakeyaml;

import org.raml.nodes.RamlSequenceNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

public class SYSequenceNode extends SYBaseRamlNode implements RamlSequenceNode
{

    public SYSequenceNode(SequenceNode sequenceNode)
    {
        super(sequenceNode);
    }

}
