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

import org.apache.commons.lang.StringUtils;
import org.raml.nodes.ErrorNode;
import org.raml.nodes.Node;
import org.raml.nodes.StringNode;

import java.util.Collection;

public class ErrorNodeFactory
{

    public static ErrorNode createUnexpectedKey(Node key, Collection<String> options)
    {
        return new ErrorNode("Unexpected key '" + getLabel(key) + "'. Options are : " + StringUtils.join(options, " or "));
    }

    private static String getLabel(Node key)
    {
        if (key instanceof StringNode)
        {
            return ((StringNode) key).getValue();
        }
        return key.getClass().getSimpleName();
    }

    public static ErrorNode createInvalidElement(Node child)
    {
        return new ErrorNode("Invalid array element " + child.getClass().getSimpleName() + ".");
    }

    public static ErrorNode createInvalidRootElement(Node rootNode, String expected)
    {
        return new ErrorNode("Invalid root node " + rootNode.getClass().getSimpleName() + ". Expected : " + expected + ".");
    }

    public static ErrorNode createInvalidTemplateFunctionExpression(Node node, String token)
    {
        return new ErrorNode("Invalid template function expression " + token);
    }

    public static ErrorNode createInvalidTemplateParameterExpression(Node node, String token)
    {
        return new ErrorNode("Can not resolver parameter " + token);
    }
}
