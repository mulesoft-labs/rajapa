/*
 *
 */
package org.raml.phase;

import org.raml.nodes.Node;

/**
 * A phase applies a given logic into a node recursively.
 */
public interface Phase
{

    Node apply(Node tree);
}
