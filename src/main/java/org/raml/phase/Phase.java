package org.raml.phase;

import org.raml.nodes.RamlNode;

/**
 * A phase applies a given logic into a node recursively.
 */
public interface Phase
{

    RamlNode apply(RamlNode tree);
}
