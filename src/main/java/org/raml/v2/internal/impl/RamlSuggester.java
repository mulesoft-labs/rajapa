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
package org.raml.v2.internal.impl;


import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;
import org.raml.v2.internal.framework.grammar.rule.Rule;
import org.raml.v2.internal.impl.commons.RamlHeader;
import org.raml.v2.internal.impl.commons.RamlVersion;
import org.raml.v2.internal.impl.v08.grammar.Raml08Grammar;
import org.raml.v2.internal.impl.v10.grammar.Raml10Grammar;
import org.raml.v2.api.loader.DefaultResourceLoader;
import org.raml.v2.api.loader.ResourceLoader;
import org.raml.v2.internal.framework.nodes.*;
import org.raml.v2.suggester.*;
import org.raml.v2.internal.utils.Inflector;

public class RamlSuggester
{

    private ResourceLoader resourceLoader;

    public RamlSuggester(ResourceLoader resourceLoader)
    {
        this.resourceLoader = resourceLoader;
    }

    public RamlSuggester()
    {
        this(new DefaultResourceLoader());
    }

    /**
     * Returns the suggestions for the specified document at the given position.
     * In most common cases the offset will be the cursor position.
     * @param document The raml document
     * @param offset The offset from the begging of the document
     * @return The suggestions
     */
    public Suggestions suggestions(String document, int offset)
    {
        final List<Suggestion> result = new ArrayList<>();
        final RamlParsingContext ramlParsingContext = getContext(document, offset);
        final int location = ramlParsingContext.getLocation();
        final String content = ramlParsingContext.getContent();
        final List<Suggestion> suggestions = getSuggestions(ramlParsingContext, document, offset, location);
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
        Collections.sort(result);
        return new Suggestions(result, content, location);

    }

    private List<Suggestion> getSuggestions(RamlParsingContext context, String document, int offset, int location)
    {
        switch (context.getContextType())
        {
        case FUNCTION_CALL:
            return getFunctionCallSuggestions();
        case STRING_TEMPLATE:
            return getTemplateParameterSuggestions(document, offset, location);
        case LIBRARY_CALL:
        case ITEM:
        case VALUE:
            return getSuggestionsAt(context, document, offset, location);
        default:
            return getSuggestionByColumn(context, document, offset, location);

        }
    }

    @Nonnull
    private List<Suggestion> getTemplateParameterSuggestions(String document, int offset, int location)
    {
        final Node rootNode = getRootNode(document, offset, location);
        Node node = searchNodeAt(rootNode, location);
        boolean inTrait = false;
        while (node != null)
        {

            if (node instanceof KeyValueNode)
            {
                if (((KeyValueNode) node).getKey() instanceof StringNode)
                {
                    final String value = ((StringNode) ((KeyValueNode) node).getKey()).getValue();
                    if (value.equals("traits") || value.equals("resourceTypes"))
                    {
                        inTrait = value.equals("traits");
                        break;
                    }
                }
            }
            node = node.getParent();
        }
        return inTrait ? defaultTraitParameters() : defaultResourceTypeParameters();
    }

    @Nonnull
    private List<Suggestion> defaultTraitParameters()
    {
        final List<Suggestion> suggestions = defaultResourceTypeParameters();
        suggestions.add(new DefaultSuggestion("methodName", "The name of the method", ""));
        return suggestions;
    }

