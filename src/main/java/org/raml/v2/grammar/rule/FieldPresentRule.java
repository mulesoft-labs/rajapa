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

import java.util.List;

import javax.annotation.Nonnull;

import org.raml.nodes.Node;
import org.raml.suggester.RamlParsingContext;
import org.raml.suggester.Suggestion;
import org.raml.utils.NodeSelector;


public class FieldPresentRule extends Rule
{

    private String selector;

    private Rule delegate;

    public FieldPresentRule(String selector, Rule then)
    {
        this.selector = selector;
        this.delegate = then;
    }

    @Nonnull
    @Override
    public List<Suggestion> getSuggestions(Node node, RamlParsingContext context)
    {
        return delegate.getSuggestions(node, context);
    }

    @Override
    public boolean matches(@Nonnull Node node)
    {
        return isPresent(node) && delegate.matches(node);
    }

    private boolean isPresent(@Nonnull Node node)
    {
        return NodeSelector.selectFrom(selector, node) != null;
    }

    @Override
    public Node apply(@Nonnull Node node)
    {
        if (isPresent(node))
        {
            return delegate.apply(node);
        }
        else
        {
            return ErrorNodeFactory.createMissingField(selector);
        }
    }

    @Override
    public String getDescription()
    {
        return delegate.getDescription();
    }
}
