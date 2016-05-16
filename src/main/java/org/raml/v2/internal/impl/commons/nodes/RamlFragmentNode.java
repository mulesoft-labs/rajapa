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
package org.raml.v2.internal.impl.commons.nodes;

import org.raml.v2.internal.framework.nodes.*;
import org.raml.v2.internal.impl.v10.grammar.Raml10Grammar;
import org.raml.v2.internal.utils.NodeSelector;
import org.raml.v2.internal.utils.NodeUtils;

import javax.annotation.Nonnull;

public class RamlFragmentNode extends AbstractRamlNode implements ObjectNode, ContextProviderNode, LibraryNodeProvider
{
    private Node libraryNode;

    public RamlFragmentNode()
    {
    }

    public RamlFragmentNode(AbstractRamlNode node)
    {
        super(node);
    }

    @Nonnull
    @Override
    public Node copy()
    {
        return new RamlFragmentNode(this);
    }

    @Override
    public NodeType getType()
    {
        return NodeType.Object;
    }

    @Override
    public Node getContextNode()
    {
        return getLibraryNode() != null || getParent() == null ? this : NodeUtils.getContextNode(getParent());
    }

    @Override
    public Node getLibraryNode()
    {
        return libraryNode;
    }

    public void resolveInternalLibraryNode()
    {
        if (libraryNode == null)
        {
            libraryNode = NodeSelector.selectFrom(Raml10Grammar.USES_KEY_NAME, this);
            if (libraryNode != null)
            {
                children.remove(libraryNode);
            }
        }
    }
}
