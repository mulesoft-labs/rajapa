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
package org.raml.v2.grammar.rule;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.raml.v2.nodes.DefaultPosition;
import org.raml.v2.nodes.KeyValueNode;
import org.raml.v2.nodes.KeyValueNodeImpl;
import org.raml.v2.nodes.Node;
import org.raml.v2.nodes.NodeType;
import org.raml.v2.nodes.StringNodeImpl;
import org.raml.v2.suggester.RamlParsingContext;
import org.raml.v2.suggester.Suggestion;

public class KeyValueRule extends Rule
{

    private final Rule keyRule;
    private final Rule valueRule;
    private String description;
    private boolean required;

    private DefaultValue defaultValue;

    public KeyValueRule(Rule keyRule, Rule valueRule)
    {

        this.keyRule = keyRule;
        this.valueRule = valueRule;
        this.required = false;
    }

    public KeyValueRule required()
    {
        this.required = true;
        return this;
    }

    @Nonnull
    @Override
    public List<Suggestion> getSuggestions(Node node, RamlParsingContext context)
    {
        return getValueRule().getSuggestions(node, context);
    }

    @Nonnull
    public List<Suggestion> getKeySuggestions(Node node, RamlParsingContext context)
    {
        final List<Suggestion> suggestions = getKeyRule().getSuggestions(node, context);
        final List<Suggestion> result = new ArrayList<>();
        for (Suggestion suggestion : suggestions)
        {
            Suggestion keySuggest = suggestion;
            if (description != null)
            {
                keySuggest = suggestion.withDescription(description);
            }
            keySuggest = keySuggest.withValue(suggestion.getValue() + ": ");
            result.add(keySuggest);
        }
        return result;
    }

    @Override
    public List<Suggestion> getSuggestions(List<Node> pathToRoot, RamlParsingContext context)
    {
        if (!pathToRoot.isEmpty())
        {
            return valueRule.getSuggestions(pathToRoot.subList(1, pathToRoot.size()), context);
        }
        else
        {
            return super.getSuggestions(pathToRoot, context);
        }
    }


    public KeyValueRule description(String description)
    {
        this.description = description;
        return this;
    }


    @Override
    public boolean matches(@Nonnull Node node)
    {
        return node instanceof KeyValueNode && getKeyRule().matches(((KeyValueNode) node).getKey());
    }

    public boolean repeated()
    {
        return !(getKeyRule() instanceof StringValueRule);
    }

    public Rule getKeyRule()
    {
        return keyRule;
    }

    public Rule getValueRule()
    {
        return valueRule;
    }

    public KeyValueRule then(Class<? extends Node> clazz)
    {
        super.then(clazz);
        return this;
    }

    @Override
    public Node apply(@Nonnull Node node)
    {
        if (!(node instanceof KeyValueNode))
        {
            return ErrorNodeFactory.createInvalidType(node, NodeType.KeyValue);
        }
        else if (!getKeyRule().matches(((KeyValueNode) node).getKey()))
        {
            return getKeyRule().apply(node);
        }
        final Node result = createNodeUsingFactory(node);
        final KeyValueNode keyValueNode = (KeyValueNode) node;
        final Node key = keyValueNode.getKey();
        key.replaceWith(getKeyRule().apply(key));
        final Node value = keyValueNode.getValue();
        value.replaceWith(getValueRule().apply(value));
        return result;
    }

    @Override
    public String getDescription()
    {
        return getKeyRule().getDescription() + ": " + getValueRule().getDescription();
    }

    public boolean isRequired()
    {
        return required;
    }

    @Nonnull
    public KeyValueRule defaultValue(DefaultValue defaultValue)
    {
        this.defaultValue = defaultValue;
        return this;
    }

    public void applyDefault(Node parent)
    {
        if (defaultValue == null)
        {
            return;
        }
        if (!(getKeyRule() instanceof StringValueRule))
        {
            throw new RuntimeException("Key rule " + getKeyRule().getClass().getSimpleName() + " does not support default values");
        }
        Node keyNode = new StringNodeImpl(((StringValueRule) getKeyRule()).getValue());
        Node valueNode = this.defaultValue.getDefaultValue(parent);
        KeyValueNodeImpl newNode = new KeyValueNodeImpl(keyNode, valueNode);
        newNode.setEndPosition(DefaultPosition.emptyPosition());
        newNode.setStartPosition(DefaultPosition.emptyPosition());
        parent.addChild(newNode);
    }

}
