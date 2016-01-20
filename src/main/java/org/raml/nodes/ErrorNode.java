package org.raml.nodes;

import org.raml.nodes.impl.AbstractRamlNode;

public class ErrorNode extends AbstractRamlNode
{
    private final String errorMessage;

    public ErrorNode(String msg)
    {
        this.errorMessage = msg;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

}
