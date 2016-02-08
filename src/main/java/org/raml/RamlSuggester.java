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
import org.raml.nodes.ObjectNode;
import org.raml.suggester.DefaultSuggestion;
import org.raml.suggester.RamlContext;
import org.raml.suggester.RamlContextType;
import org.raml.suggester.Suggestion;
import org.raml.utils.Inflector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RamlSuggester
{


    public List<Suggestion> suggestions(String document, int offset)
    {
        final List<Suggestion> result = new ArrayList<>();
        final RamlContext ramlContext = getContext(document, offset);
        final int location = ramlContext.getLocation();
        final String content = ramlContext.getContent();
        final List<Suggestion> suggestions = getSuggestions(document, offset, ramlContext, location);
        if (content.isEmpty())
        {
            result.addAll(suggestions);
        }
        else
        {
            for (Suggestion suggestion : suggestions)
            {
                if (suggestion.getValue().startsWith(content))
                {
                    result.add(suggestion);
                }
            }
        }
        return result;

    }

    private List<Suggestion> getSuggestions(String document, int offset, RamlContext ramlContext, int location)
    {
        switch (ramlContext.getTokenType())
        {
        case FUNCTION_CALL:
            return getFunctionCallSuggestions();
        case STRING_TEMPLATE:
            return defaultParameters();
        case LIBRARY_CALL:
        case LIST_ITEM:
        case VALUE:
            return getSuggestionsAt(document, offset, location);
        default:
            return getSuggestionByColumn(document, offset, location);

        }
    }

    @Nonnull
    private List<Suggestion> defaultParameters()
    {
        List<Suggestion> suggestions = new ArrayList<>();
        suggestions.add(new DefaultSuggestion("resourcePath", "", ""));
        return suggestions;
    }

    @Nonnull
    private List<Suggestion> getFunctionCallSuggestions()
    {
        List<Suggestion> suggestions = new ArrayList<>();
        final Method[] declaredMethods = Inflector.class.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods)
        {
            if (Modifier.isStatic(declaredMethod.getModifiers()) && Modifier.isPublic(declaredMethod.getModifiers()))
            {
                suggestions.add(new DefaultSuggestion("!" + declaredMethod.getName(), "", declaredMethod.getName()));
            }
        }
        return suggestions;
    }

    private List<Suggestion> getSuggestionsAt(String document, int offset, int location)
    {
        final String header = document.substring(0, location + 1);
        final String footer = getFooter(document, offset);
        final String realDocument = header + footer;
        // We only run the first phase
        final Node root = new RamlBuilder(RamlBuilder.FIRST_PHASE).build(realDocument);
        Node node = searchNodeAt(root, location);
        if (node != null)
        {
            // Recreate path with the node at the correct indentation
            final List<Node> pathToRoot = createPathToRoot(node);
            final Raml10Grammar raml10Grammar = new Raml10Grammar();
            // Follow the path from the root to the node and apply the rules for auto-completion.
            final Rule rootRule = raml10Grammar.raml();
            return rootRule.getSuggestions(pathToRoot);
        }
        else
        {
            return Collections.emptyList();
        }
    }

    private List<Suggestion> getSuggestionByColumn(String document, int offset, int location)
    {
        // // I don't care column number unless is an empty new line
        int columnNumber = getColumnNumber(document, offset);
        final String header = document.substring(0, location + 1);
        final String footer = getFooter(document, offset);
        final String realDocument = header + footer;

        // We only run the first phase
        final Node root = new RamlBuilder(RamlBuilder.FIRST_PHASE).build(realDocument);
        Node node = searchNodeAt(root, location);
        if (node != null)
        {
            node = getValueNodeAtColumn(columnNumber, node);
            // Recreate path with the node at the correct indentation
            final List<Node> pathToRoot = createPathToRoot(node);
            final Raml10Grammar raml10Grammar = new Raml10Grammar();
            // Follow the path from the root to the node and apply the rules for auto-completion.
            final Rule rootRule = raml10Grammar.raml();
            return rootRule.getSuggestions(pathToRoot);
        }
        else
        {
            return Collections.emptyList();
        }
    }

    @Nonnull
    private RamlContext getContext(String document, int offset)
    {
        RamlContext context = null;
        int location = offset;
        final StringBuilder content = new StringBuilder();
        while (location >= 0 && context == null)
        {
            char character = document.charAt(location);
            switch (character)
            {
            case ':':
                context = new RamlContext(RamlContextType.VALUE, revertAndTrim(content), location + 1);
                break;
            case '-':
                context = new RamlContext(RamlContextType.LIST_ITEM, revertAndTrim(content), location);
                break;
            case '<':
                if (location > 0)
                {
                    if (document.charAt(location - 1) == '<')
                    {
                        location--;
                        final String contextContent = revertAndTrim(content);
                        final String[] split = contextContent.split("\\|");
                        if (split.length > 1)
                        {
                            context = new RamlContext(RamlContextType.FUNCTION_CALL, split[split.length - 1].trim(), location);
                        }
                        else if (contextContent.endsWith("|"))
                        {
                            context = new RamlContext(RamlContextType.FUNCTION_CALL, "", location);
                        }
                        else
                        {
                            context = new RamlContext(RamlContextType.STRING_TEMPLATE, contextContent, location);
                        }
                        break;
                    }
                }
                content.append(character);
                break;
            case '.':
                context = new RamlContext(RamlContextType.LIBRARY_CALL, revertAndTrim(content), location);
                break;
            case ',':
            case '\n':
                context = new RamlContext(RamlContextType.ANY, revertAndTrim(content), location);
                break;
            default:
                content.append(character);
            }
            location--;
        }

        if (context == null)
        {
            context = new RamlContext(RamlContextType.ANY, revertAndTrim(content), location);
        }

        return context;
    }

    @Nonnull
    private String revertAndTrim(StringBuilder content)
    {
        return content.reverse().toString().trim();
    }

    private Node getValueNodeAtColumn(int columnNumber, Node node)
    {
        if (columnNumber == 0)
        {
            return node.getRootNode();
        }
        else
        {
            // Create the path from the selected node to the root.
            final List<Node> path = createPathToRoot(node);
            // Find the node with the indentation in the path
            for (Node element : path)
            {
                if (element instanceof KeyValueNode)
                {
                    if (element.getStartPosition().getColumn() < columnNumber)
                    {
                        // In an object we need that it should be minor for arrays
                        node = ((KeyValueNode) element).getValue();
                    }
                }
                else if (element instanceof ObjectNode)
                {
                    // In an object we need that it should be equals or minor
                    if (element.getStartPosition().getColumn() <= columnNumber)
                    {
                        node = element;
                    }
                }
            }
            return node;
        }
    }

    @Nonnull
    private List<Node> createPathToRoot(Node node)
    {
        final List<Node> path = new ArrayList<>();
        Node parent = node;
        while (parent != null)
        {
            path.add(0, parent);
            parent = parent.getParent();
        }
        return path;
    }

    @Nonnull
    private String getFooter(String document, int offset)
    {
        int loc = offset;
        char current = document.charAt(loc);
        while (loc < document.length() - 1 && current != '\n')
        {
            loc++;
            current = document.charAt(loc);
        }

        return loc < document.length() ? document.substring(loc) : "";
    }

    private int getColumnNumber(String document, int offset)
    {
        final StringBuilder contextLine = getContextLine(document, offset);
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

    @Nonnull
    private StringBuilder getContextLine(String document, int offset)
    {
        final StringBuilder contextLine = new StringBuilder();

        int location = offset;
        char character = document.charAt(location);
        while (location > 0 && character != '\n')
        {
            location--;
            contextLine.append(character);
            character = document.charAt(location);

        }
        return contextLine;
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
                    if (child.getChildren().isEmpty())
                    {
                        return child;
                    }
                    else
                    {
                        return searchNodeAt(child, location);
                    }
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
