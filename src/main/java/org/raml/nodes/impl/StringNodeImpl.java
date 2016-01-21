/*
 *
 */
package org.raml.nodes.impl;

import org.raml.nodes.StringNode;

public class StringNodeImpl extends AbstractRamlNode implements StringNode
{

    private String value;

    public StringNodeImpl(String value)
    {
        this.value = value;
    }

    @Override
    public String getValue()
    {
        return value;
    }
}
