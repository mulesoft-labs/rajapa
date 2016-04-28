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
package org.raml.v2.impl.commons.phase;

import com.google.common.collect.Lists;

import java.math.BigInteger;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.raml.v2.grammar.rule.AllOfRule;
import org.raml.v2.grammar.rule.AnyOfRule;
import org.raml.v2.grammar.rule.BooleanTypeRule;
import org.raml.v2.grammar.rule.DateValueRule;
import org.raml.v2.grammar.rule.DivisorValueRule;
import org.raml.v2.grammar.rule.IntegerTypeRule;
import org.raml.v2.grammar.rule.IntegerValueRule;
import org.raml.v2.grammar.rule.KeyValueRule;
import org.raml.v2.grammar.rule.MaxLengthRule;
import org.raml.v2.grammar.rule.MaximumValueRule;
import org.raml.v2.grammar.rule.MinLengthRule;
import org.raml.v2.grammar.rule.MinimumValueRule;
import org.raml.v2.grammar.rule.ObjectListRule;
import org.raml.v2.grammar.rule.ObjectRule;
import org.raml.v2.grammar.rule.RangeValueRule;
import org.raml.v2.grammar.rule.RegexValueRule;
import org.raml.v2.grammar.rule.Rule;
import org.raml.v2.grammar.rule.StringTypeRule;
import org.raml.v2.grammar.rule.StringValueRule;
import org.raml.v2.impl.commons.nodes.PropertyNode;
import org.raml.v2.impl.v10.nodes.types.InheritedPropertiesInjectedNode;
import org.raml.v2.impl.v10.nodes.types.builtin.BooleanTypeNode;
import org.raml.v2.impl.v10.nodes.types.builtin.DateTypeNode;
import org.raml.v2.impl.v10.nodes.types.builtin.NumericTypeNode;
import org.raml.v2.impl.v10.nodes.types.builtin.ObjectTypeNode;
import org.raml.v2.impl.v10.nodes.types.builtin.StringTypeNode;
import org.raml.v2.impl.v10.nodes.types.builtin.TypeNode;
import org.raml.v2.impl.v10.nodes.types.builtin.TypeNodeVisitor;
import org.raml.v2.impl.v10.nodes.types.builtin.UnionTypeNode;


public class TypeToRuleVisitor implements TypeNodeVisitor<Rule>
{

    @Override
    public Rule visitString(StringTypeNode stringTypeNode)
    {
        final AllOfRule typeRule = new AllOfRule(new StringTypeRule());
        if (StringUtils.isNotEmpty(stringTypeNode.getPattern()))
        {
            typeRule.and(new RegexValueRule(Pattern.compile(stringTypeNode.getPattern())));
        }

        if (!stringTypeNode.getEnumValues().isEmpty())
        {
            typeRule.and(new AnyOfRule(Lists.newArrayList(getStringRules(stringTypeNode.getEnumValues()))));
        }

        if (stringTypeNode.getMaxLength() != null)
        {
            Integer maxLength = stringTypeNode.getMaxLength();
            typeRule.and(new MaxLengthRule(maxLength));
        }

        if (stringTypeNode.getMinLength() != null)
        {
            Integer maxLength = stringTypeNode.getMinLength();
            typeRule.and(new MinLengthRule(maxLength));
        }
        return typeRule;

    }

    @Override
    public Rule visitObject(ObjectTypeNode objectTypeNode)
    {
        if (objectTypeNode.isArray())
        {
            return new ObjectListRule(getInheritanceRules(objectTypeNode));
        }
        else
        {
            return getInheritanceRules(objectTypeNode);
        }
    }

    public Rule getInheritanceRules(ObjectTypeNode objectTypeNode)
    {
        List<Rule> rules = Lists.newArrayList();
        if (!objectTypeNode.getInheritedProperties().isEmpty())
        {
            for (InheritedPropertiesInjectedNode inheritedProperties : objectTypeNode.getInheritedProperties())
            {
                rules.add(getPropertiesRules(inheritedProperties.getProperties()));
            }
        }
        else if (objectTypeNode.get("items") instanceof ObjectTypeNode)
        {
            if(!((ObjectTypeNode) objectTypeNode.get("items")).getInheritedProperties().isEmpty())
            {
                for (InheritedPropertiesInjectedNode inheritedProperties : ((ObjectTypeNode) objectTypeNode.get("items")).getInheritedProperties())
                {
                    rules.add(getPropertiesRules(inheritedProperties.getProperties()));
                }
            }
            else if(!((ObjectTypeNode) objectTypeNode.get("items")).getProperties().isEmpty())
            {
                rules.add(getPropertiesRules(((ObjectTypeNode) objectTypeNode.get("items")).getProperties()));
            }
        }
        if (!rules.isEmpty())
        {
            return new AnyOfRule(rules);
        }
        return getPropertiesRules(objectTypeNode.getProperties());
    }

    private ObjectRule getPropertiesRules(List<PropertyNode> properties)
    {
        ObjectRule objectRule = new ObjectRule();

        for (PropertyNode property : properties)
        {
            TypeNode typeNode = property.getTypeNode();
            KeyValueRule field = new KeyValueRule(new StringValueRule(property.getName()), typeNode.visit(new TypeToRuleVisitor()));
            if (property.isRequired())
            {
                field.required();
            }
            objectRule.with(field);
        }

        return objectRule;
    }


    @Override
    public Rule visitBoolean(BooleanTypeNode booleanTypeNode)
    {
        return new BooleanTypeRule();
    }

    @Override
    public Rule visitNumber(NumericTypeNode numericTypeNode)
    {
        final AllOfRule typeRule = new AllOfRule();
        typeRule.and(new IntegerTypeRule());

        if (numericTypeNode.getMinimum() != null && numericTypeNode.getMaximum() != null)
        {
            typeRule.and(new RangeValueRule(numericTypeNode.getMinimum(), numericTypeNode.getMaximum()));
        }
        else if (numericTypeNode.getMinimum() != null)
        {
            typeRule.and(new MinimumValueRule(numericTypeNode.getMinimum()));
        }
        else if (numericTypeNode.getMaximum() != null)
        {
            typeRule.and(new MaximumValueRule(numericTypeNode.getMaximum()));
        }

        if (!numericTypeNode.getEnumValues().isEmpty())
        {
            typeRule.and(new AnyOfRule(Lists.newArrayList(getNumericRules(numericTypeNode.getEnumValues()))));
        }

        if (numericTypeNode.getMultiple() != null)
        {
            typeRule.and(new DivisorValueRule(numericTypeNode.getMultiple()));
        }
        return typeRule;
    }

    @Override
    public Rule visitDate(DateTypeNode dateTypeNode)
    {
        return new DateValueRule(dateTypeNode.getDateType(), dateTypeNode.getRFC());
    }

    @Override
    public Rule visitExample(List<PropertyNode> properties, boolean allowsAdditionalProperties)
    {
        ObjectRule propertiesRules = getPropertiesRules(properties);
        propertiesRules.setStrict(true);
        propertiesRules.setAllowsAdditionalProperties(allowsAdditionalProperties);
        return propertiesRules;
    }

    private List<Rule> getStringRules(List<String> enumValues)
    {
        List<Rule> rules = Lists.newArrayList();
        for (String value : enumValues)
        {
            rules.add(new StringValueRule(value));
        }
        return rules;
    }


    private List<Rule> getNumericRules(List<Number> enumValues)
    {
        List<Rule> rules = Lists.newArrayList();
        for (Number value : enumValues)
        {
            rules.add(new IntegerValueRule(new BigInteger(value.toString())));
        }
        return rules;
    }
}
