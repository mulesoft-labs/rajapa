package org.raml.nodes.snakeyaml;

import org.raml.nodes.StringNode;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class SYScalarNode extends SYBaseRamlNode implements StringNode
{

    public SYScalarNode(ScalarNode scalarNode)
    {
        super(scalarNode);
    }

    public String getValue()
    {
        return ((ScalarNode) getYamlNode()).getValue();
    }
}
