/*
 *
 */
package org.raml.phase;

import org.raml.nodes.Node;

/**
 * Applies a transformation to a specific node and returns the new node.
 */
public interface Transformer
{

    boolean matches(Node node);

    Node transform(Node node);

}
