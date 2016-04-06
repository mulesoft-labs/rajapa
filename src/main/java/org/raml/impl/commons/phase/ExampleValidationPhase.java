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
import org.raml.impl.commons.nodes.ExampleTypeNode;
import org.raml.impl.v10.nodes.types.InheritedPropertiesInjectedNode;
import org.raml.impl.v10.nodes.types.builtin.ObjectTypeNode;
import org.raml.nodes.Node;
import org.raml.phase.Phase;

public class ExampleValidationPhase implements Phase
{

    @Override
    public Node apply(Node tree)
    {
        final List<ExampleTypeNode> examples = tree.findDescendantsWith(ExampleTypeNode.class);
        Node types = tree.get("types");
        Node transform;
        ObjectTypeNode type;
        Rule rule;
        for (ExampleTypeNode example : examples)
        {
            transform = null;
            if (types != null)
            {
                type = (ObjectTypeNode) types.get(example.getTypeName());
                if (type != null)
                {
                    if (!type.getInheritedProperties().isEmpty())
                    {
                        List<Rule> inheritanceRules = getInheritanceRules(example, type);
                        rule = new AnyOfRule(inheritanceRules);
                    }
                    else
                    {
                        rule = example.visitProperties(new TypeToRuleVisitor(), type.getProperties());
                    }
                    transform = rule.apply(example);
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
