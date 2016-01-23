/*
 *
 */
package org.raml.nodes.impl;

import org.raml.grammar.Raml10Grammar;
import org.raml.nodes.Node;
import org.raml.utils.NodeSelector;

public class SecuritySchemeRefNode extends AbstractReferenceNode
{
    private String name;

    public SecuritySchemeRefNode(String name)
    {
        this.name = name;
    }

    @Override
    public String getRefName()
    {
        return name;
    }

    @Override
    public SecuritySchemeNode getRefNode()
    {
        // We add the .. as the node selector selects the value and we want the key value pair
        final Node resolve = NodeSelector.selectFrom(Raml10Grammar.SECURITY_SCHEMES_KEY_NAME + "/*/" + getRefName() + "/..", getRelativeNode());
        if (resolve instanceof SecuritySchemeNode)
        {
            return (SecuritySchemeNode) resolve;
        }
        else
        {
            return null;
        }
    }
}
