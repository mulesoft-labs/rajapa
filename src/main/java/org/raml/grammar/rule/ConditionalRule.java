package org.raml.grammar.rule;

import org.raml.nodes.Node;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ConditionalRule
{
    private Rule condition;
    private List<KeyValueRule> rules;

    public ConditionalRule(Rule condition)
    {
        this.condition = condition;
        this.rules = new ArrayList<>();
    }

    public boolean matches(@Nonnull Node node)
    {
        return condition.matches(node);
    }

    public ConditionalRule add(@Nonnull KeyValueRule rule)
    {
        rules.add(rule);
        return this;
    }

    @Nonnull
    public List<KeyValueRule> getRules()
    {
        return rules;
    }
}
