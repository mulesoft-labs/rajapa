package org.raml.nodes;

import org.yaml.snakeyaml.nodes.Node;

public class RamlResourceNode extends RamlNodeHandler
{

    private RamlKeyValueNode decoratee;

    public RamlResourceNode(RamlKeyValueNode node)
    {
        decoratee = node;
    }
}
