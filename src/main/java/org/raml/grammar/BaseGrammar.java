/*
 *
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

    public IntegerTypeRule range(Range range)
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
}
