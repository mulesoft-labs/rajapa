package org.raml.grammar;

import org.raml.nodes.Node;
import org.raml.nodes.StringNode;

public class StringValueRule extends Rule {

    private String value;

    public StringValueRule(String value) {
        this.value = value;
    }

    @Override
    boolean matches(Node node) {
        return node instanceof StringNode && ((StringNode) node).getValue().equals(value);
    }

    @Override
    Node transform(Node node) {
        return node;
    }

    @Override
    String getDescription() {
        return "\"" + value + "\"";
    }
}
