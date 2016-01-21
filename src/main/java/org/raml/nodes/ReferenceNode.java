package org.raml.nodes;

import javax.annotation.Nullable;

public interface ReferenceNode extends Node
{

    String getRefName();

    @Nullable
    Node getRefNode();
}
