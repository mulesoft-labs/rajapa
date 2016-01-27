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

public class StringValueRule extends Rule
{

    private String value;

    public StringValueRule(String value)
    {
        this.value = value;
    }

    @Override
    public boolean matches(Node node)
    {
        return node instanceof StringNode && ((StringNode) node).getValue().equals(value);
    }

    @Override
    public Node transform(Node node)
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
