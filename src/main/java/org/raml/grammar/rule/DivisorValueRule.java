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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import org.raml.nodes.FloatingNode;
import org.raml.nodes.IntegerNode;
import org.raml.nodes.Node;
import org.raml.nodes.SimpleTypeNode;
import org.raml.suggester.RamlParsingContext;
import org.raml.suggester.Suggestion;

public class DivisorValueRule extends Rule
{

    private Number divisorValue;

    public DivisorValueRule(Number divisorValue)
    {
        this.divisorValue = divisorValue;
    }

    @Nonnull
    @Override
    public List<Suggestion> getSuggestions(Node node, RamlParsingContext context)
    {
        return Collections.emptyList();
    }

    @Override
    public boolean matches(@Nonnull Node node)
    {
        if (node instanceof IntegerNode)
        {
            return divisorValue.intValue() != 0 && ((IntegerNode) node).getValue() % divisorValue.intValue() == 0;
        }
        return node instanceof FloatingNode && divisorValue.floatValue() != 0f &&
               BigDecimal.ZERO.equals(((FloatingNode) node).getValue().divide(new BigDecimal(divisorValue.floatValue()), BigDecimal.ROUND_UNNECESSARY));
    }

    @Override
    public Node apply(@Nonnull Node node)
    {
        if (matches(node))
        {
            return createNodeUsingFactory(node, ((SimpleTypeNode) node).getValue());
        }
        else
        {
            if ((node instanceof IntegerNode && divisorValue.intValue() == 0) ||
                node instanceof FloatingNode && divisorValue.floatValue() == 0f)
            {
                ErrorNodeFactory.createInvalidDivisorValue();
            }
            return ErrorNodeFactory.createInvalidMultipleOfValue(divisorValue);
        }
    }

    @Override
    public String getDescription()
    {
        return "Multiple of value";
    }
}
