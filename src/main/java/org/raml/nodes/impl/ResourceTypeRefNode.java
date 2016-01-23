/*
 *
 */
package org.raml.nodes.impl;

import org.raml.grammar.Raml10Grammar;
import org.raml.nodes.Node;
import org.raml.utils.NodeSelector;

import javax.annotation.Nullable;

public class ResourceTypeRefNode extends AbstractReferenceNode
{

    private String name;

    public ResourceTypeRefNode(String name)
    {
        this.name = name;
    }

    @Override
    public String getRefName()
    {
        return name;
    }

    @Override
    @Nullable
    public ResourceTypeNode getRefNode()
    {
        // We add the .. as the node selector selects the value and we want the key value pair
        final Node resolve = NodeSelector.selectFrom(Raml10Grammar.RESOURCE_TYPES_KEY_NAME + "/*/" + getRefName() + "/..", getRelativeNode());
        if (resolve instanceof ResourceTypeNode)
        {
            return (ResourceTypeNode) resolve;
        }
        else
        {
            return null;
        }
    }
}
