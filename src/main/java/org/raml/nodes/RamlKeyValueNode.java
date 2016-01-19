package org.raml.nodes;

public interface RamlKeyValueNode extends RamlNode {

    RamlNode getKey();

    RamlNode getValue();
}
