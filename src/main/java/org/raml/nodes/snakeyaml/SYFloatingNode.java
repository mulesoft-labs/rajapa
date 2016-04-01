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

import org.raml.nodes.FloatingNode;
import org.raml.nodes.Node;
import org.raml.nodes.NodeType;
import org.yaml.snakeyaml.nodes.ScalarNode;

import java.math.BigDecimal;

import javax.annotation.Nonnull;

public class SYFloatingNode extends SYBaseRamlNode implements FloatingNode
{
    public SYFloatingNode(SYFloatingNode node)
    {
        super(node);
    }

    public SYFloatingNode(ScalarNode yamlNode)
    {
        super(yamlNode);
    }

    @Override
    public BigDecimal getValue()
    {
        final String value = ((ScalarNode) getYamlNode()).getValue();
        return new BigDecimal(value);
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
        return new SYFloatingNode(this);
    }

    @Override
    public NodeType getType()
    {
        return NodeType.Float;
    }
}
