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

import org.raml.nodes.KeyValueNode;
import org.raml.nodes.Node;
import org.raml.suggester.Suggestion;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class KeyValueRule extends Rule {

    private final Rule keyRule;
    private final Rule valueRule;
    private String description;

    public KeyValueRule(Rule keyRule, Rule valueRule) {

        this.keyRule = keyRule;
        this.valueRule = valueRule;
    }

    @Nonnull
    @Override
    public List<Suggestion> getSuggestions(Node node) {
        return getValueRule().getSuggestions(node);
    }

    @Nonnull
    public List<Suggestion> getKeySuggestions(Node node) {
        final List<Suggestion> suggestions = getKeyRule().getSuggestions(node);
        if (description != null) {
            List<Suggestion> result = new ArrayList<>();
            for (Suggestion suggestion : suggestions) {
                result.add(suggestion.withDescription(description));
            }
            return result;
        } else {
            return suggestions;
        }
    }

    @Override
    public List<Suggestion> getSuggestions(List<Node> pathToRoot) {
        if (!pathToRoot.isEmpty()) {
            return valueRule.getSuggestions(pathToRoot.subList(1, pathToRoot.size()));
        } else {
            return super.getSuggestions(pathToRoot);
        }
    }


    public KeyValueRule description(String description) {
        this.description = description;
        return this;
    }


    @Override
    public boolean matches(@Nonnull Node node) {
        return node instanceof KeyValueNode && getKeyRule().matches(((KeyValueNode) node).getKey());
    }

    public boolean repeated() {
        return !(getKeyRule() instanceof StringValueRule);
    }

    public Rule getKeyRule() {
        return keyRule;
    }

    public Rule getValueRule() {
        return valueRule;
    }

    public KeyValueRule then(Class<? extends Node> clazz) {
        super.then(clazz);
        return this;
    }

    @Override
    public Node transform(@Nonnull Node node) {
        Node result = node;
        if (getFactory() != null) {
            result = getFactory().create();
        }
        final KeyValueNode keyValueNode = (KeyValueNode) node;
        final Node key = keyValueNode.getKey();
        key.replaceWith(getKeyRule().transform(key));
        final Node value = keyValueNode.getValue();
        value.replaceWith(getValueRule().transform(value));
        return result;
    }

    @Override
    public String getDescription() {
        return getKeyRule().getDescription() + ": " + getValueRule().getDescription();
    }
}
