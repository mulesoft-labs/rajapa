package org.raml.grammar;

import org.raml.nodes.Node;

import java.util.ArrayList;
import java.util.List;

public abstract class Rule {

    private List<Rule> rules = new ArrayList<>();

    abstract boolean matches(Node node);

    abstract Node transform(Node node);

    abstract String getDescription();

    public List<Rule> getSubRules() {
        return rules;
    }

    public Rule addSubRule(Rule rule){
        this.rules.add(rule);
        return this;
    }
}
