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
package org.raml.grammar.rule;

import org.raml.nodes.Node;
import org.raml.suggester.Suggestion;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Collections;
import java.util.List;

public abstract class Rule
{

    @Nullable
    private NodeFactory factory;

    protected Rule()
    {

    }

    @Nonnull
    public abstract List<Suggestion> getSuggestions(Node node);

    public abstract boolean matches(@Nonnull Node node);

    /**
     * Applies the rule to the node using the following criteria
     * - if rule does not match returns an ErrorNode and stops processing
     * - if rule matches, applies rules to children
     * - if rule contains a NodeFactory, returns the result of it
     * - else returns node
     * <p/>
     * Only structure rules ({@link ObjectRule}, {@link ArrayRule}, {@link KeyValueRule}) replace child nodes.
     *
     * @param node The current node
     * @return the result of the factory or the current node
     */
    @Nonnull
    public abstract Node apply(@Nonnull Node node);

    public abstract String getDescription();

    @Nullable
    private NodeFactory getFactory()
    {
        return factory;
    }

    @Nonnull
    public Node createNodeUsingFactory(@Nonnull Node currentNode, Object... args)
    {
        if (getFactory() != null)
        {
            Node newNode = getFactory().create(args);
            if (!newNode.getClass().equals(currentNode.getClass()))
            {
                return newNode;
            }
        }
        return currentNode;
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

    public List<Suggestion> getSuggestions(List<Node> pathToRoot)
    {
        if (!pathToRoot.isEmpty())
        {
            return getSuggestions(pathToRoot.get(0));
        }
        else
        {
            return Collections.emptyList();
        }
    }
}
