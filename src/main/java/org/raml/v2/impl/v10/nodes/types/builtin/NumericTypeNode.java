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
package org.raml.impl.v10.nodes.types.builtin;

import com.google.common.collect.Lists;

import java.util.List;

import javax.annotation.Nonnull;

import org.raml.nodes.AbstractRamlNode;
import org.raml.nodes.IntegerNode;
import org.raml.nodes.Node;
import org.raml.nodes.NodeType;
import org.raml.nodes.ObjectNode;
import org.raml.nodes.StringNode;
import org.raml.nodes.snakeyaml.SYArrayNode;
import org.raml.utils.NodeSelector;

public class NumericTypeNode extends AbstractRamlNode implements TypeNode, ObjectNode
{

    public NumericTypeNode()
    {
    }

    private NumericTypeNode(NumericTypeNode node)
    {
        super(node);
    }

    public Number getMinimum()
    {
        return NodeSelector.selectIntValue("minimum", getSource());
    }

    public Number getMaximum()
    {
        return NodeSelector.selectIntValue("maximum", getSource());
    }

    public Number getMultiple()
    {
        return NodeSelector.selectIntValue("multipleOf", getSource());
    }

    public String getFormat()
    {
        return NodeSelector.selectStringValue("format", getSource());
    }

    @Nonnull
    @Override
    public Node copy()
    {
        return new NumericTypeNode(this);
    }

    @Override
    public NodeType getType()
    {
        return NodeType.Object;
    }

    @Override
    public <T> T visit(TypeNodeVisitor<T> visitor)
    {
        return visitor.visitNumber(this);
    }

    @Nonnull
    public List<Number> getEnumValues()
    {

        Node values = this.get("enum");
        List<Number> enumValues = Lists.newArrayList();
        if (values != null && values instanceof SYArrayNode)
        {
            for (Node node : values.getChildren())
            {
                enumValues.add(((IntegerNode) node).getValue());
            }
        }
        return enumValues;
    }
}
