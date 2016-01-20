package org.raml.grammar.rule;

import org.raml.grammar.rule.NodeFactory;
import org.raml.nodes.Node;

public class ClassNodeFactory implements NodeFactory
{

    private Class<? extends Node> clazz;

    public ClassNodeFactory(Class<? extends Node> clazz)
    {
        this.clazz = clazz;
    }

    @Override
    public Node create(Object... args)
    {
        try
        {
            return clazz.newInstance();
        }
        catch (InstantiationException e)
        {
            throw new RuntimeException(e);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }
}
