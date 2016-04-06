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

import java.util.List;

import javax.annotation.Nonnull;

import org.raml.grammar.rule.AnyValueRule;
import org.raml.impl.v10.nodes.types.builtin.BooleanTypeNode;
import org.raml.impl.v10.nodes.types.builtin.NumericTypeNode;
import org.raml.impl.v10.nodes.types.builtin.StringTypeNode;
import org.raml.impl.v10.nodes.types.builtin.TypeNode;
import org.raml.impl.v10.nodes.types.builtin.TypeNodeVisitor;
import org.raml.nodes.AbstractRamlNode;
import org.raml.nodes.KeyValueNode;
import org.raml.nodes.Node;
import org.raml.nodes.NodeType;
import org.raml.nodes.ObjectNode;
import org.raml.nodes.StringNode;

public class ExampleTypeNode extends AbstractRamlNode implements ObjectNode, TypeNode
{

    private List<PropertyNode> properties;

    public ExampleTypeNode()
    {
    }

    protected ExampleTypeNode(ExampleTypeNode node)
    {
        super(node);
    }

    @Override
    public <T> T visit(TypeNodeVisitor<T> visitor)
    {
        Node grandParent = getParent().getParent();
        if (grandParent instanceof StringTypeNode)
        {
            return visitor.visitString((StringTypeNode) grandParent);
        }
        else if (grandParent instanceof NumericTypeNode)
        {
            return visitor.visitNumber((NumericTypeNode) grandParent);
        }
        else if (grandParent instanceof BooleanTypeNode)
        {
            return visitor.visitBoolean((BooleanTypeNode) grandParent);
        }
        return (T) new AnyValueRule();
    }

    public <T> T visitProperties(TypeNodeVisitor<T> visitor, List<PropertyNode> properties)
    {
        return visitor.visitExample(properties);
    }

    public String getTypeName()
    {
        return ((StringNode) ((KeyValueNode) this.getParent().getParent().getParent()).getKey()).getValue();
    }

    public void setProperties(List<PropertyNode> properties)
    {
        this.properties = properties;
    }

    @Nonnull
    @Override
    public Node copy()
    {
        return new ExampleTypeNode(this);
    }

    @Override
    public NodeType getType()
    {
        return NodeType.Object;
    }
}
