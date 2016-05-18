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
package org.raml.v2.internal.framework.nodes.snakeyaml;

import javax.annotation.Nonnull;

import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.nodes.NodeType;
import org.raml.v2.internal.framework.nodes.StringNode;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class SYStringNode extends SYBaseRamlNode implements StringNode
{

    // For copy
    protected SYStringNode(SYStringNode node, String resourcePath)
    {
        super(node, resourcePath);
    }


    public SYStringNode(ScalarNode scalarNode, String resourcePath)
    {
        super(scalarNode, resourcePath);
    }

    public String getValue()
    {
        return ((ScalarNode) getYamlNode()).getValue();
    }

    @Nonnull
    @Override
    public Node copy()
    {
        return new SYStringNode(this, getResourcePath());
    }

    @Override
    public String toString()
    {
        return getValue();
    }

    @Override
    public NodeType getType()
    {
        return NodeType.String;
    }
}
