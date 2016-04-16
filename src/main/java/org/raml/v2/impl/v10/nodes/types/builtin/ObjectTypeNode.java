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

import org.raml.v2.impl.commons.nodes.PropertyNode;
import org.raml.v2.nodes.AbstractRamlNode;
import org.raml.v2.nodes.Node;
import org.raml.v2.nodes.NodeType;
import org.raml.v2.nodes.ObjectNode;
import org.raml.v2.impl.v10.nodes.types.InheritedPropertiesInjectedNode;

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
        List<Node> properties = getSource().get("properties").getChildren();
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
}
