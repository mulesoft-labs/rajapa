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

import java.io.IOException;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.raml.nodes.ErrorNode;
import org.raml.nodes.Node;
import org.raml.nodes.NodeType;

public class ErrorNodeFactory
{

    public static ErrorNode createUnexpectedKey(Node key, Collection<String> options)
    {
        return new ErrorNode("Unexpected key '" + key + "'. Options are : " + StringUtils.join(options, " or "));
    }

    public static ErrorNode createInvalidArrayElement(Node child)
    {
        return new ErrorNode("Invalid array element " + child + ".");
    }

    public static ErrorNode createInvalidNode(Node child)
    {
        return new ErrorNode("Invalid element " + child + ".");
    }

    public static ErrorNode createInvalidRootElement(Node rootNode, String expected)
    {
        return new ErrorNode("Invalid root node " + rootNode + ". Expected : " + expected + ".");
    }

    public static ErrorNode createInvalidTemplateFunctionExpression(Node node, String token)
    {
        return new ErrorNode("Invalid template function expression " + token);
    }

    public static ErrorNode createInvalidTemplateParameterExpression(Node node, String token)
    {
        return new ErrorNode("Cannot resolve parameter " + token);
    }

    public static Node createRequiredValueNotFound(Node node, Rule keyRule)
    {
        final ErrorNode errorNode = new ErrorNode("Missing required field " + keyRule);
        errorNode.setSource(node);
        return errorNode;
    }

    public static Node createInvalidType(Node node, NodeType type)
    {
        return new ErrorNode("Invalid type " + node.getType() + ", expected " + type);
    }

    public static Node createInvalidFragmentName(String fragmentText)
    {
        return new ErrorNode("Invalid fragment name '" + fragmentText + "'");
    }

    public static Node createEmptyDocument()
    {
        return new ErrorNode("Empty document.");
    }

    public static Node createUnsupportedVersion(String version)
    {
        return new ErrorNode("Unsupported version " + version + "");
    }

    public static Node createInvalidHeader(String header)
    {
        return new ErrorNode("Invalid header declaration " + header);
    }

    public static Node createInvalidInput(IOException ioe)
    {
        return new ErrorNode("Error while reading the input. Reason " + ioe.getMessage());
    }

    public static Node createInvalidMaxLength(int maxLength)
    {
        return new ErrorNode("Expected max length " + maxLength);
    }

    public static Node createInvalidMinLength(int minLength)
    {
        return new ErrorNode("Expected min length " + minLength);
    }

    public static Node createInvalidMinimumValue(Number minimumValue)
    {
        return new ErrorNode("Expected minimum value " + minimumValue);
    }

    public static Node createInvalidDivisorValue()
    {
        return new ErrorNode("Can not divide by 0");
    }

    public static Node createInvalidMultipleOfValue(Number multipleOfValue)
    {
        return new ErrorNode("Expected a multiple of " + multipleOfValue);
    }

    public static Node createInvalidMaximumValue(Number maximumValue)
    {
        return new ErrorNode("Expected maximum value " + maximumValue);
    }

    public static Node createInvalidRangeValue(Number minimumValue, Number maximumValue)
    {
        return new ErrorNode("Expected number between " + minimumValue + " and " + maximumValue);
    }

    public static Node createMissingField(String selector)
    {
        return new ErrorNode("Missing field " + selector);
    }

    public static Node createMissingAnnotationType(String type)
    {
        return new ErrorNode("Missing Annotation Type '" + type + "'");
    }

    public static Node createInvalidValue(Node node, String expected)
    {
        return new ErrorNode("Invalid value '" + node + "'. Expected " + expected);
    }
}
