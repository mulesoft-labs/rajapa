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

import java.util.List;

import javax.annotation.Nonnull;

import org.raml.v2.grammar.rule.AnyValueRule;
import org.raml.v2.utils.JSonDumper;
import org.raml.v2.impl.v10.nodes.types.builtin.BooleanTypeNode;
import org.raml.v2.impl.v10.nodes.types.builtin.NumericTypeNode;
import org.raml.v2.impl.v10.nodes.types.builtin.ObjectTypeNode;
import org.raml.v2.impl.v10.nodes.types.builtin.StringTypeNode;
import org.raml.v2.impl.v10.nodes.types.builtin.TypeNode;
import org.raml.v2.impl.v10.nodes.types.builtin.TypeNodeVisitor;
import org.raml.v2.nodes.AbstractRamlNode;
import org.raml.v2.nodes.KeyValueNode;
import org.raml.v2.nodes.Node;
import org.raml.v2.nodes.NodeType;
import org.raml.v2.nodes.ObjectNode;
import org.raml.v2.nodes.StringNode;
import org.raml.v2.nodes.snakeyaml.SYStringNode;
import org.raml.v2.utils.NodeUtils;

public class ExampleTypeNode extends AbstractRamlNode implements ObjectNode, TypeNode
{

    private String typeName;


    public ExampleTypeNode()
    {
    }

    protected ExampleTypeNode(AbstractRamlNode node)
    {
        super(node);
    }

    public ExampleTypeNode(AbstractRamlNode node, String typeName)
    {
        this(node);
        this.typeName = typeName;
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

    public <T> T visitProperties(TypeNodeVisitor<T> visitor, List<PropertyNode> properties, boolean allowsAdditionalProperties)
    {
        return visitor.visitExample(properties, allowsAdditionalProperties);
    }

    public String getTypeName()
    {
        if (this.typeName != null)
        {
            return typeName;
        }

        Node type = this.getParent().getParent().get("type");
        if (type != null && type instanceof StringNode && !"object".equals(((StringNode) type).getValue()))
        {
            String value = ((StringNode) type).getValue();
            if (NodeUtils.isSchemaType(type))
            {
                return ((SYStringNode) ((KeyValueNode) this.getParent().getParent().getParent()).getKey()).getValue();
            }
            else if ("array".equals(value))
            {
                return ((StringNode) type.getParent().getParent().get("items")).getValue();
            }
            else
            {
                Node parent = this.getParent().getParent().getParent();
                if (parent != null && parent instanceof KeyValueNode && ((KeyValueNode) parent).getKey() instanceof StringNode && ((KeyValueNode) parent).getValue() instanceof ObjectTypeNode)
                {
                    return ((StringNode) ((KeyValueNode) parent).getKey()).getValue();
                }
                else
                {
                    return value;
                }
            }
        }
        return ((StringNode) ((KeyValueNode) this.getParent().getParent().getParent()).getKey()).getValue();
    }

    @Nonnull
    @Override
    public Node copy()
    {
        ExampleTypeNode exampleTypeNode = new ExampleTypeNode();
        exampleTypeNode.setSource(this.getSource().copy());
        return exampleTypeNode;
    }

    @Override
    public NodeType getType()
    {
        return NodeType.Object;
    }

    @Override
    public String toString()
    {
        return getSource().toString();
    }

    public String toJsonString()
    {
        return JSonDumper.dump(getSource());
    }

    public boolean isArrayExample()
    {
        Node type = this.getParent().getParent().get("type");
        return type instanceof StringNode && "array".equals(((StringNode) type).getValue());
    }

}
