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
package org.raml.nodes.snakeyaml;

import javax.annotation.Nonnull;

import org.raml.nodes.NodeType;
import org.raml.nodes.NullNode;
import org.yaml.snakeyaml.nodes.Node;

public class SYNullNode extends SYBaseRamlNode implements NullNode
{
    public SYNullNode(SYNullNode node)
    {
        super(node);
    }

    public SYNullNode(Node yamlNode)
    {
        super(yamlNode);
    }

    @Nonnull
    @Override
    public org.raml.nodes.Node copy()
    {
        return new SYNullNode(this);
    }

    @Override
    public NodeType getType()
    {
        return NodeType.Null;
    }
}
