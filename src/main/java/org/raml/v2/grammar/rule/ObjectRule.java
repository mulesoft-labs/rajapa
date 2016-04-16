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

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;
import org.raml.v2.nodes.KeyValueNode;
import org.raml.v2.nodes.Node;
import org.raml.v2.nodes.NodeType;
import org.raml.v2.nodes.ObjectNode;
import org.raml.v2.suggester.RamlParsingContext;
import org.raml.v2.suggester.RamlParsingContextType;
import org.raml.v2.suggester.Suggestion;

public class ObjectRule extends Rule
{
    private List<KeyValueRule> fields;
    private ConditionalRules conditionalRules;

    private boolean strict = false;

    public ObjectRule()
    {
        this.fields = new ArrayList<>();
    }

    public ObjectRule(boolean strict)
    {
        this();
        this.strict = strict;
    }

    @Nonnull
    @Override
    public List<Suggestion> getSuggestions(Node node, RamlParsingContext context)
    {
        List<Suggestion> result = new ArrayList<>();
        final List<KeyValueRule> fieldRules = getAllFieldRules(node);
        for (KeyValueRule rule : fieldRules)
        {
            if (rule.repeated() || !matchesAny(rule, node.getChildren()))
            {
                if (context.getContextType() == RamlParsingContextType.VALUE)
                {
                    final List<Suggestion> keySuggestions = rule.getKeySuggestions(node, context);
                    String prefix = "\n" + StringUtils.repeat(" ", node.getStartPosition().getColumn() + 2);
                    for (Suggestion keySuggestion : keySuggestions)
                    {
                        result.add(keySuggestion.withPrefix(prefix));
                    }
                }
                else
                {
                    // We return the suggestions of the key
                    result.addAll(rule.getKeySuggestions(node, context));
                }
            }
        }
        return result;
    }

    private boolean matchesAny(KeyValueRule rule, List<Node> children)
    {
        for (Node child : children)
        {
            if (rule.matches(child))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean matches(@Nonnull Node node)
    {
        boolean isObjectNode = node instanceof ObjectNode;
        if (!strict)
        {
            return isObjectNode;
        }
        else
        {
            return isObjectNode && allChildrenMatch(node);
        }
    }

    private boolean allChildrenMatch(Node node)
    {
        List<Node> children = node.getChildren();
        final List<KeyValueRule> allFieldRules = getAllFieldRules(node);
        boolean matches = true;
        for (KeyValueRule rule : allFieldRules)
        {
            matches &= !rule.isRequired() || matchesAny(rule, children);
        }
        return matches;
    }

    @Override
    public Node apply(@Nonnull Node node)
    {
        if (!matches(node))
        {
            return ErrorNodeFactory.createInvalidType(node, NodeType.Object);
        }
        else
        {
            Node result = getResult(node);
            final List<Node> children = node.getChildren();
            final List<KeyValueRule> allFieldRules = getAllFieldRules(node);
            final List<KeyValueRule> nonMatchingRules = new ArrayList<>(allFieldRules);

            for (Node child : children)
            {
                final Rule matchingRule = findMatchingRule(allFieldRules, child);
                if (matchingRule != null)
                {
                    nonMatchingRules.remove(matchingRule);
                    final Node newChild = matchingRule.apply(child);
                    child.replaceWith(newChild);
                }
                else
                {
                    final Collection<String> options = Collections2.transform(allFieldRules, new Function<KeyValueRule, String>()
                    {
                        @Override
                        public String apply(KeyValueRule rule)
                        {
                            return rule.getKeyRule().getDescription();
                        }
                    });
                    child.replaceWith(ErrorNodeFactory.createUnexpectedKey(((KeyValueNode) child).getKey(), new TreeSet<>(options)));
                }
            }

            for (KeyValueRule rule : nonMatchingRules)
            {
                if (rule.isRequired())
                {
                    result.addChild(ErrorNodeFactory.createRequiredValueNotFound(node, rule.getKeyRule()));
                }
                else
                {
                    rule.applyDefault(node);
                }
            }

            return result;
        }
    }

    protected Node getResult(Node node)
    {
        return createNodeUsingFactory(node, node);
    }

    private List<KeyValueRule> getAllFieldRules(Node node)
    {
        if (conditionalRules != null)
        {
            final List<KeyValueRule> rulesNode = conditionalRules.getRulesNode(node);
            final ArrayList<KeyValueRule> rules = new ArrayList<>(rulesNode);
            rules.addAll(fields);
            return rules;
        }
        else
        {
            return fields;
        }
    }

    @Nullable
    private Rule findMatchingRule(List<? extends Rule> rootRule, Node node)
    {
        for (Rule rule : rootRule)
        {
            if (rule.matches(node))
            {
                return rule;
            }
        }
        return null;
    }

    @Override
    public ObjectRule then(Class<? extends Node> clazz)
    {
        super.then(clazz);
        return this;
    }

    public ObjectRule with(KeyValueRule field)
    {
        this.fields.add(field);
        return this;
    }

    @Override
    public List<Suggestion> getSuggestions(List<Node> pathToRoot, RamlParsingContext context)
    {
        if (pathToRoot.isEmpty())
        {
            return Collections.emptyList();
        }
        else
        {
            final Node mappingNode = pathToRoot.get(0);
            switch (pathToRoot.size())
            {
            case 1:
                return getSuggestions(mappingNode, context);
            default:
                final Node node = pathToRoot.get(1);
                final Rule matchingRule = findMatchingRule(getAllFieldRules(mappingNode), node);
                return matchingRule == null ? Collections.<Suggestion> emptyList() : matchingRule.getSuggestions(pathToRoot.subList(1, pathToRoot.size()), context);
            }
        }
    }

    @Override
    public String getDescription()
    {
        return "Mapping";
    }

    public boolean isStrict()
    {
        return strict;
    }

    public void setStrict(boolean strict)
    {
        this.strict = strict;
    }

    public ObjectRule with(ConditionalRules conditional)
    {
        this.conditionalRules = conditional;
        return this;
    }

    public ObjectRule merge(ObjectRule objectRule)
    {
        if (objectRule.conditionalRules != null)
        {
            if (this.conditionalRules != null)
            {
                throw new IllegalStateException("Cannot merge conditional rules.");
            }
            this.conditionalRules = objectRule.conditionalRules;
        }
        for (KeyValueRule keyValueRule : objectRule.fields)
        {
            this.fields.add(keyValueRule);
        }
        return this;
    }
}