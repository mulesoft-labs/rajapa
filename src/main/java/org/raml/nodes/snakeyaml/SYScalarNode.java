package org.raml.nodes.snakeyaml;

import org.raml.nodes.RamlStringNode;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class SYScalarNode extends SYBaseRamlNode implements RamlStringNode
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
