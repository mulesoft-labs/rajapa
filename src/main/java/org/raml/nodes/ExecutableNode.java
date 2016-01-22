/*
 *
 */
package org.raml.nodes;

public interface ExecutableNode extends Node
{

    Node execute(ExecutionContext context);
}
