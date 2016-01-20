package org.raml.nodes;

public interface KeyValueNode extends Node
{

    Node getKey();

    Node getValue();
}
