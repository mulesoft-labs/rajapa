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

import javax.annotation.Nonnull;

import org.raml.v2.internal.impl.commons.RamlVersion;
import org.raml.v2.api.loader.ResourceLoader;
import org.raml.v2.internal.framework.nodes.AbstractRamlNode;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.nodes.NodeType;
import org.raml.v2.internal.framework.nodes.ObjectNode;

public class RamlDocumentNode extends AbstractRamlNode implements ObjectNode, ContextProviderNode
{

    private RamlVersion version;
    private ResourceLoader resourceLoader;

    public RamlDocumentNode()
    {
    }

    public RamlDocumentNode(RamlDocumentNode node)
    {
        super(node);
    }

    @Nonnull
    @Override
    public Node copy()
    {
        return new RamlDocumentNode(this);
    }

    @Override
    public NodeType getType()
    {
        return NodeType.Object;
    }

    @Nonnull
    public RamlVersion getVersion()
    {
        return version;
    }

    public void setVersion(RamlVersion version)
    {
        this.version = version;
    }

    @Nonnull
    public ResourceLoader getResourceLoader()
    {
        return resourceLoader;
    }

    public void setResourceLoader(ResourceLoader resourceLoader)
    {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public Node getContextNode()
    {
        return this;
    }
}
