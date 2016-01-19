package org.raml.phase;

import org.raml.nodes.RamlNode;

/**
 * Applies a transformation to a specific node and returns the new node.
 */
public interface Transformer {

    boolean matches(RamlNode node);

    RamlNode transform(RamlNode node);

}
