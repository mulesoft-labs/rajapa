package org.raml.nodes;

import org.yaml.snakeyaml.nodes.ScalarNode;

public class RamlScalarNode extends RamlAbstractNode
{

    public RamlScalarNode(ScalarNode scalarNode)
    {
        super(scalarNode);
    }

    public String getValue()
    {
        return ((ScalarNode) getYamlNode()).getValue();
    }
}
