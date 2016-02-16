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

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import org.raml.nodes.BooleanNode;
import org.raml.nodes.Node;
import org.raml.nodes.NodeType;
import org.raml.suggester.DefaultSuggestion;
import org.raml.suggester.Suggestion;

public class BooleanTypeRule extends AbstractTypeRule
{
    @Nonnull
    @Override
    public List<Suggestion> getSuggestions(Node node)
    {
        return Arrays.<Suggestion> asList(new DefaultSuggestion("true", "Boolean true", ""), new DefaultSuggestion("false", "Boolean false", ""));
    }

    @Override
    public boolean matches(@Nonnull Node node)
    {
        return node instanceof BooleanNode;
    }


    @Override
    public String getDescription()
    {
        return "Boolean";
    }

    @Nonnull
    @Override
    NodeType getType()
    {
        return NodeType.Boolean;
    }
}
