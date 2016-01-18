package org.raml.visitor;

import org.raml.nodes.RamlNode;

public interface Phase
{

    RamlNode apply(RamlNode node);
}
