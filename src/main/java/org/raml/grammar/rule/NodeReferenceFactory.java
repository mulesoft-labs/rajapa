/*
 *
 */
package org.raml.grammar.rule;

import org.raml.nodes.Node;
import org.raml.nodes.impl.LibraryRefNode;

public class NodeReferenceFactory implements NodeFactory
{

    private NodeFactory defaultFactory;

    public NodeReferenceFactory(Class<? extends Node> referenceClassNode)
    {
        defaultFactory = new ClassNodeFactory(referenceClassNode);
    }

    @Override
    public Node create(Object... args)
    {
        final String value = (String) args[0];
        final String[] parts = value.split("\\.");
        Node result = null;
        Node parent = null;
        for (int i = parts.length - 1; i >= 0; i--)
        {
            String part = parts[i];
            if (parent == null)
            {
                parent = defaultFactory.create(part);
                result = parent;
            }
            else
            {
                final LibraryRefNode libraryRefNode = new LibraryRefNode(part);
                parent.addChild(libraryRefNode);
                parent = libraryRefNode;
            }

        }

        return result;
    }
}
