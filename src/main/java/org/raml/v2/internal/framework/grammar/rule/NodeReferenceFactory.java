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
package org.raml.v2.internal.framework.grammar.rule;

import org.raml.v2.internal.framework.nodes.AbstractRamlNode;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.nodes.Position;
import org.raml.v2.internal.impl.v10.nodes.LibraryRefNode;

import javax.annotation.Nonnull;
import java.text.CharacterIterator;

public class NodeReferenceFactory implements NodeFactory
{

    private NodeFactory defaultFactory;

    public NodeReferenceFactory(Class<? extends Node> referenceClassNode)
    {
        defaultFactory = new ClassNodeFactory(referenceClassNode);
    }

    @Override
    public Node create(@Nonnull Node currentNode, Object... args)
    {
        final String value = (String) args[0];
        return parse(currentNode, value, 0);
    }

    public Node parse(Node currentNode, String value, int startLocation)
    {

        final String[] parts = value.split("\\.");
        Node result = null;
        Node parent = null;
        int currentShift = value.length();
        for (int i = parts.length - 1; i >= 0; i--)
        {
            String part = parts[i];
            currentShift -= part.length();
            final Position endPosition = currentNode.getStartPosition().rightShift(startLocation + currentShift + value.length());
            final Position startPosition = currentNode.getStartPosition().rightShift(startLocation + currentShift);
            if (parent == null)
            {
                parent = defaultFactory.create(currentNode, part);
                if (parent instanceof AbstractRamlNode)
                {
                    ((AbstractRamlNode) parent).setStartPosition(startPosition);
                    ((AbstractRamlNode) parent).setEndPosition(endPosition);
                }
                result = parent;
            }
            else
            {
                final LibraryRefNode libraryRefNode = new LibraryRefNode(part);
                libraryRefNode.setStartPosition(startPosition);
                libraryRefNode.setEndPosition(endPosition);
                parent.addChild(libraryRefNode);
                parent = libraryRefNode;
                // The 1 is from the dot
                currentShift -= 1;
            }
        }
        return result;
    }
}
