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
package org.raml.impl.v10.nodes.types;

import com.google.common.collect.Lists;

import java.util.List;

import javax.annotation.Nonnull;

import org.raml.impl.commons.nodes.PropertyNode;
import org.raml.nodes.BaseNode;
import org.raml.nodes.DefaultPosition;
import org.raml.nodes.KeyValueNode;
import org.raml.nodes.Node;
import org.raml.nodes.NodeType;
import org.raml.nodes.ObjectNode;
import org.raml.nodes.Position;

public class InheritedPropertiesInjectedNode extends BaseNode implements ObjectNode
{

    public InheritedPropertiesInjectedNode()
    {
    }

    public InheritedPropertiesInjectedNode(InheritedPropertiesInjectedNode inheritedPropertiesInjectedNode)
    {
        super(inheritedPropertiesInjectedNode);
    }

    public List<PropertyNode> getProperties()
    {
        List<PropertyNode> properties = Lists.newArrayList();
        for (Node propertyNode : ((KeyValueNode) this.getChildren().get(0)).getValue().getChildren())
        {
            properties.add((PropertyNode) propertyNode);
        }
        return properties;
    }

    @Nonnull
    @Override
    public Position getStartPosition()
    {
        return DefaultPosition.emptyPosition();
    }

    @Nonnull
    @Override
    public Position getEndPosition()
    {
        return DefaultPosition.emptyPosition();
    }

    @Nonnull
    @Override
    public Node copy()
    {
        return new InheritedPropertiesInjectedNode(this);
    }

    @Override
    public NodeType getType()
    {
        return NodeType.Object;
    }

}
