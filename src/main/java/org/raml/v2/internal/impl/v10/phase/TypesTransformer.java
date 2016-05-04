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
package org.raml.v2.impl.v10.phase;

import static org.raml.v2.internal.utils.NodeUtils.*;
import static org.raml.v2.internal.utils.SchemaGenerator.*;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.raml.v2.internal.framework.grammar.rule.ErrorNodeFactory;
import org.raml.v2.impl.commons.model.BuiltInScalarType;
import org.raml.v2.impl.commons.nodes.PropertyNode;
import org.raml.v2.impl.v10.nodes.types.InheritedPropertiesInjectedNode;
import org.raml.v2.impl.v10.nodes.types.builtin.ObjectTypeNode;
import org.raml.v2.impl.v10.nodes.types.builtin.TypeNode;
import org.raml.v2.impl.v10.nodes.types.builtin.UnionTypeNode;
import org.raml.v2.nodes.*;
import org.raml.v2.nodes.snakeyaml.SYArrayNode;
import org.raml.v2.nodes.snakeyaml.SYNullNode;
import org.raml.v2.nodes.snakeyaml.SYObjectNode;
import org.raml.v2.nodes.snakeyaml.SYStringNode;
import org.raml.v2.internal.framework.phase.Transformer;
import org.raml.v2.internal.utils.SchemaGenerator;

public class TypesTransformer implements Transformer
{

    private String actualPath;

    public TypesTransformer(String actualPath)
    {
        this.actualPath = actualPath;
    }

    public TypesTransformer()
    {
    }


    @Override
    public boolean matches(Node node)
    {
        return node instanceof ObjectTypeNode;
    }

    @Override
    public Node transform(Node node)
    {
        if (node instanceof UnionTypeNode)
        {
            transformUnionTypeProperties(node);
        }
        else if (node instanceof ObjectTypeNode && getType(node) instanceof SYArrayNode)
        {
            transformObjectTypeProperties(node);
        }

        validateGeneratedProperties(node);
        return node;
    }

    private void validateGeneratedProperties(Node node)
    {
        if (node instanceof ObjectTypeNode)
        {
            ObjectTypeNode objectNode = (ObjectTypeNode) node;
            Integer totalNumberOfProperties = getNumberOfProperties(node);
            if (objectNode.getMinProperties() != null)
            {
                if (objectNode.getMinProperties().compareTo(totalNumberOfProperties) > 0)
                {
                    node.replaceWith(ErrorNodeFactory.createInvalidNumberOfProperties("minimum", objectNode.getMinProperties(), totalNumberOfProperties));
                }
            }
            if (objectNode.getMaxProperties() != null)
            {
                if (objectNode.getMaxProperties().compareTo(totalNumberOfProperties) < 0)
                {
                    node.replaceWith(ErrorNodeFactory.createInvalidNumberOfProperties("maximum", objectNode.getMaxProperties(), totalNumberOfProperties));
                }
            }
        }
    }

    private Integer getNumberOfProperties(Node node)
    {
        Node properties = node.get("properties");
        if (properties == null)
        {
            return 0;
        }
        else
        {
            return properties.getChildren().size();
        }
    }

    private void transformObjectTypeProperties(Node node)
    {
        Node properties = node.get("properties");
        final SYArrayNode typesNode = (SYArrayNode) getType(node);
        if (typesNode != null)
        {
            Set<List<String>> typeCombinations = validateAndGetPossibleTypes(typesNode);
            for (List<String> combination : typeCombinations)
            {
                Node originalProperties = properties != null ? properties.copy() : null;
                for (String objectTypeName : combination)
                {
                    originalProperties = processType(originalProperties, node, objectTypeName);
                }
                injectProperties((ObjectTypeNode) node, new StringNodeImpl(combination.toString()), (SYObjectNode) originalProperties);
            }
        }
    }

