package org.raml.nodes.impl;

import org.raml.nodes.*;
import org.raml.nodes.impl.RamlKeyValueNodeImpl;

public class RamlResourceNode extends RamlKeyValueNodeImpl {


    private RamlStringNode resourceName;

    public RamlResourceNode(RamlStringNode resourceName, RamlNode value) {
        super(resourceName, value);
        this.resourceName = resourceName;
    }

    public RamlStringNode getResourceName() {
        return resourceName;
    }

}
