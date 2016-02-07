/*
 * Copyright 2013 (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.raml.nodes.impl;

import org.raml.nodes.Node;
import org.raml.nodes.NodeType;
import org.raml.nodes.ReferenceNode;

public abstract class AbstractReferenceNode extends AbstractRamlNode implements ReferenceNode
{

    public AbstractReferenceNode()
    {
    }

    public AbstractReferenceNode(AbstractReferenceNode node)
    {
        super(node);
    }

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

    @Override
    public NodeType getType()
    {
        return NodeType.Reference;
    }

}
