/*
 *
 */
package org.raml.nodes.impl;

import org.raml.grammar.Raml10Grammar;
import org.raml.nodes.Node;
import org.raml.utils.NodeSelector;

import javax.annotation.Nullable;

public class LibraryRefNode extends AbstractReferenceNode
{

    private String name;

    public LibraryRefNode(String name)
    {
        this.name = name;
    }

    @Override
    public String getRefName()
    {
        return name;
    }

    @Nullable
    @Override
    public Node getRefNode()
    {
        return NodeSelector.selectFrom(Raml10Grammar.USES_KEY_NAME + "/" + name, getRelativeNode());
    }
}
