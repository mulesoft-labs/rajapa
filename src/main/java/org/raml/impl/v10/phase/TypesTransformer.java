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
package org.raml.impl.v10.phase;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.raml.impl.commons.nodes.PropertyNode;
import org.raml.impl.v10.nodes.types.InheritedPropertiesInjectedNode;
import org.raml.impl.v10.nodes.types.builtin.ObjectTypeNode;
import org.raml.impl.v10.nodes.types.builtin.UnionTypeNode;
import org.raml.nodes.ErrorNode;
import org.raml.nodes.KeyValueNode;
import org.raml.nodes.KeyValueNodeImpl;
import org.raml.nodes.Node;
import org.raml.nodes.StringNodeImpl;
import org.raml.nodes.snakeyaml.SYArrayNode;
import org.raml.nodes.snakeyaml.SYObjectNode;
import org.raml.nodes.snakeyaml.SYStringNode;
import org.raml.phase.Transformer;
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
            transformUnionTypeProperties(node, typesRoot);
        }
        else if (node instanceof ObjectTypeNode && node.get("type") instanceof SYArrayNode)
        {
            transformObjectTypeProperties(node, typesRoot);
        }
        return node;
    }

    private void transformObjectTypeProperties(Node node, SYObjectNode typesRoot)
    {
        Node properties = node.get("properties");
        final SYArrayNode typesNode = (SYArrayNode) node.get("type");
        if (typesNode != null)
        {
            Set<List<String>> typeCombinations = getAllPossibleTypes(typesNode);
            for (List<String> combination : typeCombinations)
            {
                Node originalProperties = properties != null ? properties.copy() : null;
                for (String objectType : combination)
                {
                    originalProperties = processType(typesRoot, originalProperties, objectType);
                }
                injectProperties((ObjectTypeNode) node, new StringNodeImpl(combination.toString()), (SYObjectNode) originalProperties);
            }
        }
    }

    private Node processType(SYObjectNode typesRoot, Node originalProperties, String objectType)
    {
        ObjectTypeNode typeNode = getType(typesRoot, objectType);
        if (originalProperties == null)
        {
            SYObjectNode newProperties = (SYObjectNode) typeNode.get("properties");
            if (newProperties != null)
            {
                originalProperties = newProperties.copy();
            }
        }
        else
        {
            List<PropertyNode> unionProperties = getTypeProperties(typeNode);
            addProperties(originalProperties, unionProperties);
        }
        return originalProperties;
    }

    private void transformUnionTypeProperties(Node node, SYObjectNode typesRoot)
    {
        Node properties = node.get("properties");
        if (properties != null)
        {
            final SYStringNode typeNode = (SYStringNode) node.get("type");
            if (typeNode != null)
            {
                for (String type : typeNode.getValue().split("\\|"))
                {
                    String trimmedType = StringUtils.trim(type);
                    ObjectTypeNode parentTypeNode = getType(typesRoot, trimmedType);
                    if (parentTypeNode == null)
                    {
                        Node errorNode = new ErrorNode("inexistent type definition for " + trimmedType);
                        errorNode.setSource(typeNode);
                        typeNode.replaceWith(errorNode);
                        // TODO - might be improved adding a multiple error for every type that's non existent - migueloliva - Apr 5, 2016
                        return; // let's quit after the first error
                    }
                    else
                    {
                        List<PropertyNode> unionProperties = getTypeProperties(parentTypeNode);
                        addProperties(properties, unionProperties);
                    }
                }
            }
        }
    }

    private void addProperties(Node properties, List<PropertyNode> unionProperties)
    {
        if (unionProperties != null)
        {
            for (PropertyNode property : unionProperties)
            {
                Node existingProperty = properties.get(property.getName());
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

    private Set<List<String>> getAllPossibleTypes(SYArrayNode typesNode)
    {
        List<Set<String>> types = Lists.newArrayList();
        for (Node typeNode : typesNode.getChildren())
        {
            String typeElement = ((SYStringNode) typeNode).getValue();
            Set<String> splitTypes = Sets.newLinkedHashSet();
            for (String type : typeElement.split("\\|"))
            {
                if (StringUtils.isNotBlank(StringUtils.trimToNull(type)))
                {
                    splitTypes.add(StringUtils.trim(type));
                }
            }
            types.add(splitTypes);
        }
        return Sets.cartesianProduct(types);
    }

    private SYObjectNode getTypesRoot(Node node)
    {
        final KeyValueNode keyValueNode = (KeyValueNode) NodeUtils.getAncestor(node, 3);
        return keyValueNode != null ? (SYObjectNode) keyValueNode.getValue() : null;
    }

    private List<PropertyNode> getTypeProperties(ObjectTypeNode node)
    {
        return node.getProperties();
    }

    private ObjectTypeNode getType(SYObjectNode node, String typeName)
    {
        return (ObjectTypeNode) node.get(typeName);
    }

    private void injectProperties(ObjectTypeNode node, StringNodeImpl key, SYObjectNode properties)
    {
        InheritedPropertiesInjectedNode injected = new InheritedPropertiesInjectedNode();
        KeyValueNode keyValue = new KeyValueNodeImpl(key, properties);
        setKeyPosition(key, properties, injected, keyValue);
        // node.addChild(injected);
        node.addInheritedProperties(injected);
    }

    private void setKeyPosition(StringNodeImpl key, SYObjectNode properties, Node injected, KeyValueNode keyValue)
    {
        key.setEndPosition(properties.getStartPosition());
        key.setStartPosition(properties.getStartPosition().leftShift(key.getValue().length()));
        injected.addChild(keyValue);
    }
}
