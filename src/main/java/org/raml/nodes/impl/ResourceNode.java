/*
 *
 */
package org.raml.nodes.impl;

import org.raml.nodes.Node;
import org.raml.nodes.StringNode;

public class ResourceNode extends KeyValueNodeImpl
{

    public String getRelativeUri()
    {
        Node key = getKey();
        if (key instanceof StringNode)
        {
            return ((StringNode) key).getValue();
        }
        else
        {
            throw new IllegalStateException("Key must be a string but was a " + key.getClass());
        }
    }
}
