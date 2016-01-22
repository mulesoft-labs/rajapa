/*
 *
 */
package org.raml.nodes.impl;

import org.raml.nodes.StringNode;

public class ResourceTypeNode extends KeyValueNodeImpl
{

    public ResourceTypeNode()
    {
    }

    public String getName()
    {
        final StringNode key = (StringNode) getKey();
        return key.getValue();
    }

}
