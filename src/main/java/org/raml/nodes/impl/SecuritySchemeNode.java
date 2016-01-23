/*
 *
 */
package org.raml.nodes.impl;

import org.raml.nodes.StringNode;

public class SecuritySchemeNode extends KeyValueNodeImpl
{

    public SecuritySchemeNode()
    {
    }

    public String getName()
    {
        final StringNode key = (StringNode) getKey();
        return key.getValue();
    }
}
