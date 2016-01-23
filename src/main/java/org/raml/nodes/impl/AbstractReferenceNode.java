/*
 *
 */
package org.raml.nodes.impl;

import org.raml.nodes.Node;
import org.raml.nodes.ReferenceNode;
import org.raml.nodes.StringNode;

import java.util.List;

public abstract class AbstractReferenceNode extends AbstractRamlNode implements ReferenceNode
{

    public Node getRelativeNode()
    {
        if (!getChildren().isEmpty() && getChildren().get(0) instanceof ReferenceNode)
        {
            return ((ReferenceNode) getChildren().get(0)).getRefNode();
        }
        else
        {
            return getRootNode();
        }

    }

}
