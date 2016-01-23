/*
 *
 */
package org.raml.grammar.rule;

import org.raml.nodes.Node;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

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
            if (args != null)
            {
                Class[] types = new Class[args.length];
                for (int i = 0; i < args.length; i++)
                {
                    Object arg = args[i];
                    types[i] = arg.getClass();
                }
                try
                {
                    final Constructor constructor = clazz.getConstructor(types);
                    return clazz.cast(constructor.newInstance(args));
                }
                catch (NoSuchMethodException ignored)
                {
                    // If no constructor with the arguments try default constructor
                }
                catch (InvocationTargetException e)
                {
                    throw new RuntimeException(e);
                }
            }
            return clazz.newInstance();
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }
}
