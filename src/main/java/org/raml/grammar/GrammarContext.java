/*
 *
 */
package org.raml.grammar;

import org.raml.grammar.rule.Rule;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class GrammarContext
{

    private Map<String, Rule> ruleMap;

    public GrammarContext()
    {
        this.ruleMap = new HashMap<>();
    }

    public void registerRule(String name, Rule rule)
    {
        this.ruleMap.put(name, rule);
    }

    @Nullable
    public Rule getRuleByName(String name)
    {
        return ruleMap.get(name);
    }
}
