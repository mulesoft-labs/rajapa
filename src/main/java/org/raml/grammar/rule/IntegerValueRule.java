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

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import org.raml.nodes.IntegerNode;
import org.raml.nodes.Node;
import org.raml.suggester.Suggestion;

public class IntegerValueRule extends Rule
{

    private BigInteger number;

    public IntegerValueRule(BigInteger number)
    {
        this.number = number;
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
            return ((IntegerNode) node).getValue().equals(number.intValue());
        }
        else
        {
            return false;
        }
    }

    @Override
    public Node apply(@Nonnull Node node)
    {
        if (!matches(node))
        {
            return ErrorNodeFactory.createInvalidValue(node, String.valueOf(number));
        }
        else
        {
            return createNodeUsingFactory(node);
        }

    }

    @Override
    public String getDescription()
    {
        return number.toString();
    }
}
