package org.raml.grammar;

import org.raml.nodes.KeyValueNode;
import org.raml.nodes.Node;

public class Rules {

    public static MappingRule mapping(Class<? extends Node> clazz) {
        return new MappingRule(clazz);
    }

    public static KeyValueRule field(Rule keyRule, Rule valueRule) {
        return field(null, keyRule, valueRule);
    }

    public static KeyValueRule field(Class<? extends KeyValueNode> clazz, Rule keyRule, Rule valueRule) {
        return new KeyValueRule(clazz, keyRule, valueRule);
    }

    public static StringTypeRule stringType() {
        return new StringTypeRule();
    }

    public static StringValueRule string(String value) {
        return new StringValueRule(value);
    }

}