    @Nonnull
    private List<Suggestion> defaultResourceTypeParameters()
    {
        final List<Suggestion> suggestions = new ArrayList<>();
        suggestions.add(new DefaultSuggestion("resourcePath", "The resource's full URI relative to the baseUri (if any)", ""));
        suggestions.add(new DefaultSuggestion("resourcePathName", "The rightmost path fragment of the resource's relative URI, " +
                                                                  "omitting any parametrize brackets (\"{\" and \"}\")", ""));
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

    private List<Suggestion> getSuggestionsAt(RamlParsingContext context, String document, int offset, int location)
    {
        final Node root = getRootNode(document, offset, location);
        Node node = searchNodeAt(root, location);
        if (node != null)
        {
            // If it is the key of a key value pair
            if (node.getParent() instanceof KeyValueNode && node.getParent().getChildren().indexOf(node) == 0)
            {
                node = node.getParent().getParent();
            }
            // Recreate path with the node at the correct indentation
            final List<Node> pathToRoot = createPathToRoot(node);
            // Follow the path from the root to the node and apply the rules for auto-completion.
            final Rule rootRule = getRuleFor(document);
            return rootRule != null ? rootRule.getSuggestions(pathToRoot, context) : Collections.<Suggestion> emptyList();
        }
        else
        {
            return Collections.emptyList();
        }
    }

    private Node getRootNode(String document, int offset, int location)
    {
        // We only run the first phase
        final RamlBuilder ramlBuilder = new RamlBuilder(RamlBuilder.FIRST_PHASE);
        try
        {
            // We try the with the original document
            final Node rootNode = ramlBuilder.build(document, resourceLoader, "");
            if (rootNode instanceof StringNode)
            {
                // File still doesn't have any mapping and will not generate any suggestions so we'll force the parsing to make it a mapping
                return ramlBuilder.build(stripLastChanges(document, offset, location) + "\n\nstub: stub", resourceLoader, ""); // we add an invalid key so as to force the creation of the root node
            }
            else if (!(rootNode instanceof ErrorNode))
            {
                return rootNode;
            }
            else if (rootNode instanceof EmptyErrorNode)
            {
                // File is not corrupted but just empty, we should suggest initial keys for the current file
                return ramlBuilder.build(document + "\n\nstub: stub", resourceLoader, ""); // we add an invalid key so as to force the creation of the root node
            }
            else
            {
                // We remove some current keywords to see if it parses
                return ramlBuilder.build(stripLastChanges(document, offset, location), resourceLoader, "");
            }
        }
        catch (final Exception e)
        {
            // We remove some current keywords to see if it parses
            return ramlBuilder.build(stripLastChanges(document, offset, location), resourceLoader, "");
        }
    }

    private String stripLastChanges(String document, int offset, int location)
    {
        final String header = document.substring(0, location + 1);
        final String footer = getFooter(document, offset);
        final String realDocument = header + footer;

        return realDocument;
    }

    private List<Suggestion> getSuggestionByColumn(RamlParsingContext context, String document, int offset, int location)
    {
        if (StringUtils.isBlank(document) && offset == -1)
        {
            return getHeaderSuggestions();
        }

        // // I don't care column number unless is an empty new line
        int columnNumber = getColumnNumber(document, offset);
        final Node root = getRootNode(document, offset, location);
        Node node = searchNodeAt(root, location);

        if (node != null)
        {
            node = getValueNodeAtColumn(columnNumber, node);
            // Recreate path with the node at the correct indentation
            final List<Node> pathToRoot = createPathToRoot(node);

            // Follow the path from the root to the node and apply the rules for auto-completion.
            final Rule rootRule = getRuleFor(document);
            return rootRule != null ? rootRule.getSuggestions(pathToRoot, context) : Collections.<Suggestion> emptyList();
        }
        else
        {
            return Collections.emptyList();
        }
    }

    private List<Suggestion> getHeaderSuggestions()
    {
        return Arrays.asList(
                (Suggestion) new DefaultSuggestion("#%RAML 1.0", "RAML 1.0 root file header", DefaultSuggestion.RAML_1_0_HEADER),
                new DefaultSuggestion("#%RAML 1.0 DocumentationItem", "An item in the collection of items that is the value of the root-level documentation property",
                        "RAML 1.0 Documentation Item fragment"),
                new DefaultSuggestion("#%RAML 1.0 DataType", "A data type declaration where the type property may be used", "RAML 1.0 Data Type fragment"),
                new DefaultSuggestion("#%RAML 1.0 NamedExample", "A property of the examples property, whose key is a name of an example and whose value describes the example",
                        "RAML 1.0 Named Example fragment"),
                new DefaultSuggestion("#%RAML 1.0 ResourceType", "A single resource type declaration", "RAML 1.0 Resource Type fragment"),
                new DefaultSuggestion("#%RAML 1.0 Trait", "A single trait declaration", "RAML 1.0 Trait fragment"),
                new DefaultSuggestion("#%RAML 1.0 AnnotationTypeDeclaration", "A single annotation type declaration", "RAML 1.0 Annotation Type Declaration fragment"),
                new DefaultSuggestion("#%RAML 1.0 Library", "A RAML library", "RAML 1.0 Library fragment"),
                new DefaultSuggestion("#%RAML 1.0 Overlay", "An overlay file", "RAML 1.0 Overlay fragment"),
                new DefaultSuggestion("#%RAML 1.0 Extension", "An extension file", "RAML 1.0 Extension fragment"),
                new DefaultSuggestion("#%RAML 1.0 SecurityScheme", "A definition of a security scheme", "RAML 1.0 Security Scheme fragment"));
    }

    @Nonnull
    private RamlParsingContext getContext(String document, int offset)
    {
        RamlParsingContext context = null;
        int location = offset;
        final StringBuilder content = new StringBuilder();
        while (location >= 0 && context == null)
        {
            char character = document.charAt(location);
            switch (character)
            {
            case ':':
                context = new RamlParsingContext(RamlParsingContextType.VALUE, revertAndTrim(content), location + 1);
                break;
            case ',':
            case '[':
            case '{':
            case '-':
                context = new RamlParsingContext(RamlParsingContextType.ITEM, revertAndTrim(content), location);
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
                            context = new RamlParsingContext(RamlParsingContextType.FUNCTION_CALL, split[split.length - 1].trim(), location);
                        }
                        else if (contextContent.endsWith("|"))
                        {
                            context = new RamlParsingContext(RamlParsingContextType.FUNCTION_CALL, "", location);
                        }
                        else
                        {
                            context = new RamlParsingContext(RamlParsingContextType.STRING_TEMPLATE, contextContent, location);
                        }
                        break;
                    }
                }
                content.append(character);
                break;
            case '.':
                context = new RamlParsingContext(RamlParsingContextType.LIBRARY_CALL, revertAndTrim(content), location);
                break;
            case '\n':
                context = new RamlParsingContext(RamlParsingContextType.ANY, revertAndTrim(content), location);
                break;
            default:
                content.append(character);
            }
            location--;
        }

        if (context == null)
        {
            context = new RamlParsingContext(RamlParsingContextType.ANY, revertAndTrim(content), location);
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
        while (loc < document.length() && current != '\n' && current != '}' && current != ']' && current != ',')
        {
            loc++;
            if (loc == document.length())
            {
                break;
            }
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
        return contextLine.reverse();
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
                else if (child.getEndPosition().getIndex() > location || isLastNode(child))
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

    private boolean isLastNode(Node node)
    {
        final Node parent = node.getParent();
        if (parent == null)
        {
            return false;
        }
        List<Node> children = parent.getChildren();
        Node lastChild = children.get(children.size() - 1);
        return node.equals(lastChild);
    }


    @Nullable
    public Rule getRuleFor(String stringContent)
    {
        try
        {
            RamlHeader ramlHeader = RamlHeader.parse(stringContent);
            if (RamlVersion.RAML_08 == ramlHeader.getVersion())
            {
                return new Raml08Grammar().raml();
            }
            if (ramlHeader.getFragment() != null)
            {
                return ramlHeader.getFragment().getRule(new Raml10Grammar());
            }
        }
        catch (RamlHeader.InvalidHeaderException e)
        {
            // ignore, just return null
        }
        return null;
    }

}
