package org.raml.grammar;

import org.raml.nodes.KeyValueNode;
import org.raml.nodes.Node;

import javax.annotation.Nullable;

public class KeyValueRule extends Rule {

    @Nullable
    private Class<? extends KeyValueNode> clazz;

    public KeyValueRule(@Nullable Class<? extends KeyValueNode> clazz, Rule keyRule, Rule valueRule) {
        this.clazz = clazz;
        addSubRule(keyRule);
        addSubRule(valueRule);
    }

    @Override
    boolean matches(Node node) {
        if (node instanceof KeyValueNode) {
            return getKeyRule().matches(((KeyValueNode) node).getKey());
        } else {
            return false;
        }
    }

    private Rule getKeyRule() {
        return getSubRules().get(0);
    }

    private Rule getValueRule() {
        return getSubRules().get(1);
    }

    @Override
    Node transform(Node node) {
        try {
            return clazz == null ? node : clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    String getDescription() {
        return getKeyRule().getDescription() + ": " + getValueRule().getDescription();
    }
}
