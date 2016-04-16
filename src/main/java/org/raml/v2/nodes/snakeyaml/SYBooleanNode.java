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
package org.raml.v2.nodes.snakeyaml;

import javax.annotation.Nonnull;

import org.raml.v2.nodes.BooleanNode;
import org.raml.v2.nodes.Node;
import org.raml.v2.nodes.NodeType;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class SYBooleanNode extends SYBaseRamlNode implements BooleanNode
{

    public SYBooleanNode(SYBooleanNode node)
    {
        super(node);
    }

    public SYBooleanNode(ScalarNode scalarNode)
    {
        super(scalarNode);
    }

    public Boolean getValue()
    {
        final String value = ((ScalarNode) getYamlNode()).getValue();
        return Boolean.parseBoolean(value);
    }

    @Override
    public String toString()
    {
        return String.valueOf(getValue());
    }

    @Nonnull
    @Override
    public Node copy()
    {
        return new SYBooleanNode(this);
    }

    @Override
    public NodeType getType()
    {
        return NodeType.Boolean;
    }
}