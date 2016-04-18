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
package org.raml.v2.impl.v10.nodes.types.builtin;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.raml.v2.impl.commons.model.StringType;
import org.raml.v2.impl.commons.nodes.PropertyNode;
import org.raml.v2.nodes.AbstractRamlNode;
import org.raml.v2.nodes.Node;
import org.raml.v2.nodes.NodeType;
import org.raml.v2.nodes.ObjectNode;
import org.raml.v2.impl.v10.nodes.types.InheritedPropertiesInjectedNode;
import org.raml.v2.nodes.StringNode;
import org.raml.v2.nodes.snakeyaml.SYStringNode;
import org.raml.v2.utils.NodeUtils;

public class ObjectTypeNode extends AbstractRamlNode implements ObjectNode, TypeNode
{

    private List<InheritedPropertiesInjectedNode> inheritedProperties = Lists.newArrayList();

    public ObjectTypeNode()
    {
    }

    protected ObjectTypeNode(ObjectTypeNode node)
    {
        super(node);
    }

    public List<PropertyNode> getProperties()
    {
        ArrayList<PropertyNode> result = new ArrayList<>();
        List<Node> properties = Lists.newArrayList();
        if (getSource().get("properties") != null)
        {
            properties = getSource().get("properties").getChildren();
        }
        else if (this.get("properties") != null)
        {
            properties = this.get("properties").getChildren();
        }
        else if (this.get("type") != null &&
                 this.get("type") instanceof StringNode)
        {
            Node tree = NodeUtils.traverseToRoot(this);
            Node typesRoot = tree.get("types");
            String typeName;
            if ("array".equals(((StringNode) this.get("type")).getValue()) &&
                this.get("items") != null &&
                this.get("items") instanceof StringNode)
            {
                typeName = ((StringNode) this.get("items")).getValue();
            }
            else
            {
                typeName = ((StringNode) this.get("type")).getValue();
            }
            if (typesRoot != null)
            {
                Node type = typesRoot.get(typeName);
                if (type != null)
                {
                    return ((ObjectTypeNode) type).getProperties();
                }
            }
        }
        for (Node property : properties)
        {
            result.add((PropertyNode) property);
        }
        return result;
    }

    @Nonnull
    @Override
    public Node copy()
    {
        return new ObjectTypeNode(this);
    }

    @Override
    public NodeType getType()
    {
        return NodeType.Object;
    }

    public void addInheritedProperties(InheritedPropertiesInjectedNode node)
    {
        this.inheritedProperties.add(node);
    }

    public void setInheritedProperties(List<InheritedPropertiesInjectedNode> inheritedProperties)
    {
        this.inheritedProperties = inheritedProperties;
    }

    public List<InheritedPropertiesInjectedNode> getInheritedProperties()
    {
        return this.inheritedProperties;
    }

    @Override
    public <T> T visit(TypeNodeVisitor<T> visitor)
    {
        return visitor.visitObject(this);
    }

    public boolean isArray()
    {
        return this.get("type") != null && this.get("type") instanceof SYStringNode && "array".equals(((SYStringNode) this.get("type")).getValue());
    }
}
