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

public class RamlSuggester {


    public List<Suggestion> suggestions(String document, int offset) {
        final StringBuilder contextLine = new StringBuilder();
        int location = offset;
        char character = document.charAt(location);
        while (location > 0 && character != '\n') {
            location--;
            contextLine.append(character);
            character = document.charAt(location);

        }
        int columnNumber = getColumnNumber(contextLine);

        final String header = document.substring(0, location);

        final String footer = getFooter(document, offset);


        final String realDocument = header + footer;

        final Node root = new RamlBuilder().build(realDocument);
        Node node = searchNodeAt(root, location);
        if (node != null) {
            node = getValueNodeAtColumn(columnNumber, node);
            //Recreate path with the node at the correct indentation
            final List<Node> pathToRoot = createPathToRoot(node);
            final Raml10Grammar raml10Grammar = new Raml10Grammar();
            //Follow the path from the root to the node and apply the rules for auto-completion.
            final Rule rootRule = raml10Grammar.raml();
            return rootRule.getSuggestions(pathToRoot);
        }
        return Collections.emptyList();

    }

    private Node getValueNodeAtColumn(int columnNumber, Node node) {
        if (columnNumber == 0) {
            return node.getRootNode();
        } else {
            //Create the path from the selected node to the root.
            final List<Node> path = createPathToRoot(node);
            //Find the node with the indentation in the path
            for (Node element : path) {
                if (element instanceof KeyValueNode) {
                    if (element.getStartPosition().getColumn() <= columnNumber) {
                        node = ((KeyValueNode) element).getValue();
                    }
                }
            }
            return node;
        }
    }

    @Nonnull
    private List<Node> createPathToRoot(Node node) {
        final List<Node> path = new ArrayList<>();
        Node parent = node;
        while (parent != null) {
            path.add(0, parent);
            parent = parent.getParent();
        }
        return path;
    }

    @Nonnull
    private String getFooter(String document, int offset) {
        int loc = offset;
        char current = document.charAt(loc);
        loc++;
        while (loc < document.length() && current != '\n') {
            current = document.charAt(loc);
            loc++;
        }

        return loc < document.length() ? document.substring(loc) : "";
    }

    private int getColumnNumber(StringBuilder contextLine) {
        int columnNumber = 0;
        for (int i = 0; i < contextLine.length(); i++) {
            if (Character.isWhitespace(contextLine.charAt(i))) {
                columnNumber++;
            } else {
                break;
            }
        }
        return columnNumber;
    }

    @Nullable
    private Node searchNodeAt(Node root, int location) {
        if (root.getEndPosition().getIndex() != location || !root.getChildren().isEmpty()) {
            final List<Node> children = root.getChildren();
            for (Node child : children) {
                if (child.getEndPosition().getIndex() == location) {
                    if (child.getChildren().isEmpty()) {
                        return child;
                    } else {
                        return searchNodeAt(child, location);
                    }
                } else if (child.getEndPosition().getIndex() > location) {
                    return searchNodeAt(child, location);
                }
            }
            return null;
        } else {
            return root;
        }
    }


}
