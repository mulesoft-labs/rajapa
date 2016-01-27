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
/*
 *
 */
package org.raml.grammar.rule;


import org.raml.nodes.Node;
import org.raml.nodes.StringNode;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexValueRule extends Rule
{

    private Pattern value;

    public RegexValueRule(Pattern value)
    {
        this.value = value;
    }

    @Override
    public boolean matches(Node node)
    {
        return node instanceof StringNode && getMatcher((StringNode) node).matches();
    }

    private Matcher getMatcher(StringNode node)
    {
        return value.matcher(node.getValue());
    }

    @Override
    public Node transform(Node node)
    {
        if (getFactory() != null)
        {
            final Matcher matcher = getMatcher((StringNode) node);
            final int i = matcher.groupCount();
            final List<String> groups = new ArrayList<>();
            for (int j = 0; j < i; j++)
            {
                final String group = matcher.group(j);
                groups.add(group);
            }
            return getFactory().create(groups.toArray(new String[groups.size()]));
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
