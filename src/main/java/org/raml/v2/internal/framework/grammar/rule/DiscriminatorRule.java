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


import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.suggester.RamlParsingContext;
import org.raml.v2.suggester.Suggestion;

public class DiscriminatorRule extends Rule
{

    private Rule discriminator;
    private Rule delegate;

    public DiscriminatorRule(Rule discriminator, Rule delegate)
    {
        this.discriminator = discriminator;
        this.delegate = delegate;
    }

    @Nonnull
    @Override
    public List<Suggestion> getSuggestions(Node node, RamlParsingContext context)
    {
        List<Node> children = node.getChildren();
        if (children.isEmpty() || discriminator.matches(children.get(0)))
        {
            return delegate.getSuggestions(node, context);
        }

        return Collections.emptyList();
    }

    @Override
    public boolean matches(@Nonnull Node node)
    {
        List<Node> children = node.getChildren();
        boolean matches = delegate.matches(node);
        return matches && (children.isEmpty() || discriminator.matches(children.get(0)));
    }

    @Override
    public Node apply(@Nonnull Node node)
    {
        return delegate.apply(node);
    }

    @Override
    public String getDescription()
    {
        return delegate.getDescription();
    }
}
