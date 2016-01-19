package org.raml.nodes;

/**
 * The position of a given node in a specific resource
 */
public interface Position
{
    int getIndex();

    int getLine();

    int getColumn();

    String getResource();
}
