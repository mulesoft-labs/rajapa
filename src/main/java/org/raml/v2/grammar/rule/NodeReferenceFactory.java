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
package org.raml.v2.grammar.rule;

import org.raml.v2.nodes.Node;
import org.raml.v2.impl.v10.nodes.LibraryRefNode;

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