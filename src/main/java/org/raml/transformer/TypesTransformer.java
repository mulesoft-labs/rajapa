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
package org.raml.transformer;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.raml.nodes.ErrorNode;
import org.raml.nodes.KeyValueNode;
import org.raml.nodes.Node;
import org.raml.nodes.snakeyaml.SYObjectNode;
import org.raml.nodes.snakeyaml.SYStringNode;
import org.raml.types.builtin.ObjectTypeNode;
import org.raml.types.builtin.UnionTypeNode;
import org.raml.utils.NodeUtils;

public class TypesTransformer implements Transformer
{

    @Override
    public boolean matches(Node node)
    {
        return node instanceof ObjectTypeNode;
    }

    @Override
    public Node transform(Node node)
    {
        SYObjectNode typesRoot = getTypesRoot(node);
        if (node instanceof UnionTypeNode)
        {
            Node properties = node.get("properties");
            if (properties != null)
            {
                final SYStringNode typeNode = (SYStringNode) node.get("type");
                if (typeNode != null)
                {
                    for (String type : typeNode.getValue().split("\\|"))
                    {
                        List<Node> unionProperties = getTypeProperties(getType(typesRoot, StringUtils.trim(type)));
                        for (Node property : unionProperties)
                        {
                            Node existingProperty = properties.get(((KeyValueNode) property).getKey().toString());
                            if (existingProperty != null)
                            {
                                Node errorNode = new ErrorNode("property definition {" + property + "} overrides existing property: {" + existingProperty.getParent() + "}");
                                errorNode.setSource(property);
                                properties.addChild(errorNode);
                            }
                            else
                            {
                                properties.addChild(property);
                            }
                        }
                    }
                }
            }
        }
        return node;
    }

    private SYObjectNode getTypesRoot(Node node)
    {
        final KeyValueNode keyValueNode = (KeyValueNode) NodeUtils.getAncestor(node, 3);
        return keyValueNode != null ? (SYObjectNode) keyValueNode.getValue() : null;
    }

    private List<Node> getTypeProperties(ObjectTypeNode node)
    {
        return node.getProperties();
    }

    private ObjectTypeNode getType(SYObjectNode node, String typeName)
    {
        return (ObjectTypeNode) node.get(typeName);
    }
}
