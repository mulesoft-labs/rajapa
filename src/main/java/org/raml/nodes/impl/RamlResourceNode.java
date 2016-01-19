package org.raml.nodes.impl;

import org.raml.nodes.*;

public class RamlResourceNode extends RamlKeyValueNodeImpl {


    private StringNode resourceName;

    public RamlResourceNode(StringNode resourceName, Node value) {
        super(resourceName, value);
        this.resourceName = resourceName;
    }

    public StringNode getResourceName() {
        return resourceName;
    }

}
