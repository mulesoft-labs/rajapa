package org.raml.nodes;

public class RamlErrorNode extends RamlAbstractNode
{

    private final String errorMessage;

    public RamlErrorNode(String msg)
    {
        super(null);
        this.errorMessage = msg;
    }
}
