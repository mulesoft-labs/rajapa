package org.raml.nodes;

public interface ReferenceNode extends Node
{

    String getRefName();

    Node getRefNode();
}
