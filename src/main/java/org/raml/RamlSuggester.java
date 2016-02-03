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
package org.raml;

import org.raml.grammar.Raml10Grammar;
import org.raml.grammar.rule.Rule;
import org.raml.nodes.KeyValueNode;
import org.raml.nodes.Node;
import org.raml.suggester.Suggestion;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RamlSuggester
{


    public List<Suggestion> suggestions(String document, int offset)
    {
        final StringBuilder contextLine = new StringBuilder();
        int location = offset;
        char character = document.charAt(location);
        while (location > 0)
        {
            location--;
            if (character == '\n')
            {
                break;
            }
            contextLine.append(character);
            character = document.charAt(location);
        }
        int columnNumber = getColumnNumber(contextLine);

        final String header = document.substring(0, location);

        final String footer = getFooter(document, offset);

        /**
         * foo:
         *  soo:
         *     Z:
         *
         */
        final Node root = new RamlBuilder().build(header + footer);
        Node node = searchNodeAt(root, location);
        if (node != null)
        {
            int column = node.getStartPosition().getColumn();
            while (column > columnNumber && node != null)
            {
                node = node.getParent();
                if (node != null)
                {
                    column = node.getStartPosition().getColumn();
                }
            }
            if (node != null)
            {
                //
                if (node instanceof KeyValueNode)
                {
                    node = node.getParent();
                }
                final List<Node> path = new ArrayList<>();
                Node parent = node.getParent();
                while (parent != null)
                {
                    path.add(0, parent);
                    parent = parent.getParent();
                }

                final Raml10Grammar raml10Grammar = new Raml10Grammar();
                Rule rule = raml10Grammar.raml();
                for (Node element : path)
                {
                    rule = rule.getInnerRule(element);
                    if (rule == null)
                    {
                        return Collections.emptyList();
                    }
                }
                return rule.getSuggestions(node);
            }
        }
        return Collections.emptyList();

    }

    @Nonnull
    private String getFooter(String document, int offset)
    {
        int loc = offset;
        char current = document.charAt(loc);
        while (loc < document.length() && current != '\n')
        {
            loc++;
            current = document.charAt(loc);
        }

        return document.substring(loc);
    }

    private int getColumnNumber(StringBuilder contextLine)
    {
        int columnNumber = 0;
        for (int i = 0; i < contextLine.length(); i++)
        {
            if (Character.isWhitespace(contextLine.charAt(i)))
            {
                columnNumber++;
            }
            else
            {
                break;
            }
        }
        return columnNumber;
    }

    @Nullable
    private Node searchNodeAt(Node root, int location)
    {
        if (root.getEndPosition().getIndex() != location || !root.getChildren().isEmpty())
        {
            final List<Node> children = root.getChildren();
            for (Node child : children)
            {
                if (child.getEndPosition().getIndex() == location)
                {
                    if (child.getChildren().isEmpty())
                    {
                        return child;
                    }
                    else
                    {
                        return searchNodeAt(child, location);
                    }
                }
                else if (child.getEndPosition().getIndex() > location)
                {
                    return searchNodeAt(child, location);
                }
            }
            return null;
        }
        else
        {
            return root;
        }
    }


}
