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
package org.raml.v2.nodes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.raml.v2.impl.v10.nodes.LibraryRefNode;

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

    public static Map<String, SimpleTypeNode> getParameters(ParametrizedReferenceNode refNode)
    {
        Map<String, SimpleTypeNode> params = new HashMap<>();

        for (Node node : getParamNodes(refNode))
        {
            KeyValueNode keyValueNode = (KeyValueNode) node;
            params.put(keyValueNode.getKey().toString(), (SimpleTypeNode) keyValueNode.getValue());
        }
        return params;
    }

    private static List<Node> getParamNodes(Node refNode)
    {
        List<Node> children = refNode.getChildren();
        if (children.size() == 1)
        {
            return ((KeyValueNode) children.get(0)).getValue().getChildren();
        }
        if (children.size() == 2 && children.get(0) instanceof LibraryRefNode)
        {
            return ((KeyValueNode) children.get(1)).getValue().getChildren();
        }
        if (children.size() == 0)
        {
            throw new IllegalStateException("Parameterized reference node has no children");
        }
        throw new IllegalStateException("Parameterized reference node has invalid children types");
    }
}
