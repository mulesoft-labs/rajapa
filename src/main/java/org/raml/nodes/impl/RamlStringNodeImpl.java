package org.raml.nodes.impl;

import org.raml.nodes.RamlStringNode;

public class RamlStringNodeImpl extends AbstractRamlNode implements RamlStringNode {

    private String value;

    public RamlStringNodeImpl(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
