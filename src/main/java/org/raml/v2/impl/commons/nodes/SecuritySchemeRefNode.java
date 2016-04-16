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
package org.raml.v2.impl.commons.nodes;

import javax.annotation.Nonnull;

import org.raml.v2.impl.v10.grammar.Raml10Grammar;
import org.raml.v2.nodes.Node;
import org.raml.v2.nodes.AbstractReferenceNode;
import org.raml.v2.utils.NodeSelector;

public class SecuritySchemeRefNode extends AbstractReferenceNode
{
    private String name;

    public SecuritySchemeRefNode(String name)
    {
        this.name = name;
    }

    public SecuritySchemeRefNode(SecuritySchemeRefNode node)
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
    public SecuritySchemeNode getRefNode()
    {
        // We add the .. as the node selector selects the value and we want the key value pair
        final Node resolve = NodeSelector.selectFrom(Raml10Grammar.SECURITY_SCHEMES_KEY_NAME + "/*/" + getRefName() + "/..", getRelativeNode());
        if (resolve instanceof SecuritySchemeNode)
        {
            return (SecuritySchemeNode) resolve;
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
        return new SecuritySchemeRefNode(this);
    }
}
