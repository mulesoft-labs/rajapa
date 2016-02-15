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
import java.util.Collections;
import java.util.List;

public class ParametrizedNodeReferenceRule extends ObjectRule
{

    private ReferenceSuggester suggester;

    public ParametrizedNodeReferenceRule(String referenceKey)
    {
        suggester = new ReferenceSuggester(referenceKey);
    }

    @Override
    public boolean matches(@Nonnull Node node)
    {
        // It should have at least one key value pair
        return super.matches(node) && hasOneKey(node);
    }

    private boolean hasOneKey(@Nonnull Node node)
    {
        return !node.getChildren().isEmpty() && !node.getChildren().get(0).getChildren().isEmpty();
    }


    @Override
    public List<Suggestion> getSuggestions(List<Node> pathToRoot)
    {
        switch (pathToRoot.size())
        {
        case 1:
            return getSuggestions(pathToRoot.get(0));
        default:
            return Collections.emptyList();
        }

    }

    @Nonnull
    @Override
    public List<Suggestion> getSuggestions(Node node)
    {
        if (hasOneKey(node))
        {
            final Node key = node.getChildren().get(0).getChildren().get(0);
            return suggester.getSuggestions(key);
        }
        else
        {
            return super.getSuggestions(node);
        }
    }

    @Override
    protected Node getResult(Node node)
    {
        if (getFactory() != null)
        {
            // Get the key of the first key value pair
            final String arg = node.getChildren().get(0).getChildren().get(0).toString();
            return getFactory().create(arg);
        }
        else
        {
            return node;
        }
    }

    @Override
    public String getDescription()
    {
        return "Parametrized reference call.";
    }
}
