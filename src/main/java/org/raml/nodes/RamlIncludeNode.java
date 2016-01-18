package org.raml.nodes;

import org.yaml.snakeyaml.nodes.ScalarNode;

public class RamlIncludeNode extends RamlScalarNode
{

    public RamlIncludeNode(ScalarNode scalarNode)
    {
        super(scalarNode);
    }

    public String getIncludePath()
    {
        //TODO go up tree to get the whole path if there are other includes
        return getValue();
    }
}
