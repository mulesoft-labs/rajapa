/*
 *
 */
package org.raml.grammar.rule;

import org.raml.nodes.Node;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class Rule
{

    @Nullable
    private NodeFactory factory;

    protected Rule()
    {

    }

    public abstract boolean matches(@Nonnull Node node);

    public abstract Node transform(@Nonnull Node node);

    public abstract String getDescription();

    @Nullable
    public NodeFactory getFactory()
    {
        return factory;
    }

    public Rule then(Class<? extends Node> clazz)
    {
        this.factory = new ClassNodeFactory(clazz);
        return this;
    }

    public Rule then(NodeFactory factory)
    {
        this.factory = factory;
        return this;
    }


}
