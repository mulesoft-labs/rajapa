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

import org.raml.grammar.GrammarContext;
import org.raml.nodes.Node;
import org.raml.suggester.Suggestion;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class ReferenceRule extends Rule {


    private GrammarContext context;
    private final String name;
    private Rule ref;

    public ReferenceRule(GrammarContext context, String name) {
        this.context = context;
        this.name = name;
    }

    public Rule getRef() {
        if (ref == null) {
            final Rule ruleByName = context.getRuleByName(name);
            if (ruleByName != null) {
                ref = ruleByName;
            } else {
                throw new RuntimeException("Invalid grammar rule reference name " + name);
            }
        }
        return ref;
    }


    @Override
    public List<Suggestion> getSuggestions(List<Node> pathToRoot) {
        return getRef().getSuggestions(pathToRoot);
    }

    @Nonnull
    @Override
    public List<Suggestion> getSuggestions(Node node) {
        return Collections.emptyList();
    }


    @Override
    public boolean matches(@Nonnull Node node) {
        return getRef().matches(node);
    }

    @Override
    public Node transform(@Nonnull Node node) {
        return getRef().transform(node);
    }

    @Override
    public String getDescription() {
        return getRef().getDescription();
    }
}
