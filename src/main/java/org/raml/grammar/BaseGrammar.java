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

    public MappingRule mapping()
    {
        return new MappingRule();
    }

    public MappingRule mapping(String name)
    {
        final MappingRule mapping = mapping();
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

    public IntegerTypeRule range(Range<BigInteger> range)
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

    public ReferenceRule ref(String name)
    {
        return new ReferenceRule(context, name);
    }

    public AnyOfRule optional(Rule rule)
    {
        return anyOf(rule, new NullValueRule());
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
