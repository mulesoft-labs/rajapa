package org.raml.nodes.impl;

import org.raml.nodes.StringNode;

public class RamlStringNodeImpl extends AbstractRamlNode implements StringNode {

    private String value;

    public RamlStringNodeImpl(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
