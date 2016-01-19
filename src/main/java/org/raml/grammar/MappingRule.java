package org.raml.grammar;

import org.raml.nodes.Node;
import org.raml.nodes.ObjectNode;

import javax.annotation.Nullable;

public class MappingRule extends Rule {

    private Class<? extends Node> clazz;

    public MappingRule(@Nullable Class<? extends Node> clazz) {
        this.clazz = clazz;
    }

    @Override
    public boolean matches(Node node) {
        return node instanceof ObjectNode;
    }

    @Override
    public Node transform(Node node) {
        try {
            return clazz == null ? node : clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    String getDescription() {
        return "Mapping";
    }
}
