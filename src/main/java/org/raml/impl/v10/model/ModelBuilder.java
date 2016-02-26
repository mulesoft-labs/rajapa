/*
 * Copyright 2013 (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.raml.impl.v10.model;

import org.raml.nodes.Node;
import org.raml.utils.NodeSelector;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ModelBuilder
{

    public static <T> T create(Class<T> apiInterface, Node delegateNode)
    {
        return (T) java.lang.reflect.Proxy.newProxyInstance(
                apiInterface.getClassLoader(),
                new Class[] {apiInterface},
                new ModelProxy(delegateNode));
    }

    private static class ModelProxy implements InvocationHandler
    {

        private Node node;

        public ModelProxy(Node node)
        {
            this.node = node;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
        {
            final String name = method.getName();
            final Class<?> returnType = method.getReturnType();
            // Convention instrospection
            if (returnType.isPrimitive() || String.class.isAssignableFrom(returnType))
            {
                return NodeSelector.selectType(name, node, null);
            }
            else
            {
                return ModelBuilder.create(returnType, NodeSelector.selectFrom(name, node));
            }
        }
    }

}