    private Node processType(Node originalProperties, Node context, String objectType)
    {
        TypeNode typeNode = getType(objectType, context);

        if (typeNode instanceof ObjectTypeNode)
        {
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
                List<PropertyNode> unionProperties = getTypeProperties((ObjectTypeNode) typeNode);
                addProperties(originalProperties, unionProperties);
            }
        }
        return originalProperties;
    }

    private void transformUnionTypeProperties(Node node)
    {

        final StringNode typeNode = (StringNode) getType(node);
        if (SchemaGenerator.isSchemaNode(node))
        {
            SchemaGenerator.wrapNode(node, actualPath);
            return;
        }
        validateInheritedTypes(typeNode);

        final Node properties = node.get("properties");
        if (properties != null)
        {
            Node unionProperties;
            if (typeNode != null)
            {
                for (final String type : getSplitTypes(typeNode.getValue()))
                {
                    final String trimmedType = StringUtils.trim(type);
                    unionProperties = processType(properties.copy(), node, trimmedType);
                    if (unionProperties != null && !(unionProperties instanceof SYNullNode))
                    {
                        injectProperties((ObjectTypeNode) node, new StringNodeImpl(trimmedType), (SYObjectNode) unionProperties);
                    }
                }
            }
        }
        else if (getType(node) instanceof StringNode)
        {
            String trimmedType = StringUtils.trim(((StringNode) getType(node)).getValue());
            if ("array".equals(trimmedType))
            {
                return;
            }
            if (isSchemaNode(getType(node)))
            {
                SchemaGenerator.wrapNode(getType(node), actualPath);
                return;
            }

            final TypeNode parentTypeNodeGeneral = getType(trimmedType, typeNode);
            if (parentTypeNodeGeneral instanceof ObjectTypeNode)
            {
                final ObjectTypeNode parentTypeNode = (ObjectTypeNode) parentTypeNodeGeneral;

                if (parentTypeNode.get("properties") != null)
                {
                    injectProperties((ObjectTypeNode) node, new StringNodeImpl(trimmedType), (SYObjectNode) parentTypeNode.get("properties"));
                }
                if (!parentTypeNode.getInheritedProperties().isEmpty())
                {
                    ((ObjectTypeNode) node).setInheritedProperties(parentTypeNode.getInheritedProperties());
                }
                else if (isSchemaNode(getType(parentTypeNode)))
                {
                    SchemaNodeImpl schemaNode = new SchemaNodeImpl((StringNodeImpl) getType(parentTypeNode), actualPath);
                    getType(node).replaceWith(schemaNode);
                    return;
                }
            }
        }
    }

    private String[] getSplitTypes(String types)
    {
        return StringUtils.replaceEach(types, new String[] {"(", ")"}, new String[] {"", ""}).split("\\|");
    }

    private void validateInheritedTypes(final StringNode typeNode)
    {
        if (typeNode != null)
        {
            if (isCustomRamlType(typeNode) && !SchemaGenerator.isSchemaNode(typeNode))
            {
                for (final String type : getSplitTypes(typeNode.getValue()))
                {
                    final String trimmedType = StringUtils.trim(type);
                    final TypeNode parentTypeNode = getType(trimmedType, typeNode);
                    if (parentTypeNode == null)
                    {
                        final Node errorNode = ErrorNodeFactory.createInexistentType(trimmedType);
                        typeNode.replaceWith(errorNode);
                    }
                }
            }
        }
    }

    private boolean isCustomRamlType(StringNode typeNode)
    {
        final String typeNodeValue = typeNode.getValue();
        return !BuiltInScalarType.isBuiltInScalarType(typeNodeValue) && !typeNodeValue.equals("array") && !typeNodeValue.equals("object") && !isSchemaNode(typeNode);
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
                    Node errorNode = new ErrorNode("property definition {" + existingProperty.getParent() + "} overrides existing property: {" + property + "}");
                    errorNode.setSource(existingProperty);
                    properties.addChild(errorNode);
                }
                else
                {
                    properties.addChild(property);
                }
            }
        }
    }

    private Set<List<String>> validateAndGetPossibleTypes(SYArrayNode typesNode)
    {
        List<Set<String>> types = Lists.newArrayList();
        for (Node typeNode : typesNode.getChildren())
        {
            String typeElement = ((SYStringNode) typeNode).getValue();
            Set<String> splitTypes = Sets.newLinkedHashSet();
            for (String type : getSplitTypes(typeElement))
            {
                if (StringUtils.isNotBlank(StringUtils.trimToNull(type)))
                {
                    final String objectType = StringUtils.trim(type);

                    final TypeNode typeDefinition = getType(objectType, typeNode);
                    if (typeDefinition == null)
                    {
                        Node error = ErrorNodeFactory.createInexistentType(objectType);
                        typeNode.replaceWith(error);
                    }
                    else
                    {
                        splitTypes.add(objectType);
                    }
                }
            }
            types.add(splitTypes);
        }
        return Sets.cartesianProduct(types);
    }

    private List<PropertyNode> getTypeProperties(ObjectTypeNode node)
    {
        return node.getProperties();
    }

    private void injectProperties(ObjectTypeNode node, StringNodeImpl key, SYObjectNode properties)
    {
        InheritedPropertiesInjectedNode injected = new InheritedPropertiesInjectedNode();
        KeyValueNode keyValue = new KeyValueNodeImpl(key, properties);
        setKeyPosition(key, properties, injected, keyValue);
        // node.addChild(injected);
        injected.setParent(node);
        node.addInheritedProperties(injected);
    }

    private void setKeyPosition(StringNodeImpl key, SYObjectNode properties, Node injected, KeyValueNode keyValue)
    {
        key.setEndPosition(properties.getStartPosition());
        key.setStartPosition(properties.getStartPosition().leftShift(key.getValue().length()));
        injected.addChild(keyValue);
    }
}
