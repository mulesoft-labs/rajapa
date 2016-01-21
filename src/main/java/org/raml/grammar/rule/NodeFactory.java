/*
 *
 */
package org.raml.grammar.rule;

import org.raml.nodes.Node;

public interface NodeFactory
{
    Node create(Object... args);
}
