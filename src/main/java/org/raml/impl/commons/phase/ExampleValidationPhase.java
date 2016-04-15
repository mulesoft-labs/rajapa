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
package org.raml.impl.commons.phase;

import com.google.common.collect.Lists;

import java.util.List;

import org.raml.grammar.rule.AnyOfRule;
import org.raml.grammar.rule.Rule;
import org.raml.grammar.rule.JsonSchemaValidationRule;
import org.raml.grammar.rule.XmlSchemaValidationRule;
import org.raml.impl.commons.model.BuiltInType;
import org.raml.impl.commons.nodes.ExampleTypeNode;
import org.raml.impl.commons.nodes.MultipleExampleTypeNode;
import org.raml.impl.v10.nodes.types.InheritedPropertiesInjectedNode;
import org.raml.impl.v10.nodes.types.builtin.ObjectTypeNode;
import org.raml.impl.v10.nodes.types.builtin.TypeNode;
import org.raml.loader.ResourceLoader;
import org.raml.nodes.KeyValueNode;
import org.raml.nodes.KeyValueNodeImpl;
import org.raml.nodes.Node;
import org.raml.nodes.ObjectNode;
import org.raml.nodes.StringNode;
import org.raml.nodes.snakeyaml.RamlNodeParser;
import org.raml.nodes.snakeyaml.SYStringNode;
import org.raml.phase.Phase;

public class ExampleValidationPhase implements Phase
{

    private ResourceLoader resourceLoader;
    private final String actualPath;


    public ExampleValidationPhase(ResourceLoader resourceLoader, String actualPath)
    {
        this.resourceLoader = resourceLoader;
        this.actualPath = actualPath;
    }

    @Override
    public Node apply(Node tree)
    {
        final List<ExampleTypeNode> examples = tree.findDescendantsWith(ExampleTypeNode.class);
        Node types = tree.get("types");
        Node transform;
        ObjectTypeNode type;
        for (ExampleTypeNode example : examples)
        {
            Rule rule = null;
            transform = null;
            String typeName = example.getTypeName();
            if (types != null && !BuiltInType.isBuiltInType(typeName) && !isBuiltInTypeAlias(typeName, tree))
            {
                type = (ObjectTypeNode) types.get(typeName);
                if (type != null)
                {
                    Node schemaType = type.get("type");
                    if (schemaType != null && schemaType instanceof StringNode && (((StringNode) schemaType).getValue().startsWith("{") || ((StringNode) schemaType).getValue().startsWith("<")))
                    {
                        String value = ((StringNode) schemaType).getValue();
                        if (value.startsWith("{"))
                        {
                            rule = new JsonSchemaValidationRule(value);
                        }
                        else if (value.startsWith("<"))
                        {
                            rule = new XmlSchemaValidationRule(value, resourceLoader, actualPath);
                        }
                    }
                    else if (!type.getInheritedProperties().isEmpty())
                    {
                        List<Rule> inheritanceRules = getInheritanceRules(example, type);
                        rule = new AnyOfRule(inheritanceRules);
                    }
                    else
                    {
                        rule = example.visitProperties(new TypeToRuleVisitor(), type.getProperties());
                    }
                    if (example instanceof MultipleExampleTypeNode || example.isArrayExample())
                    {
                        for (Node childExample : example.getChildren())
                        {
                            Node exampleValue;
                            if (childExample instanceof KeyValueNode)
                            {
                                exampleValue = ((KeyValueNodeImpl) childExample).getValue();
                            }
                            else if (childExample instanceof ObjectNode)
                            {
                                exampleValue = childExample;
                            }
                            else
                            {
                                break;
                            }
                            transform = rule.apply(exampleValue);
                            exampleValue.replaceWith(transform);
                            transform = null;
                        }
                    }
                    else
                    {
                        if (example.getSource() instanceof StringNode && !(rule instanceof JsonSchemaValidationRule || rule instanceof XmlSchemaValidationRule))
                        {
                            Node transformed = RamlNodeParser.parse(((SYStringNode) example.getSource()).getValue());
                            transform = rule.apply(transformed);
                        }
                        else
                        {
                            transform = rule.apply(example);
                        }
                    }
                }
            }
            else
            {
                rule = example.visit(new TypeToRuleVisitor());
                transform = rule.apply(example.getSource());
            }
            if (transform != null)
            {
                example.replaceWith(transform);
            }
        }
        return tree;
    }

    private boolean isBuiltInTypeAlias(String typeName, Node tree)
    {
        Node types = tree.get("types");
        if (types != null)
        {
            Node type = types.get(typeName);
            if (type != null && type.get("type") != null && type.get("type") instanceof StringNode)
            {
                String objectType = ((StringNode) type.get("type")).getValue();
                return BuiltInType.isBuiltInType(objectType);
            }
        }
        return false;
    }

    private List<Rule> getInheritanceRules(ExampleTypeNode example, ObjectTypeNode type)
    {
        List<Rule> rules = Lists.newArrayList();
        for (InheritedPropertiesInjectedNode inheritedProperties : type.getInheritedProperties())
        {
            rules.add(example.visitProperties(new TypeToRuleVisitor(), inheritedProperties.getProperties()));
        }
        return rules;
    }
}
