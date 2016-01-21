/*
 *
 */
package org.raml.grammar.rule;

import org.apache.commons.lang.StringUtils;
import org.raml.nodes.ErrorNode;
import org.raml.nodes.Node;

import java.util.Collection;

public class ErrorNodeFactory
{

    public static ErrorNode createUnexpectedKey(Node key, Collection<String> options)
    {
        return new ErrorNode("Unexpected key " + key + ". Options are : " + StringUtils.join(options, " or "));
    }

    public static ErrorNode createInvalidElement(Node child)
    {
        return new ErrorNode("Invalid array element " + child.getClass().getSimpleName() + ".");
    }

    public static ErrorNode createInvalidRootElement(Node rootNode, String expected)
    {
        return new ErrorNode("Invalid root node " + rootNode.getClass().getSimpleName() + ". Expected : " + expected + ".");
    }
}
