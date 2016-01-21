package org.raml.nodes.impl;

import org.raml.nodes.StringNode;

public class TraitNode extends KeyValueNodeImpl
{

    public String getName()
    {
        final StringNode key = (StringNode) getKey();
        return key.getValue();
    }
}
