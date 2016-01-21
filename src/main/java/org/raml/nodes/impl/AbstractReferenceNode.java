/*
 *
 */
package org.raml.nodes.impl;

import org.raml.nodes.ReferenceNode;
import org.raml.nodes.StringNode;

public abstract class AbstractReferenceNode extends AbstractRamlNode implements ReferenceNode
{
    @Override
    public String getRefName()
    {
        return ((StringNode) getSource()).getValue();
    }
}
