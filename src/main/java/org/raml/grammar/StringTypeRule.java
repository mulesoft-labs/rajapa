package org.raml.grammar;

import org.raml.nodes.Node;
import org.raml.nodes.StringNode;

public class StringTypeRule extends Rule {
    @Override
    boolean matches(Node node) {
        return node instanceof StringNode;
    }

    @Override
    Node transform(Node node) {
        return node;
    }

    @Override
    String getDescription() {
        return "String";
    }
}
