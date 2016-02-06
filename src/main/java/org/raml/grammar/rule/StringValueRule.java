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

import org.apache.commons.lang.StringUtils;
import org.raml.nodes.Node;
import org.raml.nodes.StringNode;
import org.raml.suggester.DefaultSuggestion;
import org.raml.suggester.Suggestion;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StringValueRule extends Rule
{

    private String value;
    private String description;

    public StringValueRule(String value)
    {
        this.value = value;
    }

    @Nonnull
    @Override
    public List<Suggestion> getSuggestions(Node node)
    {
        return Collections.<Suggestion> singletonList(new DefaultSuggestion(value, description, StringUtils.capitalize(value)));
    }


    @Override
    public boolean matches(@Nonnull Node node)
    {
        return node instanceof StringNode && ((StringNode) node).getValue().equals(value);
    }

    public StringValueRule description(String description)
    {
        this.description = description;
        return this;
    }

    @Override
    public Node transform(@Nonnull Node node)
    {
        if (getFactory() != null)
        {
            return getFactory().create(((StringNode) node).getValue());
        }
        else
        {
            return node;
        }
    }

    @Override
    public String getDescription()
    {
        return "\"" + value + "\"";
    }
}
