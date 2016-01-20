package org.raml.nodes.snakeyaml;

import org.raml.nodes.StringNode;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class SYStringNode extends SYBaseRamlNode implements StringNode
{

    public SYStringNode(ScalarNode scalarNode)
    {
        super(scalarNode);
    }

    public String getValue()
    {
        return ((ScalarNode) getYamlNode()).getValue();
    }
}
