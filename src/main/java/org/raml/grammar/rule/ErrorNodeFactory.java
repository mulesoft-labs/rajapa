/*
 *
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
