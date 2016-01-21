package org.raml.nodes.impl;

import javax.annotation.Nullable;
import java.util.List;

public class ResourceTypeRefNode extends AbstractReferenceNode
{

    @Override
    @Nullable
    public ResourceTypeNode getRefNode()
    {
        final List<ResourceTypeNode> childrenWith = getRootNode().findChildrenWith(ResourceTypeNode.class);
        for (ResourceTypeNode typeNode : childrenWith)
        {
            if (typeNode.getName().equals(getRefName()))
            {
                return typeNode;
            }
        }
        return null;
    }
}
