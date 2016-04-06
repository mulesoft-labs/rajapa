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

import com.google.common.collect.Range;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.raml.nodes.IntegerNode;
import org.raml.nodes.Node;
import org.raml.nodes.NodeType;
import org.raml.suggester.Suggestion;

public class IntegerTypeRule extends AbstractTypeRule
{

    @Nullable
    private Range<Integer> range;

    public IntegerTypeRule(@Nullable Range<Integer> range)
    {
        this.range = range;
    }

    public IntegerTypeRule()
    {
        this(null);
    }

    @Nonnull
    @Override
    public List<Suggestion> getSuggestions(Node node)
    {
        return Collections.emptyList();
    }


    @Override
    public boolean matches(@Nonnull Node node)
    {
        if (node instanceof IntegerNode)
        {
            if (range != null)
            {
                return range.contains(((IntegerNode) node).getValue());
            }
            else
            {
                return true;
            }
        }
        else
        {
            return false;
        }
    }

    @Override
    public String getDescription()
    {
        return "Integer";
    }

    @Nonnull
    @Override
    NodeType getType()
    {
        return NodeType.Integer;
    }
}
