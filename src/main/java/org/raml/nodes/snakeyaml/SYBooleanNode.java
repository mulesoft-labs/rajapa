package org.raml.nodes.snakeyaml;

import org.raml.nodes.BooleanNode;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class SYBooleanNode extends SYBaseRamlNode implements BooleanNode
{

    public SYBooleanNode(ScalarNode scalarNode)
    {
        super(scalarNode);
    }

    public Boolean getValue()
    {
        final String value = ((ScalarNode) getYamlNode()).getValue();
        return Boolean.parseBoolean(value);
    }
}