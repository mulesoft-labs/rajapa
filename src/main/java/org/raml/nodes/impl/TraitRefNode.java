/*
 *
 */
package org.raml.nodes.impl;

import java.util.List;

public class TraitRefNode extends AbstractReferenceNode
{

    @Override
    public TraitNode getRefNode()
    {
        final List<TraitNode> childrenWith = getRootNode().findChildrenWith(TraitNode.class);
        for (TraitNode typeNode : childrenWith)
        {
            if (typeNode.getName().equals(getRefName()))
            {
                return typeNode;
            }
        }
        return null;
    }
}
