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

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import org.raml.nodes.KeyValueNode;
import org.raml.nodes.Node;
import org.raml.nodes.NodeType;
import org.raml.nodes.ObjectNode;
import org.raml.suggester.Suggestion;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ObjectRule extends Rule
{
    private List<KeyValueRule> fields;
    private ConditionalRules conditionalRules;

    public ObjectRule()
    {
        this.fields = new ArrayList<>();
    }

    @Nonnull
    @Override
    public List<Suggestion> getSuggestions(Node node)
    {
        List<Suggestion> result = new ArrayList<>();
        final List<KeyValueRule> fieldRules = getAllFieldRules(node);
        for (KeyValueRule rule : fieldRules)
        {
            if (rule.repeated() || !matchesAny(rule, node.getChildren()))
            {
                // We return the suggestions of the key
                result.addAll(rule.getKeySuggestions(node));
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
        return node instanceof ObjectNode;
    }

    @Override
    public Node transform(@Nonnull Node node)
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
            final List<KeyValueRule> requiredRules = new ArrayList<>();
            for (KeyValueRule fieldRule : allFieldRules)
            {
                if (fieldRule.isRequired())
                {
                    requiredRules.add(fieldRule);
                }
            }

            for (Node child : children)
            {
                final Rule matchingRule = findMatchingRule(allFieldRules, child);
                if (matchingRule != null)
                {
                    requiredRules.remove(matchingRule);
                    final Node newChild = matchingRule.transform(child);
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
                    child.replaceWith(ErrorNodeFactory.createUnexpectedKey(((KeyValueNode) child).getKey(), options));
                }
            }

            if (!requiredRules.isEmpty())
            {
                for (KeyValueRule requiredRule : requiredRules)
                {
                    result.addChild(ErrorNodeFactory.createRequiredValueNotFound(node, requiredRule.getKeyRule()));
                }
            }

            return result;
        }
    }

    protected Node getResult(Node node)
    {
        Node result = node;
        if (getFactory() != null)
        {
            result = getFactory().create(node);
        }
        return result;
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


    public ObjectRule with(KeyValueRule field)
    {
        this.fields.add(field);
        return this;
    }

    @Override
    public List<Suggestion> getSuggestions(List<Node> pathToRoot)
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
                return getSuggestions(mappingNode);
            default:
                final Node node = pathToRoot.get(1);
                final Rule matchingRule = findMatchingRule(getAllFieldRules(mappingNode), node);
                return matchingRule == null ? Collections.<Suggestion> emptyList() : matchingRule.getSuggestions(pathToRoot.subList(1, pathToRoot.size()));
            }
        }
    }

    @Override
    public String getDescription()
    {
        return "Mapping";
    }

    public ObjectRule with(ConditionalRules conditional)
    {
        this.conditionalRules = conditional;
        return this;
    }
}
