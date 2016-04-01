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
package org.raml.impl.commons.nodes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.raml.impl.v10.grammar.Raml10Grammar;
import org.raml.nodes.AbstractReferenceNode;
import org.raml.nodes.Node;
import org.raml.nodes.StringNode;
import org.raml.utils.NodeSelector;

public class BaseResourceTypeRefNode extends AbstractReferenceNode
{

    private String name;

    public BaseResourceTypeRefNode(String name)
    {
        this.name = name;
    }

    public BaseResourceTypeRefNode(BaseResourceTypeRefNode node)
    {
        super(node);
        this.name = node.name;
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

    @Nonnull
    @Override
    public Node copy()
    {
        return new BaseResourceTypeRefNode(this);
    }

}
