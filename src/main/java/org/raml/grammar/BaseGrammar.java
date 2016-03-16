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
package org.raml.grammar;

import com.google.common.collect.Range;
import org.raml.grammar.rule.*;

import javax.annotation.Nonnull;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.regex.Pattern;

public class BaseGrammar
{

    private GrammarContext context;

    public BaseGrammar()
    {
        this.context = new GrammarContext();
    }

    public ObjectRule objectType()
    {
        return new ObjectRule();
    }

    public ObjectRule objectType(String name)
    {
        final ObjectRule mapping = objectType();
        this.context.registerRule(name, mapping);
        return mapping;
    }

    public AnyValueRule any()
    {
        return new AnyValueRule();
    }

    public ArrayValueRule array(Rule of)
    {
        return new ArrayValueRule(of);
    }

    public IntegerTypeRule integerType()
    {
        return new IntegerTypeRule(null);
    }

    public IntegerTypeRule range(Range<Integer> range)
    {
        return new IntegerTypeRule(range);
    }

    public IntegerValueRule integer(Integer value)
    {
        return new IntegerValueRule(new BigInteger(value.toString()));
    }

    public KeyValueRule field(Rule keyRule, Rule valueRule)
    {
        return new KeyValueRule(keyRule, optional(valueRule));
    }

    public KeyValueRule fieldWithRequiredValue(Rule keyRule, Rule valueRule)
    {
        return new KeyValueRule(keyRule, valueRule);
    }

    public KeyValueRule requiredField(Rule keyRule, Rule valueRule)
    {
        return new KeyValueRule(keyRule, valueRule).required();
    }

    public StringTypeRule stringType()
    {
        return new StringTypeRule();
    }

    public BooleanTypeRule booleanType()
    {
        return new BooleanTypeRule();
    }

    public StringValueRule string(String value)
    {
        return new StringValueRule(value);
    }

    public RegexValueRule regex(String pattern)
    {
        return new RegexValueRule(Pattern.compile(pattern));
    }

    public AnyOfRule anyOf(Rule... rules)
    {
        return new AnyOfRule(Arrays.asList(rules));
    }

    public NegativeRule not(Rule rule)
    {
        return new NegativeRule(rule);
    }

    public AllOfRule allOf(Rule... rules)
    {
        return new AllOfRule(Arrays.asList(rules));
    }

    public ReferenceRule ref(String name)
    {
        return new ReferenceRule(context, name);
    }

    public AnyOfRule optional(Rule rule)
    {
        return anyOf(rule, nullValue());
    }

    @Nonnull
    protected NullValueRule nullValue()
    {
        return new NullValueRule();
    }

    public ConditionalRules when(String expr, ConditionalRule... cases)
    {
        return new ConditionalRules(expr, cases);
    }

    public ConditionalRule is(Rule rule)
    {
        return new ConditionalRule(rule);
    }

}
